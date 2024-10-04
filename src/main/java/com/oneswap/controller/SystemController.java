package com.oneswap.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/1.0/system")
public class SystemController {

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck(){
        return new ResponseEntity("data", HttpStatus.OK);
    }

}
