package com.kasa.adr.controller;


import com.kasa.adr.config.Constant;
import com.kasa.adr.dto.ArbitratorAssign;
import com.kasa.adr.dto.CallDetails;
import com.kasa.adr.model.CaseDetails;
import com.kasa.adr.model.CaseDocuments;
import com.kasa.adr.model.CaseHistoryDetails;
import com.kasa.adr.model.User;
import com.kasa.adr.repo.CaseDocumentsRepo;
import com.kasa.adr.repo.CaseHistoryDetailsRepo;
import com.kasa.adr.repo.UserRepository;
import com.kasa.adr.service.CaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = Constant.BASE_CONTEXT_PATH + "/case")
public class CaseController {

    @Autowired
    CaseService caseService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    CaseHistoryDetailsRepo caseHistoryDetailsRepo;

    @Autowired
    CaseDocumentsRepo caseDocumentsRepo;

    @GetMapping("/{caseId}")
    public Optional<CaseDetails> caseDetails(@PathVariable String caseId) {
        return caseService.findById(caseId);
    }

    @GetMapping("/byPage")
    public ResponseEntity<Map<String, Object>> findAllByPage(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int sizePerPage,

                                                             @RequestParam String monthYear, @RequestParam String arbitrator, @RequestParam String claimant) {
        Pageable pageable = PageRequest.of(page, sizePerPage, Sort.Direction.DESC, "customerName");

        Map<String, Object> allByPage = caseService.findAllByPage(pageable, monthYear, arbitrator, claimant);
        return new ResponseEntity<>(allByPage, HttpStatus.OK);


    }


    @GetMapping("/case-ids")
    public List<String> findCaseIds(@RequestParam String monthYear, @RequestParam String arbitratorId, @RequestParam String claimantId, @RequestParam String status) {
        return caseService.findCaseIds(monthYear, arbitratorId, claimantId, status);
    }

    @GetMapping("/search")
    public List<CaseDetails> search(@RequestParam String customerId) {
        return caseService.findCaseByCustomerId(customerId);
    }

    @PostMapping("/schedule-call")
    public ResponseEntity<?> scheduleCall(@RequestBody CallDetails callDetails) {
        caseService.scheduleCall(callDetails);
        return new ResponseEntity<>("Request Scheduled", HttpStatus.OK);
    }


    @PostMapping("/upload-documents")
    public ResponseEntity<?> updateDocuments(@RequestParam("caseId") String caseId, @RequestParam("file") MultipartFile multipartFile, @RequestParam("description") String description, @RequestParam("userId") String userId) {
        if (multipartFile.isEmpty()) {
            return new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST);
        }
        Optional<User> updatedBy = userRepository.findById(userId);
        if (updatedBy.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        String name = updatedBy.get().getName();
        caseService.updateDocuments(caseId, multipartFile, description, name);
        return new ResponseEntity<>("document updated", HttpStatus.OK);
    }

    @PostMapping("/assign-arbitrator")
    public ResponseEntity<?> assignArbitrator(@RequestBody ArbitratorAssign arbitratorAssign) {
        caseService.assignArbitrator(arbitratorAssign);
        return new ResponseEntity<>("Arbitrator Assigned", HttpStatus.OK);
    }

    @GetMapping("/history/{caseId}")
    public ResponseEntity<List<CaseHistoryDetails>> getCaseHistory(@PathVariable String caseId) {
        List<CaseHistoryDetails> caseHistory = caseHistoryDetailsRepo.findByCaseId(caseId);
        return new ResponseEntity<>(caseHistory, HttpStatus.OK);
    }

    @GetMapping("/documents/{caseId}")
    public ResponseEntity<List<CaseDocuments>> getCaseDocuments(@PathVariable String caseId) {
        List<CaseDocuments> documents = caseDocumentsRepo.findByCaseId(caseId);
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @PostMapping("/send-notice")
    public ResponseEntity<?> sendNotice(@RequestParam(value = "caseIds", required = false) String[] caseIds, @RequestParam(value = "file",required = false) MultipartFile multipartFile, @RequestParam("templateId") String templateId, @RequestParam("userId") String userId) {
        if (multipartFile.isEmpty()) {
            return new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST);
        }
        Optional<User> updatedBy = userRepository.findById(userId);
        if (updatedBy.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
       caseService.sendNotice(caseIds, multipartFile, templateId, updatedBy.get());
        return new ResponseEntity<>("Notice sent!", HttpStatus.OK);
    }

}
