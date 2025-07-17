package com.kasa.adr.service;

import com.kasa.adr.model.CaseDetails;
import com.kasa.adr.model.Template;
import com.kasa.adr.model.User;
import com.kasa.adr.repo.CaseRepository;
import com.kasa.adr.repo.TemplateRepo;
import com.kasa.adr.service.external.EmailService;
import com.kasa.adr.service.external.MSG91Service;
import com.kasa.adr.service.external.S3Service;
import com.kasa.adr.util.BatchProcessor;
import com.kasa.adr.util.CommonUtils;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationService {
    Logger logger = LoggerFactory.getLogger(NotificationService.class);


    @Autowired
    TemplateRepo templateRepo;

    @Autowired
    EmailService emailService;

    @Autowired
    CaseRepository caseRepository;


    @Autowired
    S3Service s3Service;

    @Autowired
    MSG91Service smsService;

    @Value("${aws.s3.cloudfront.url}")
    private String cloudFrontUrl;


    @Async
    public void sendNotice(List<String> caseIds, String templateId) {
        logger.info("Sending notice for caseIds: {}", caseIds);
        Optional<Template> template = templateRepo.findById(templateId);
        if (template.isPresent()) {

            List<CaseDetails> cases = caseRepository.findAllById(caseIds);
            if (!cases.isEmpty()) {
                BatchProcessor.processInBatches(cases.stream(), 100, 300, batch -> {
                    logger.info("Processing batch: " + batch);
                    batch.stream().forEach(aCase -> {
                        processSingleCase(aCase, template.get());
                    });
                });
            } else {
                logger.warn("No cases found for the provided case IDs.");
            }
        } else {
            logger.error("Template with ID {} not found.", templateId);
        }

    }

    private void processSingleCase(CaseDetails aCase, Template template) {
        Map<String, Object> placeHolderMap = createPlaceHolderMap(aCase);
        String attachmentText = replacePlaceholdersRegex(template.getAttachmentText(), placeHolderMap);
        String emailBody = replacePlaceholdersRegex(template.getEmailBody(), placeHolderMap);
        String emailSubject = template.getEmailSubject();


        String pdfFilePath = CommonUtils.generateUniqueFileName(aCase.getCustomerName())+".pdf";
        Document document = Jsoup.parse(attachmentText, "UTF-8");
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        File attachment = new File(pdfFilePath);
        try (OutputStream os = new FileOutputStream(pdfFilePath)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withUri(pdfFilePath);
            builder.toStream(os);
            builder.withW3cDocument(new W3CDom().fromJsoup(document), "/");
            builder.run();
            s3Service.uploadSingleFile(attachment, "notice");
            if( aCase.getCustomerEmailAddress() != null && !aCase.getCustomerEmailAddress().isEmpty()) {
                emailService.sendEmailWithAttachment(aCase.getCustomerEmailAddress(), emailSubject, emailBody, attachment);
            } else {
                logger.warn("No email address provided for case ID: {}", aCase.getId());
            }
            String url =cloudFrontUrl+"/notice/" + pdfFilePath;
            smsService.sendFirstSMSByTemplateId(aCase,url, template.getSmsTemplateId());
            smsService.sendFirstWhatsAppMsgTemplateId(aCase, url, template.getWhatsAppTemplateId());

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            attachment.delete();
        }


    }

    private  Map<String, Object> createPlaceHolderMap(CaseDetails caseDetails) {
        Map<String, Object> map = new HashMap<>();
        // Customer Info
        map.put("CUSTOMER_ID", caseDetails.getCustomerId());
        map.put("Customer_Name", caseDetails.getCustomerName());
        map.put("Customer_Address", caseDetails.getCustomerAddress());
        map.put("cust_email_address", caseDetails.getCustomerEmailAddress());
        map.put("cust_contact_Number", caseDetails.getCustomerContactNumber());

        // Co-applicant Info
        map.put("coapplicant_name", caseDetails.getCoapplicantName());
        map.put("coapplicant_address", caseDetails.getCoapplicantAddress());
        map.put("coapplicant_email", caseDetails.getCoapplicantEmail());
        map.put("coapplicant_mobile", caseDetails.getCoapplicantMobile());

        // Product Info
        map.put("PRODUCT", caseDetails.getProduct());
        map.put("Asset_Description1", caseDetails.getAssetDescription1());
        map.put("make", caseDetails.getMake());
        map.put("Car_Reg_Number1", caseDetails.getCarRegNumber1());
        map.put("Engine_Number1", caseDetails.getEngineNumber1());
        map.put("Chasis_Number1", caseDetails.getChasisNumber1());

        // Loan 1
        map.put("Tenure_1", caseDetails.getTenure1());
        map.put("Loan_Number_1", caseDetails.getLoanNumber1());
        map.put("Disbursal_Date1", caseDetails.getDisbursalDate1());
        map.put("Amount_Finance_LAN_1", caseDetails.getAmountFinanceLan1());
        map.put("EMI_LAN_1", caseDetails.getEmiLan1());
        map.put("Penalty_Charges_1", caseDetails.getPenaltyCharges1());
        map.put("Bounce_Charges_1", caseDetails.getBounceCharges1());
        map.put("Future_Principal_1", caseDetails.getFuturePrincipal1());

        // Loan 2
        map.put("Loan_Number_2", caseDetails.getLoanNumber2());
        map.put("Disbursal_Date2", caseDetails.getDisbursalDate2());
        map.put("Amount_FinanceLAN_2", caseDetails.getAmountFinanceLan2());
        map.put("EMI_LAN_2", caseDetails.getEmiLan2());
        map.put("Loan2_PenaltyCharges", caseDetails.getLoan2PenaltyCharges());
        map.put("Loan2_BounceCharges", caseDetails.getLoan2BounceCharges());
        map.put("Loan2_FuturePrincipal", caseDetails.getLoan2FuturePrincipal());

        // Legal Info
        map.put("Loan_Recall_Notice_Date", caseDetails.getLoanRecallNoticeDate());
        map.put("LRN_Amount", caseDetails.getLrnAmount());
        map.put("fc_amount", caseDetails.getFcAmount());
        map.put("Installment_Overdue", caseDetails.getInstallmentOverdue());
        map.put("Pending_Emis", caseDetails.getPendingEmis());
        map.put("total_claim_amount", caseDetails.getTotalClaimAmount());
        map.put("Claim_Amount_in_Word", caseDetails.getClaimAmountInWord());
        map.put("Loan_Recall_Notice_Number", caseDetails.getLoanRecallNoticeNumber());

        // Legal Dates
        map.put("KASA_Appointment_Date", caseDetails.getKasaAppointmentDate());
        map.put("Commencement_letter_date", caseDetails.getCommencementLetterDate());
        map.put("Invocation_Ref_No", caseDetails.getInvocationRefNo());
        map.put("Arbitration_Notice_Dispatch_Date", caseDetails.getArbitrationNoticeDispatchDate());
        map.put("Appearance_Date", caseDetails.getAppearanceDate());
        map.put("Statement_of_Account_Date", caseDetails.getStatementOfAccountDate());
        map.put("Interim_Order_Date", caseDetails.getInterimOrderDate());
        map.put("Evidence_date", caseDetails.getEvidenceDate());
        map.put("Award_Date", caseDetails.getAwardDate());

        // Placeholder for customerLoanDetails
        map.put("customerLoanDetails", caseDetails.getCaseUploadDetails());

        // Arbitrator Info
        User arbitrator = caseDetails.getAssignedArbitrator();
        if (arbitrator != null) {
            map.put("arbitrator_name", arbitrator.getName());
            map.put("arbitrator_email", arbitrator.getEmail());
            map.put("arbitrator_mobile", arbitrator.getMobile());
            map.put("arbitrator_Address_line1", arbitrator.getArbitratorProfile().getCorrespondenceAddress().getLine1());
            map.put("arbitrator_Address_line2", arbitrator.getArbitratorProfile().getCorrespondenceAddress().getLine2());
            map.put("arbitrator_Address_pin", arbitrator.getArbitratorProfile().getCorrespondenceAddress().getPin());
            map.put("arbitrator_Address_district", arbitrator.getArbitratorProfile().getCorrespondenceAddress().getDistrict());
            map.put("arbitrator_Address_state", arbitrator.getArbitratorProfile().getCorrespondenceAddress().getState());
            map.put("arbitrator_qualification", arbitrator.getArbitratorProfile().getQualification());
            map.put("arbitrator_experience", arbitrator.getArbitratorProfile().getExperience());
            if( arbitrator.getArbitratorProfile().getSigImageUrl() != null && !arbitrator.getArbitratorProfile().getSigImageUrl().isEmpty()) {
                map.put("arbitrator_sigImageUrl", "<img src=\"" + cloudFrontUrl + "/arbitrator/sigImageUrl/" + arbitrator.getArbitratorProfile().getSigImageUrl() + "\" alt=\"Signature Image\" style=\"width: 200px; height: 150px;\"/>");
            } else {
                map.put("arbitrator_sigImageUrl", "No Signature Image Available");
            }
            map.put("arbitrator_specialization", arbitrator.getArbitratorProfile().getSpecialization());
        }

        // Claimant Info
        User claimant = caseDetails.getClaimantAdmin();
        if (claimant != null) {
            map.put("claimant_name", claimant.getName());
            map.put("claimant_branch", claimant.getInstitutionProfile().getBranch());
            map.put("claimant_authorizedPersonName", claimant.getInstitutionProfile().getAuthorizedPersonName());
            map.put("claimant_designation", claimant.getInstitutionProfile().getDesignation());
            map.put("claimant_email", claimant.getEmail());
            map.put("claimant_mobile", claimant.getMobile());
            map.put("claimant_Address_line1", claimant.getInstitutionProfile().getAddress().getLine1());
            map.put("claimant_Address_line2", claimant.getInstitutionProfile().getAddress().getLine2());
            map.put("claimant_Address_pin", claimant.getInstitutionProfile().getAddress().getPin());

            map.put("claimant_Address_district", claimant.getInstitutionProfile().getAddress().getDistrict());
            map.put("claimant_Address_state", claimant.getInstitutionProfile().getAddress().getState());
        }

        // System-generated Dates
        LocalDate today = LocalDate.now();
        map.put("today_date", today);
        map.put("today_date_plus_1", today.plusDays(1));
        return map;
    }

    public static String replacePlaceholdersRegex(String text, Map<String, Object> replacementMap) {
        if (text == null || replacementMap == null) {
            return text;
        }

        // Pattern to match {{KEY}} format
        Pattern pattern = Pattern.compile("\\{\\{([^}]+)\\}\\}");
        Matcher matcher = pattern.matcher(text);

        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = replacementMap.get(key);
            String replacement = value != null ? value.toString() : matcher.group(0); // Keep original if not found
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(result);
        return result.toString();
    }
}
