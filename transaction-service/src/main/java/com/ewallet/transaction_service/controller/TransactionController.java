package com.ewallet.transaction_service.controller;

import com.ewallet.transaction_service.request.TransferRequest;
import com.ewallet.transaction_service.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        return ResponseEntity.ok(transactionService.transfer(
                request.getSenderUserId(),
                request.getReceiverUserId(),
                request.getAmount(),
                request.getDescription()
                )
        );
    }
    
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> history(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getTransactions(userId));
    }



}
