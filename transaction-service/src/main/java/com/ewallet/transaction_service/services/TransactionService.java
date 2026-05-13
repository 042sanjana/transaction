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

            String senderEmail,

            String receiverEmail,

            BigDecimal amount,

            String description
    ) {

        // ✅ Build transaction but DON'T save yet
        Transaction tx = Transaction.builder()
                .senderEmail(senderEmail)
                .receiverEmail(receiverEmail)
                .amount(amount)
                .description(description)
                .status(Transaction.Status.PENDING)
                .build();

        /* ================= DEBIT ================= */

        try {

            walletClient.debit(token, senderEmail, amount);

            log.info("Debited {} from user {}", amount, senderEmail);

        } catch (Exception e) {

            // Debit itself failed — no money moved, just mark FAILED
            log.error("Debit failed for user {}: {}", senderEmail, e.getMessage());

            tx.setStatus(Transaction.Status.FAILED);
            transactionRepository.save(tx);

            throw new RuntimeException("Debit failed: " + e.getMessage());
        }

        /* ================= CREDIT ================= */

        try {

            walletClient.credit(token, receiverEmail, amount);

            log.info("Credited {} to user {}", amount, receiverEmail);

        } catch (Exception e) {

            // ✅ Credit failed — reverse the debit (compensating transaction)
            log.error("Credit failed for user {}: {}. Reversing debit...", receiverEmail, e.getMessage());

            try {

                walletClient.credit(token, senderEmail, amount); // reverse debit
                log.info("Debit reversed successfully for user {}", senderEmail);

            } catch (Exception rollbackEx) {

                // 🚨 Critical — debit reversed failed, needs manual intervention
                log.error("CRITICAL: Debit reversal failed for user {} | Amount: {} | Error: {}",
                        senderEmail, amount, rollbackEx.getMessage());
            }

            tx.setStatus(Transaction.Status.FAILED);
            transactionRepository.save(tx);

            throw new RuntimeException("Credit failed, debit reversed: " + e.getMessage());
        }

        /* ================= SUCCESS ================= */

        // ✅ Only saved as SUCCESS if both debit and credit passed
        tx.setStatus(Transaction.Status.SUCCESS);
        return transactionRepository.save(tx);
    }

    public List<Transaction> getTransactions(String email) {
        return transactionRepository.findBySenderEmailOrReceiverEmail(email);
    }
}