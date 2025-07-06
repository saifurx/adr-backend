package com.kasa.adr.controller;


import com.kasa.adr.config.Constant;
import com.kasa.adr.dto.ArbitratorAssign;
import com.kasa.adr.dto.CallDetails;
import com.kasa.adr.dto.EmailDetails;
import com.kasa.adr.dto.UpdateStatus;
import com.kasa.adr.model.CaseDetails;
import com.kasa.adr.model.CaseHistoryDetails;
import com.kasa.adr.service.CaseHistoryDetailsService;
import com.kasa.adr.service.CaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    CaseHistoryDetailsService caseHistoryDetailsService;

    @GetMapping("/{caseId}")
    public Optional<CaseDetails> caseDetails(@PathVariable String caseId) {
        return caseService.findById(caseId);
    }

    @GetMapping("/byPage")
    public ResponseEntity<Map<String, Object>> findAllByPage(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int sizePerPage,
                                                             @RequestParam(defaultValue = "createdAt") String sortField,
                                                             @RequestParam(defaultValue = "desc") Sort.Direction sortDirection, @RequestParam String monthYear, @RequestParam String arbitrator, @RequestParam String claimant, @RequestParam String status, @RequestParam String srcStr) {
        Pageable pageable = PageRequest.of(page, sizePerPage, sortDirection, sortField);
        if (!srcStr.isEmpty()) {
            return new ResponseEntity<>(caseService.findAllByPage(pageable, srcStr), HttpStatus.OK);
        } else {
            Map<String, Object> allByPage = caseService.findAllByPage(pageable, monthYear, arbitrator, claimant, status);
            return new ResponseEntity<>(allByPage, HttpStatus.OK);
        }

    }

//    @GetMapping("/casesByPage")
//    public Page<CaseDetails> casesByPage(@RequestParam String arbitratorId,
//                                  @RequestParam String claimantAdminId,
//                                  @RequestParam String status,
//                                  @RequestParam String monthYear, Pageable pageable) {
//        return caseService.casesByPage(pageable, monthYear, arbitratorId, claimantAdminId, status);
//
//    }


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


    @PostMapping("/send-email")
    public ResponseEntity<?> sendEmail(@RequestBody EmailDetails emailDetails) {
        caseService.sendEMail(emailDetails);
        return new ResponseEntity<>("Request Scheduled", HttpStatus.OK);
    }


    @PostMapping("/update-status")
    public ResponseEntity<?> updateStatus(@RequestBody UpdateStatus updateStatus) {
        caseService.updateStatus(updateStatus);
        return new ResponseEntity<>("status updated", HttpStatus.OK);
    }

    @PostMapping("/assign-arbitrator")
    public ResponseEntity<?> assignArbitrator(@RequestBody ArbitratorAssign arbitratorAssign) {
        caseService.assignArbitrator(arbitratorAssign);
        return new ResponseEntity<>("Arbitrator Assigned", HttpStatus.OK);
    }

    @GetMapping("/case-history/{caseId}")
    public ResponseEntity<List<CaseHistoryDetails>> getCaseHistory(@PathVariable String caseId) {
        List<CaseHistoryDetails> caseHistory = caseHistoryDetailsService.findByCaseId(caseId);
        return new ResponseEntity<>(caseHistory, HttpStatus.OK);
    }
}
