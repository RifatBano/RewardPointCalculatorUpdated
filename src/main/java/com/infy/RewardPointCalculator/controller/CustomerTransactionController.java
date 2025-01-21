package com.infy.RewardPointCalculator.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infy.RewardPointCalculator.dto.CustomerTransactionDTO;
import com.infy.RewardPointCalculator.model.CustomerTransaction;
import com.infy.RewardPointCalculator.service.CustomerTransactionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers/transactions/{customerId}")
public class CustomerTransactionController {

    @Autowired
    private CustomerTransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<CustomerTransaction>> getTransactions(@PathVariable Long customerId) {
        List<CustomerTransaction> transactions = transactionService.getTransactions(customerId);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping
    public ResponseEntity<CustomerTransaction> addTransaction(
            @PathVariable Long customerId,
            @RequestBody @Valid CustomerTransactionDTO transactionDTO) {
        CustomerTransaction transaction = transactionService.addTransaction(customerId, transactionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }
    @PutMapping("/{transactionId}")
    public ResponseEntity<CustomerTransaction> editTransaction(
            @PathVariable Long customerId, @PathVariable Long transactionId,
            @RequestBody @Valid CustomerTransactionDTO transactionDTO) {
        CustomerTransaction updatedTransaction = transactionService.editTransaction(customerId, transactionId, transactionDTO);
        return ResponseEntity.ok(updatedTransaction);
    }
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long customerId, @PathVariable Long transactionId) {
        transactionService.deleteTransaction(customerId, transactionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
