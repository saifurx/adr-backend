package com.kasa.adr.controller;


import com.kasa.adr.config.Constant;
import com.kasa.adr.dto.Documents;
import com.kasa.adr.model.Case;
import com.kasa.adr.model.CaseHistoryDetails;
import com.kasa.adr.service.CaseHistoryDetailsService;
import com.kasa.adr.service.CaseService;
import com.kasa.adr.service.external.MSG91Service;
import com.kasa.adr.service.external.S3Service;
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
@RequestMapping(value = Constant.BASE_CONTEXT_PATH + "/respondent")
public class RespondentController {
    Logger logger = LoggerFactory.getLogger(RespondentController.class);

    @Autowired
    CaseService caseService;

    @Autowired
    MSG91Service msg91Service;
    @Autowired
    S3Service s3Service;
    @Autowired
    CaseHistoryDetailsService caseHistoryDetailsService;

    @GetMapping("/test")
    public String test() {
        return "test";
    }


    @GetMapping("/case")
    public ResponseEntity<Object> cases(@RequestParam String token, @RequestParam String mobile) {
        if (msg91Service.validateOtpToken(token)) {
            return new ResponseEntity<>(caseService.getCaseByMobile(mobile), HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid Token", HttpStatus.UNAUTHORIZED);

    }

    @PostMapping(value = "/upload")
    public ResponseEntity<?> uploadFile(@RequestParam String token, @RequestParam("caseId") String caseId, @RequestParam("file")  MultipartFile multipartFile,@RequestParam("description") String description) {
        if (msg91Service.validateOtpToken(token)) {
            logger.info("Uploading files");
            String fileName = s3Service.uploadCaseFile(caseId,multipartFile, "cases");
            Case aCase = caseService.getOneCase(caseId);
            List<Documents> documents = aCase.getDocuments();
            if(documents == null) {
                documents = new java.util.ArrayList<>();
            }
            documents.add(Documents.builder().fileName(fileName).description(description).createdAt(Instant.now()).build());
            aCase.setDocuments(documents);
            caseService.save(aCase);
            //TODO update case history
            caseHistoryDetailsService.createCaseHistoryDetail(CaseHistoryDetails.builder().createdBy("Respondent").caseId(caseId).description("document uploaded").date(Instant.now()).documentUrl(fileName).build());
            return new ResponseEntity<>(aCase, HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid Token", HttpStatus.UNAUTHORIZED);
    }
    @GetMapping("/case-history/{caseId}")
    public ResponseEntity<List<CaseHistoryDetails>> getCaseHistory(@PathVariable String caseId,@RequestParam String token) {
        List<CaseHistoryDetails> caseHistory = caseHistoryDetailsService.findByCaseId(caseId);
        return new ResponseEntity<>(caseHistory, HttpStatus.OK);
    }

}
