package com.kasa.adr.controller;


import com.kasa.adr.config.Constant;
import com.kasa.adr.dto.*;
import com.kasa.adr.model.*;
import com.kasa.adr.repo.HolidayRepo;
import com.kasa.adr.repo.SpecializationRepo;
import com.kasa.adr.repo.TemplateRepo;
import com.kasa.adr.service.CaseFileProcessingService;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    SpecializationRepo specializationRepo;


    @Autowired
    S3Service s3Service;

    @Autowired
    CaseFileProcessingService caseFileProcessingService;


    @PostMapping("/admin")
    public ResponseEntity<Object> createAdmin(@RequestBody AdminCreateRequest loginRequest) {
        return userService.registerAdmin(loginRequest);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<User>> allAdmin() {
        return new ResponseEntity<>(userService.getAllAdmin(), HttpStatus.OK);
    }

    @PostMapping("/claimant-team/{claimantAdminUserId}")
    public ResponseEntity<Object> createClaimantUser(@PathVariable String id, @RequestBody ClaimantCreateRequest claimantCreateRequest) {
        return userService.createClaimantUser(id, claimantCreateRequest);
    }

    @GetMapping("/claimant-admin")
    public ResponseEntity<List<User>> claimantAdmin() {
        return new ResponseEntity<>(userService.claimantAdmin(), HttpStatus.OK);
    }

    @GetMapping("/claimant-team/{claimantAdminUserId}")
    public ResponseEntity<List<User>> claimantTeam(@PathVariable String id) {
        return new ResponseEntity<>(userService.getClaimantUser(id), HttpStatus.OK);
    }

    @PostMapping("/specialization")
    public ResponseEntity<Object> createSpecialization(@RequestBody Specialization specialization) {
        return new ResponseEntity<>(specializationRepo.save(specialization), HttpStatus.OK);
    }


    @GetMapping("/specialization")
    public ResponseEntity<Object> allSpecialization(@RequestParam(required = false) String searchStr) {
        return new ResponseEntity<>(specializationRepo.findAll(), HttpStatus.OK);
    }

    @DeleteMapping("/specialization/{id}")
    public ResponseEntity<Object> deleteSpecialization(@PathVariable String id) {
        specializationRepo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/template")
    public List<Template> template(@RequestParam(required = false) String type) {
        logger.info("Finding template by:" + type);
        if (type == null || type.isEmpty())
            return templateRepo.findAll();
        else
            return templateRepo.findAllByType(type);
    }

    @PostMapping("/template")
    public ResponseEntity<Object> template(@RequestBody TemplateRequest templateRequest) {
        User user = userService.findUserById(templateRequest.getClaimant());
        Template template = Template.builder().type(templateRequest.getType()).subject(templateRequest.getSubject()).text(templateRequest.getText()).name(templateRequest.getName()).status(true).createdAt(Instant.now()).claimantAdminUser(user).build();
        return new ResponseEntity<>(templateRepo.save(template), HttpStatus.OK);
    }

    @PatchMapping("/template/{id}")
    public ResponseEntity<Object> updateTemplate(@RequestBody TemplateRequest templateRequest, @PathVariable String id) {
        User user = userService.findUserById(templateRequest.getClaimant());
        Template template = Template.builder()
                .id(id)
                .name(templateRequest.getName())
                .type(templateRequest.getType())
                .subject(templateRequest.getSubject())
                .text(templateRequest.getText())
                .status(true)
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
    public ResponseEntity<List<CaseUploadDetails>> myUploads(@PathVariable String userId) {
        return caseFileProcessingService.myUploads(userId);
    }

    @GetMapping("/template-fields")
    public List<String> templateFields() {
        return CommonUtils.extractFieldNames(TemplateMapObject.class);
    }

    @GetMapping("/template-names")
    public List<String> templateNames() {
        return Arrays.stream(TemplateName.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

}
