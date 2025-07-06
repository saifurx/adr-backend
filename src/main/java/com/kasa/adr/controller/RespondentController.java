package com.kasa.adr.controller;


import com.kasa.adr.config.Constant;
import com.kasa.adr.model.CaseDetails;
import com.kasa.adr.model.CaseDocuments;
import com.kasa.adr.model.CaseHistoryDetails;
import com.kasa.adr.repo.CaseDocumentsRepo;
import com.kasa.adr.repo.CaseHistoryDetailsRepo;
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
    CaseHistoryDetailsRepo caseHistoryDetailsRepo;

    @Autowired
    CaseDocumentsRepo caseDocumentsRepo;


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
    public ResponseEntity<?> uploadFile(@RequestParam String token, @RequestParam("caseId") String caseId, @RequestParam("file") MultipartFile multipartFile, @RequestParam("description") String description) {
        if (msg91Service.validateOtpToken(token)) {
            CaseDetails aCase = caseService.findById(caseId).orElse(null);
        if (aCase == null) {
                return new ResponseEntity<>("Case not found", HttpStatus.NOT_FOUND);
            }else {
            String customerName = aCase.getCustomerName();
            caseService.updateDocuments(caseId, multipartFile, description, customerName);
            return new ResponseEntity<>("Uploaded", HttpStatus.OK);
        }
        }
        return new ResponseEntity<>("Invalid Token", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/case-history/{caseId}")
    public ResponseEntity<List<CaseHistoryDetails>> getCaseHistory(@PathVariable String caseId, @RequestParam String token) {
        if (msg91Service.validateOtpToken(token)) {
            List<CaseHistoryDetails> caseHistory = caseHistoryDetailsRepo.findByCaseId(caseId);
            return new ResponseEntity<>(caseHistory, HttpStatus.OK);
        }
        return new ResponseEntity("Invalid Token", HttpStatus.UNAUTHORIZED);
    }
    @GetMapping("/case-documents/{caseId}")
    public ResponseEntity<List<CaseDocuments>> getCaseDocuments(@PathVariable String caseId, @RequestParam String token) {
        if (msg91Service.validateOtpToken(token)) {
            List<CaseDocuments> documents = caseDocumentsRepo.findByCaseId(caseId);
            return new ResponseEntity(documents, HttpStatus.OK);
        }
        return new ResponseEntity("Invalid Token", HttpStatus.UNAUTHORIZED);
    }

}
