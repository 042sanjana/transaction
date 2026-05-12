package com.ewallet.transaction_service.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    private Long senderUserId;
    private Long receiverUserId;
    private BigDecimal amount;
    private String description;
}
