package com.kasa.adr.service;

import com.kasa.adr.dto.TemplateMapObject;
import com.kasa.adr.dto.TemplateName;
import com.kasa.adr.model.Case;
import com.kasa.adr.model.Template;
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
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

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


    public void fistDefaulterNotification(List<Case> cases, String claimantId) {
        logger.info("Sending Common Notice claimant id=" + claimantId + " TemplateName.Common_Notice.name()" + TemplateName.Commencement_Letter.name());
        List<Template> pdfTemplate = templateRepo.findByNameAndType(TemplateName.Commencement_Letter.name(), "pdf");
        List<Template> emailTemplate = templateRepo.findByNameAndType(TemplateName.Commencement_Letter.name(), "email");

        logger.info("PDF Template {}", pdfTemplate.size());
        String emailText = "Dear {{defaulter.name}}";
        if (!pdfTemplate.isEmpty() && !emailTemplate.isEmpty()) {
            BatchProcessor.processInBatches(cases.stream(), 100, 300, batch -> {
                logger.info("Processing batch: " + batch);
                batch.stream().forEach(aCase -> {
                  //  processACase(aCase, emailTemplate.get(0).getText(), emailTemplate.get(0).getSubject(), pdfTemplate.get(0).getText());


                });
            });
        }
    }

    private void processACase(Case aCase, String htmlTemplateTxt, String emailSubject, String pdfTemplateTxt) {
        logger.info("Processing aCase {}", aCase);
        //Create Notice PDF from template
        //Create Email Template
        String link = "https://app.virturesolve360.com/assign-random-arbitrator?token=" + aCase.getId();// + CommonUtils.createBase64encodeToken(aCase.getId());
        htmlTemplateTxt = htmlTemplateTxt.replace("{{chooseArbitratorLink}}", link);
        File attachment = null;
        try {
            TemplateMapObject templateMapObject = CommonUtils.getTemplateMapObject(aCase);
            String pdfTemplateStr = CommonUtils.replacePlaceholders(pdfTemplateTxt, templateMapObject);
            String bodyTemplate = CommonUtils.replacePlaceholders(htmlTemplateTxt, templateMapObject);
            //  File inputHTML = new File(pdfTemplateStr);
            Document document = Jsoup.parse(pdfTemplateStr, "UTF-8");
            document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
            String pdfFilePath = aCase.getCustomerId() + "_commencement_Letter.pdf";
            attachment = new File(pdfFilePath);
            try (OutputStream os = new FileOutputStream(pdfFilePath)) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.withUri(pdfFilePath);
                builder.toStream(os);
                builder.withW3cDocument(new W3CDom().fromJsoup(document), "/");
                builder.run();
            }
            String to = aCase.getEmail();

            emailService.sendEmailWithAttachment(to, emailSubject, bodyTemplate, attachment);
            //store in s3
            //update casehistory

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            attachment.delete();
        }
    }

    public void vFirstSMSNotification(List<Case> cases, String claimantId) {
        logger.info("Preparing sms notification pdf");
        String templateId = "67feb6747d3107230524aa3c";
        Optional<Template> templateRepoById = templateRepo.findById(templateId);
        if (!templateRepoById.isEmpty()) {
       //     String pdfStr = templateRepoById.get()//.getText();
            BatchProcessor.processInBatches(cases.stream(), 100, 300, batch -> {
                logger.info("Processing batch: " + batch);
                batch.stream().forEach(aCase -> {
              //      processACaseSMS(aCase, pdfStr);


                });
            });
        }

    }

    private void processACaseSMS(Case aCase, String pdfStr) {
        File attachment = null;
        try {
            TemplateMapObject templateMapObject = CommonUtils.getTemplateMapObject(aCase);
            String pdfTemplateStr = CommonUtils.replacePlaceholders(pdfStr, templateMapObject);
            Document document = Jsoup.parse(pdfTemplateStr, "UTF-8");
            document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
            String pdfFilePath = aCase.getCustomerId() + "_arbitration_notice.pdf";
            attachment = new File(pdfFilePath);
            try (OutputStream os = new FileOutputStream(pdfFilePath)) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.withUri(pdfFilePath);
                builder.toStream(os);
                builder.withW3cDocument(new W3CDom().fromJsoup(document), "/");
                builder.run();
            }
            s3Service.uploadSingleFile(attachment, "notice");
            logger.info("file name: " + pdfFilePath);
            String url = "https://virturesolve360.com/pdf?x=" + aCase.getCustomerId();
            smsService.sendSMS(aCase.getMobile(), aCase.getName(), aCase.getLRNAmount(), url);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            attachment.delete();
        }
    }
}
