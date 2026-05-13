package com.ewallet.transaction_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(
        name = "wallet-service",
        url = "http://localhost:8080/wallet"
)
public interface WalletClient {

    @PostMapping("/{email}/creditOnTransaction")
    void credit(
            @RequestHeader("Authorization") String token,
            @PathVariable String email,
            @RequestParam BigDecimal amount
    );

    @PostMapping("/{email}/debitOnTransaction")
    void debit(

            @RequestHeader("Authorization") String token,
            @PathVariable String email,
            @RequestParam BigDecimal amount
    );
}