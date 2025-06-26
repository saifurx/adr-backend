package com.kasa.adr.controller;


import com.kasa.adr.config.Constant;
import com.kasa.adr.dto.CaseUploadRequest;
import com.kasa.adr.dto.TemplateMapObject;
import com.kasa.adr.dto.TemplateRequest;
import com.kasa.adr.model.CaseUploadDetails;
import com.kasa.adr.model.Holiday;
import com.kasa.adr.model.Template;
import com.kasa.adr.model.User;
import com.kasa.adr.repo.HolidayRepo;
import com.kasa.adr.repo.TemplateRepo;
import com.kasa.adr.service.CaseProcessingService;
import com.kasa.adr.service.UserService;
import com.kasa.adr.service.external.S3Service;
import com.kasa.adr.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = Constant.BASE_CONTEXT_PATH + "/setting")
public class SettingController {

    Logger logger = LoggerFactory.getLogger(SettingController.class);


    @Autowired
    UserService userService;

    @Autowired
    HolidayRepo holidayRepo;

    @Autowired
    TemplateRepo templateRepo;


    @Autowired
    S3Service s3Service;

    @Autowired
    CaseProcessingService caseFileProcessingService;




    @GetMapping("/claimant-admin")
    public ResponseEntity<List<User>> claimantAdmin() {
        return new ResponseEntity<>(userService.claimantAdmin(), HttpStatus.OK);
    }



    @GetMapping("/template")
    public List<Template> template() {
            return templateRepo.findAll();
    }

    @GetMapping("/template/{claimantAdminId}")
    public List<Template> template(@PathVariable String claimantAdminId) {
        return templateRepo.findByClaimantAdmin(claimantAdminId);
    }

    @PostMapping("/template")
    public ResponseEntity<Object> template(@RequestBody TemplateRequest templateRequest) {
        User user = userService.findUserById(templateRequest.getClaimantAdminId());
        Template template = Template.builder().claimantAdminUser(user).emailSubject(templateRequest.getEmailSubject()).emailBody(templateRequest.getEmailBody()).name(templateRequest.getName()).status(true).createdAt(Instant.now()).smsTemplateId(templateRequest.getSmsTemplateId()).whatsAppTemplateId(templateRequest.getWhatsAppTemplateId()).attachmentText(templateRequest.getAttachmentText()).build();
        return new ResponseEntity<>(templateRepo.save(template), HttpStatus.OK);
    }

    @PutMapping("/template/{id}")
    public ResponseEntity<Object> updateTemplate(@RequestBody TemplateRequest templateRequest, @PathVariable String id) {
        User user = userService.findUserById(templateRequest.getClaimantAdminId());
        Template template = Template.builder()
                .id(id)
                .name(templateRequest.getName())
                .emailSubject(templateRequest.getEmailSubject())
                .emailBody(templateRequest.getEmailBody())
                .attachmentText(templateRequest.getAttachmentText())
                .whatsAppTemplateId(templateRequest.getWhatsAppTemplateId())
                .smsTemplateId(templateRequest.getSmsTemplateId())
                .status(templateRequest.isStatus())
                .claimantAdminUser(user)
                .createdAt(Instant.now())
                .build();

        return new ResponseEntity<>(templateRepo.save(template), HttpStatus.OK);
    }

    @GetMapping("/holidays/{year}")
    public List<Holiday> holidayByYear(@PathVariable String year) {
        return holidayRepo.findAllByYear(year);
    }

    @PostMapping("/holidays")
    public ResponseEntity<Object> createHoliday(@RequestBody Holiday holiday) {
        return new ResponseEntity<>(holidayRepo.save(holiday), HttpStatus.OK);
    }

    @DeleteMapping("/holidays/{id}")
    public ResponseEntity<Object> deleteHoliday(@PathVariable String id) {
        holidayRepo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping(value = "/upload")
    public ResponseEntity<?> uploadFile(@RequestParam String id, @RequestParam String folder, @RequestPart(value = "files") final MultipartFile[] multipartFiles) {
        logger.info("Uploading files");
        List<String> responses = s3Service.uploadFileToS3(multipartFiles, id, folder);
        return new ResponseEntity<>(responses, HttpStatus.OK);

    }

    @PostMapping("/case-file-upload")
    public ResponseEntity<Object> caseFileUpload(@RequestBody CaseUploadRequest caseUploadRequest) {
        caseFileProcessingService.processCaseFile(caseUploadRequest);
        logger.info("Task Processing in background");
        return new ResponseEntity<>("Your file is processing!", HttpStatus.OK);
    }

    @GetMapping("/my-uploads/{userId}")
    public List<CaseUploadDetails> myUploads(@PathVariable String userId) {
        return caseFileProcessingService.myUploads(userId);
    }

    @GetMapping("/template-fields")
    public List<String> templateFields() {
        return CommonUtils.extractFieldNames(TemplateMapObject.class);
    }


}
