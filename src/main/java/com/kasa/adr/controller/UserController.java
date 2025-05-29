package com.kasa.adr.controller;

import com.kasa.adr.config.Constant;
import com.kasa.adr.dto.ArbitratorCreateRequest;
import com.kasa.adr.dto.ClaimantCreateRequest;
import com.kasa.adr.model.User;
import com.kasa.adr.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = Constant.BASE_CONTEXT_PATH + "/user")
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class);


    @Autowired
    UserService userService;


    @PostMapping("/arbitrator")
    public ResponseEntity<Object> arbitrator(@RequestBody ArbitratorCreateRequest loginRequest) {
        return userService.registerArbitratorUser(loginRequest);
    }

    @GetMapping("/arbitrator")
    public ResponseEntity<Object> arbitrator(@RequestParam(required = false) String status) {
        return new ResponseEntity<>(userService.getAllArbitrator(status), HttpStatus.OK);
    }

    @GetMapping("/{userType}/byPage")
    public Page<User> findAllByPage(@PathVariable String userType, @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int sizePerPage,
                                    @RequestParam(defaultValue = "createdAt") String sortField,
                                    @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection) {
        Pageable pageable = PageRequest.of(page, sizePerPage, sortDirection, sortField);
        return userService.findAllByPage(userType, pageable);
    }

    @GetMapping("/arbitrator/{id}")
    public ResponseEntity<Object> arbitratorById(@PathVariable String id) {
        return new ResponseEntity<>(userService.getArbitratorById(id), HttpStatus.OK);
    }

    @PatchMapping("/arbitrator/{id}")
    public ResponseEntity<Object> arbitratorUpdate(@PathVariable String id, @RequestBody ArbitratorCreateRequest loginRequest) {
        return new ResponseEntity<>(userService.updateArbitrator(id, loginRequest), HttpStatus.OK);
    }

    @PostMapping("/institution")
    public ResponseEntity<Object> institution(@RequestBody ClaimantCreateRequest createRequest) {
        return userService.newClaimant(createRequest);
    }

    @GetMapping("/institution")
    public ResponseEntity<Object> institution(@RequestParam(required = false) String searchStr) {
        return new ResponseEntity<>(userService.allInstitutions(), HttpStatus.OK);
    }

    @GetMapping("/institution/{id}")
    public ResponseEntity<Object> institutionById(@PathVariable String id) {
        return new ResponseEntity<>(userService.institutionById(id), HttpStatus.OK);
    }

    @PatchMapping("/institution/{id}")
    public ResponseEntity<Object> updateInstitution(@PathVariable String id, @RequestBody ClaimantCreateRequest institutionProfile) {
        return new ResponseEntity<>(userService.updateInstitution(id, institutionProfile), HttpStatus.OK);
    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String newPassword, @RequestParam String userId) {
        logger.info("userID :"+userId);
        return userService.resetPasswordByUserId(newPassword, userId);
    }
}