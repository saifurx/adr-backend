package com.kasa.adr.controller;


import com.kasa.adr.config.Constant;
import com.kasa.adr.model.Case;
import com.kasa.adr.model.CaseHistory;
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
            List<String> documents = aCase.getDocuments();
            documents.add(fileName);
            aCase.setDocuments(documents);
            List<CaseHistory> history = aCase.getHistory();
            history.add(CaseHistory.builder().descriptions("File Uploaded by Respondent: "+description).date(Instant.now()).build());
            aCase.setHistory(history);
            caseService.save(aCase);
            return new ResponseEntity<>(aCase, HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid Token", HttpStatus.UNAUTHORIZED);
    }


}
