package com.ewallet.transaction_service.controller;

import com.ewallet.transaction_service.entity.Transaction;
import com.ewallet.transaction_service.request.TransferRequest;
import com.ewallet.transaction_service.services.TransactionService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(
            @RequestHeader("Authorization") String token,
            @RequestBody TransferRequest request
    ) {

        return ResponseEntity.ok(
                transactionService.transfer(
                        token,
                        request.getSenderEmail(),
                        request.getReceiverEmail(),
                        request.getAmount(),
                        request.getDescription()
                )
        );
    }

    @GetMapping("/history/{email}")
    public ResponseEntity<?> history(
            @PathVariable String email
    ) {

        return ResponseEntity.ok(
                transactionService.getTransactions(email)
        );
    }

}