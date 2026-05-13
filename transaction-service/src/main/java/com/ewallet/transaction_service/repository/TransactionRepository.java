package com.ewallet.transaction_service.repository;

import com.ewallet.transaction_service.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.senderEmail = :email OR t.receiverEmail = :email ORDER BY t.createdAt DESC")
    List<Transaction> findBySenderEmailOrReceiverEmail(@Param("email") String email);
}