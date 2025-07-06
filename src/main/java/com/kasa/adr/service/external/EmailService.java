package com.kasa.adr.service.external;


import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.cryptacular.io.ClassPathResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;


    public boolean sendEmailWithHtmlAndPdfAttachment(
            String to,
            String subject,
            String htmlContent,
            File pdfAttachmentFile,
            String pdfAttachmentName) {

        try {
            // Create a MIME message
            MimeMessage message = mailSender.createMimeMessage();

            // Use MimeMessageHelper to set various email attributes
            // true parameter indicates we're creating a multipart message (for attachments)
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Set the basic email properties
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indicates HTML content

            // Add the PDF attachment
            FileSystemResource file = new FileSystemResource(pdfAttachmentFile);
            helper.addAttachment(pdfAttachmentName, file);

            // Send the email
            mailSender.send(message);

            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(new InternetAddress("noreply@virturesolve360.com"));
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject(subject);
        message.setContent(body, "text/html; charset=utf-8");
        try {
            mailSender.send(message);
            System.out.println("Email sent sucessfully!");
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    public void sendEmailWithAttachment(String to, String sub, String body, File attachment) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            message.setFrom(new InternetAddress("noreply@virturesolve360.com"));
            message.setRecipients(MimeMessage.RecipientType.TO, to);
            message.setSubject(sub);
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);
            messageBodyPart.setContent(body, "text/html; charset=utf-8"); // This sets HTML content
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(attachment);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);
            message.setContent(multipart);
            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (attachment != null && attachment.exists()) {
                // Delete the attachment file after sending the email
                if (!attachment.delete()) {
                    System.out.println("Failed to delete attachment: " + attachment.getAbsolutePath());
                }
            }
        }
    }

    //@PostConstruct
    public void sendStartupMail() throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom(new InternetAddress("noreply@virturesolve360.com"));
        message.setRecipients(MimeMessage.RecipientType.TO, "saifur.x@gmail.com");
        message.setSubject("Backend Service Started for: virturesolve360.com");

        InputStream inputStream = new ClassPathResource("templates/arbitrator-registration.html").getInputStream();
        String htmlTemplate = "";
        try { // Read the HTML template into a String variable

            Path tempFile =
                    Files.createTempDirectory("").resolve(UUID.randomUUID().toString() + ".tmp");

            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            htmlTemplate = new String(Files.readAllBytes(tempFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // Replace placeholders in the HTML template with dynamic values
        htmlTemplate = htmlTemplate.replace("${name}", "Admin");
        htmlTemplate = htmlTemplate.replace("${message}", "Hello, this is a test email. your backend service is running now!");

        // Set the email's content to be the HTML template
        message.setContent(htmlTemplate, "text/html; charset=utf-8");

        try {
            mailSender.send(message);
            System.out.println("Email sent sucessfully!");
        } catch (Exception exe) {
            exe.printStackTrace();
            System.out.println(exe.getMessage());
        }
    }

    public void welcomeEmailArbitrator(String name, String email, String password) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom(new InternetAddress("noreply@virturesolve360.com"));
        message.setRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("Welcome to virturesolve360.com");

        InputStream inputStream = new ClassPathResource("templates/welcome_arbitrator.html").getInputStream();
        String htmlTemplate = "";
        try { // Read the HTML template into a String variable

            Path tempFile =
                    Files.createTempDirectory("").resolve(UUID.randomUUID().toString() + ".tmp");

            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            htmlTemplate = new String(Files.readAllBytes(tempFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // Replace placeholders in the HTML template with dynamic values
        htmlTemplate = htmlTemplate.replace("{{name}}", name);
        htmlTemplate = htmlTemplate.replace("{{email}}", email);
        htmlTemplate = htmlTemplate.replace("{{password}}", password);

        // Set the email's content to be the HTML template
        message.setContent(htmlTemplate, "text/html; charset=utf-8");

        try {
            mailSender.send(message);
            System.out.println("Email sent sucessfully!");
        } catch (Exception exe) {
            exe.printStackTrace();
            System.out.println(exe.getMessage());
        }
    }

    public void welcomeEmailClaimant(String name, String email, String authorizedPersonName, String password) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom(new InternetAddress("noreply@virturesolve360.com"));
        message.setRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("Welcome to virturesolve360.com");

        InputStream inputStream = new ClassPathResource("templates/welcome_claimant.html").getInputStream();
        String htmlTemplate = "";
        try { // Read the HTML template into a String variable

            Path tempFile =
                    Files.createTempDirectory("").resolve(UUID.randomUUID().toString() + ".tmp");

            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            htmlTemplate = new String(Files.readAllBytes(tempFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // Replace placeholders in the HTML template with dynamic values
        htmlTemplate = htmlTemplate.replace("{{name}}", authorizedPersonName);
        htmlTemplate = htmlTemplate.replace("{{email}}", email);
        htmlTemplate = htmlTemplate.replace("{{claimantName}}", name);
        htmlTemplate = htmlTemplate.replace("{{password}}", password);

        // Set the email's content to be the HTML template
        message.setContent(htmlTemplate, "text/html; charset=utf-8");

        try {
            mailSender.send(message);
            System.out.println("Email sent sucessfully!");
        } catch (Exception exe) {
            exe.printStackTrace();
            System.out.println(exe.getMessage());
        }
    }
}