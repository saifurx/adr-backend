package com.kasa.adr.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

//@CrossOrigin(origins = "*", maxAge = 3600)

//@RestController
//@RequestMapping(value = Constant.BASE_CONTEXT_PATH + "/webhook")
public class WebhookExternal {
    @PostMapping("/zoom-events")
    public ResponseEntity<Object> zoomEvents() {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/brevo-events")
    public ResponseEntity<Object> brevoEvents() {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
