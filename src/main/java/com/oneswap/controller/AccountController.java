package com.oneswap.controller;

import com.oneswap.dto.ErrorResponseDto;
import com.oneswap.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.UnknownHostException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/1.0/account")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/info")
    public ResponseEntity<?> info(String address){
        if (address != null)
            return new ResponseEntity(accountService.getAccountByAddress(address), HttpStatus.OK);
        else
            return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/history/transactions")
    public ResponseEntity<?> transactionsHistory(String address){
        if (address != null)
            return new ResponseEntity(accountService.getTransactionByAddress(address), HttpStatus.OK);
        else
            return new ResponseEntity("no data",HttpStatus.NOT_FOUND);
    }

    @GetMapping("/history/limitOrders")
    public ResponseEntity<?> limitOrdersHistory(String address){
        if (address != null)
            return new ResponseEntity(accountService.getLimitOrderByAddress(address), HttpStatus.OK);
        else
            return new ResponseEntity("no data",HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnknownHostException.class)
    public ResponseEntity<?> handleUnknownHostException(UnknownHostException ex) {
        ErrorResponseDto<String> errorResponse = ErrorResponseDto.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
}
