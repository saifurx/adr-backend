//package com.kasa.adr.service;
//
//import com.kasa.adr.dto.TemplateMapObject;
//import com.kasa.adr.model.Case;
//import com.kasa.adr.model.CaseHistory;
//import com.kasa.adr.model.Template;
//import com.kasa.adr.repo.CaseRepository;
//import com.kasa.adr.repo.TemplateRepo;
//import com.kasa.adr.service.external.EmailService;
//import com.kasa.adr.service.external.MSG91Service;
//import com.kasa.adr.service.external.S3Service;
//import com.kasa.adr.util.CommonUtils;
//import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
//import org.jsoup.Jsoup;
//import org.jsoup.helper.W3CDom;
//import org.jsoup.nodes.Document;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.OutputStream;
//import java.text.SimpleDateFormat;
//import java.time.Instant;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class VenkyNotificationService {
//    Logger logger = LoggerFactory.getLogger(VenkyNotificationService.class);
//    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
//
//    @Autowired
//    CaseRepository caseRepository;
//    @Autowired
//    TemplateRepo templateRepo;
//    @Autowired
//    EmailService emailService;
//    @Autowired
//    S3Service s3Service;
//    @Autowired
//    MSG91Service smsService;
//    @Value("${frontend.url}")
//    private String frontendUrl;
//
//    public void sendNotice(List<Case> cases, String sequence) {
//
//        logger.info("Sending Notice for sequence " + sequence);
//        Optional<Template> templateOptional;
//        if (sequence.equalsIgnoreCase("first")) {
//            templateOptional = templateRepo.findById("67feb6747d3107230524aa3c");
//        } else if (sequence.equalsIgnoreCase("second")) {
//            templateOptional = templateRepo.findById("68049811f44e766c2c41e99c");
//        } else {
//            templateOptional = Optional.of(null);
//        }
//
//        if (templateOptional.isPresent()) {
//            Template template = templateOptional.get();
//            logger.info("Template {}", template.toString());
//            for (int i = 0; i < cases.size(); i++) {
//                processACase(cases.get(i), template, sequence);
//            }
//        } else {
//            logger.error("Template Not Found");
//        }
//
//    }
//
//    private void processACase(Case aCase, Template template, String sequence) {
//        File attachment = createPDF(aCase, template);
//        List<CaseHistory> history = aCase.getHistory();
//        logger.info("case history: " + history);
//        if (aCase.getEmail() != null) {
//            sendEMail(aCase, attachment);
//
//        }
//        if (aCase.getMobile() != null) {
//            sendSMS(aCase, attachment);
//
//        }
//
//        attachment.deleteOnExit();
//
//    }
//
//    private void sendEMail(Case aCase, File attachment) {
//        Template emailTemplate = templateRepo.findById("6804f1ae6c34226803551031").get();
//        String to = aCase.getEmail();
//        TemplateMapObject templateMapObject = CommonUtils.getTemplateMapObject(aCase);
//        try {
//            String link = frontendUrl + "/assign-random-arbitrator?token=" + aCase.getId();// + CommonUtils.createBase64encodeToken(aCase.getId());
//            String emailTemplateText = emailTemplate.getText();
//            emailTemplateText = emailTemplateText.replace("{{chooseArbitratorLink}}", link);
//            emailTemplateText = CommonUtils.replacePlaceholders(emailTemplateText, templateMapObject);
//            logger.info("Email body : " + emailTemplateText);
//            logger.info("Email attachemnt : " + emailTemplateText);
//            emailService.sendEmailWithAttachment(to, emailTemplate.getSubject(), emailTemplateText, attachment);
//
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//
//
//    }
//
//    private void sendSMS(Case aCase, File attachment) {
//        logger.info("file name: " + attachment.getName());
//        String url = frontendUrl + "/pdf?x=" + attachment.getName();
//        smsService.sendSMS(aCase.getMobile(), aCase.getName(), aCase.getLRNAmount(), url);
//    }
//
//
//    private File createPDF(Case aCase, Template template) {
//        File attachment = null;
//        LocalDate date = LocalDate.now();
//        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d/MMM/uuuu");
//        String today = date.format(formatters);
//        try {
//            TemplateMapObject templateMapObject = CommonUtils.getTemplateMapObject(aCase);
//            String pdfTemplateStr = CommonUtils.replacePlaceholders(template.getText(), templateMapObject);
//            //  logger.info("map values {}", pdfTemplateStr.contains("#current_date"));
//            // logger.info("map values {}", today);
//            String pdfTemplateStr1 = pdfTemplateStr.replace("#current_date", today);
//
//            Document document = Jsoup.parse(pdfTemplateStr1, "UTF-8");
//            document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
//            String pdfFilePath = Instant.now().toEpochMilli() + ".pdf";
//            attachment = new File(pdfFilePath);
//            OutputStream os = new FileOutputStream(pdfFilePath);
//            PdfRendererBuilder builder = new PdfRendererBuilder();
//            builder.withUri(pdfFilePath);
//            builder.toStream(os);
//            builder.withW3cDocument(new W3CDom().fromJsoup(document), "/");
//            builder.run();
//            s3Service.uploadSingleFile(attachment, "notice");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return attachment;
//    }
//}
