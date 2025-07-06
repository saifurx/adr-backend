package com.kasa.adr.controller;


import com.kasa.adr.config.Constant;
import com.kasa.adr.dto.ContactUs;
import com.kasa.adr.dto.LoginRequest;
import com.kasa.adr.dto.LoginResponse;
import com.kasa.adr.service.AuthenticationService;
import com.kasa.adr.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)

@RestController
@RequestMapping(value = Constant.BASE_CONTEXT_PATH + "/auth")
public class AuthController {

    Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    UserService userService;

    @Autowired
    AuthenticationService authenticationService;


    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        logger.info("Login for User {}", loginRequest.toString());
        return authenticationService.authenticate(loginRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity logout(@RequestHeader(name = "Authorization") String token) {
        return userService.logout(token);
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String userName) {
        logger.info("Password reset request for: " + userName);
        return userService.forgotPassword(userName);

    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserDetails(@RequestHeader(name = "Authorization") String token) {
        // System.out.println("token--->" + token);
        return userService.me(token);
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> validatePasswordResetToken(@RequestParam String token, @RequestParam String newPassword) {
        return userService.resetPassword(token, newPassword);
    }

    @PostMapping("/contact-us")
    public String contactUs(@Valid @RequestBody ContactUs contactUs) {
        String body = contactUs.toString();
        // notificationService.sendEmail("support@naesta.tech", "Crimatrix Contact Us", body);
        return "Thanks for Contact Us!";

    }

    @GetMapping("/test")
    public String test() {
        return "UP";
    }
}
