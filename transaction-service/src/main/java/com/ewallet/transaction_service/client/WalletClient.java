package com.ewallet.transaction_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "wallet-service",url = "http://localhost:8080/wallet")
public interface WalletClient {

    @PostMapping("/{userId}/debit")
    Object debit(@PathVariable Long userId,
                 @RequestParam BigDecimal amount);

    @PostMapping("/{userId}/credit")
    Object credit(@PathVariable Long userId,
                  @RequestParam BigDecimal amount);
}
