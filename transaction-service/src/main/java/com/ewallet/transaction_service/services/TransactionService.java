package com.ewallet.transaction_service.services;

import com.ewallet.transaction_service.client.WalletClient;
import com.ewallet.transaction_service.entity.Transaction;
import com.ewallet.transaction_service.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final WalletClient walletClient;

    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction transfer(

            String token,

            Long senderUserId,

            Long receiverUserId,

            BigDecimal amount,

            String description
    ) {

        Transaction tx = Transaction.builder()

                .senderUserId(senderUserId)

                .receiverUserId(receiverUserId)

                .amount(amount)

                .description(description)

                .build();

        tx = transactionRepository.save(tx);

        try {

            /* ================= DEBIT ================= */

            walletClient.debit(

                    token,

                    senderUserId,

                    amount
            );

            log.info(
                    "Debited {} from user {}",
                    amount,
                    senderUserId
            );

            /* ================= CREDIT ================= */

            walletClient.credit(

                    token,

                    receiverUserId,

                    amount
            );

            log.info(
                    "Credited {} to user {}",
                    amount,
                    receiverUserId
            );

            /* ================= SUCCESS ================= */

            tx.setStatus(Transaction.Status.SUCCESS);

            return transactionRepository.save(tx);

        } catch (Exception e) {

            /* ================= FAILED ================= */

            tx.setStatus(Transaction.Status.FAILED);

            transactionRepository.save(tx);

            log.error(
                    "Transaction failed: {}",
                    e.getMessage()
            );

            throw new RuntimeException(
                    "Transaction failed: " + e.getMessage()
            );
        }
    }

    public List<Transaction> getTransactions(Long userId) {
        return transactionRepository.findBySenderUserIdOrReceiverUserId(userId);
    }
}

