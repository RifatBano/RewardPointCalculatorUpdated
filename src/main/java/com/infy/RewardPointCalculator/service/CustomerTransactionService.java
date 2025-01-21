package com.infy.RewardPointCalculator.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.infy.RewardPointCalculator.Util.RewardPointCalculator;
import com.infy.RewardPointCalculator.dto.CustomerTransactionDTO;
import com.infy.RewardPointCalculator.model.Customer;
import com.infy.RewardPointCalculator.model.CustomerTransaction;
import com.infy.RewardPointCalculator.model.RewardPoints;
import com.infy.RewardPointCalculator.repository.CustomerRepository;
import com.infy.RewardPointCalculator.repository.CustomerTransactionRepository;
import com.infy.RewardPointCalculator.repository.RewardPointsRepository;

import jakarta.validation.Valid;

@Service
public class CustomerTransactionService {

	Logger log=LoggerFactory.getLogger(CustomerTransactionService.class);
	
    @Autowired
    private CustomerTransactionRepository transactionRepository;

    @Autowired
    private RewardPointsRepository rewardPointsRepository;

    @Autowired
    private CustomerRepository customerRepository;  // Add repository for Customer

    // Get all transactions for a specific customer
    public List<CustomerTransaction> getTransactions(Long customerId) {
    	try {
            List<CustomerTransaction> transactions = transactionRepository.findByCustomerId(customerId);
            if (transactions.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No transactions found for this customer");
            }
            return transactions;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while fetching transactions", e);
        }    }

    // Add a new transaction and calculate reward points
    public CustomerTransaction addTransaction(Long customerId, CustomerTransactionDTO transactionDTO) {
      try {
    	Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        CustomerTransaction transaction = new CustomerTransaction();
        transaction.setCustomer(customer);  // Set the existing customer from the database
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setSpentDetails(transactionDTO.getSpentDetails());
        transaction.setDate(transactionDTO.getTransactionDate());
        transactionRepository.save(transaction);

        int month = transaction.getDate().getMonthValue();
        int year = transaction.getDate().getYear();
        int points = RewardPointCalculator.calculatePoints(transactionDTO.getAmount());
        List<RewardPoints> rewardPointsList = rewardPointsRepository.findByCustomerAndMonthAndYear(customer, month, year);
        RewardPoints rewardPoints;
        if (rewardPointsList.isEmpty()) {
            rewardPoints = new RewardPoints();
            rewardPoints.setCustomer(customer);
            rewardPoints.setMonth(month);
            rewardPoints.setYear(year);
            rewardPoints.setPoints(0);  // Initialize with zero points if new entry
        }else {
            rewardPoints = rewardPointsList.get(0);  // Get the first entry from the list
        }

        rewardPoints.setPoints(rewardPoints.getPoints() + points);
        rewardPointsRepository.save(rewardPoints);

        updateRewardPointsAsync(customerId, transaction.getDate().getMonthValue(), transaction.getDate().getYear());

        
        return transaction;
      }catch (DataIntegrityViolationException e) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data, transaction could not be added", e);
      } catch (Exception e) {
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while adding the transaction", e);
      }
    }

    // Edit an existing transaction
    public CustomerTransaction editTransaction(Long customerId, Long transactionId, @Valid CustomerTransactionDTO transactionDTO) {
      try {
    	CustomerTransaction existingTransaction = transactionRepository.findByCustomerIdAndId(customerId,transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        existingTransaction.setAmount(transactionDTO.getAmount());
        existingTransaction.setSpentDetails(transactionDTO.getSpentDetails());
        existingTransaction.setDate(transactionDTO.getTransactionDate());
        transactionRepository.save(existingTransaction);
        
        int month = existingTransaction.getDate().getMonthValue();
        int year = existingTransaction.getDate().getYear();
        // Recalculate and update reward points based on the new transaction data
        int points = RewardPointCalculator.calculatePoints(transactionDTO.getAmount());
        List<RewardPoints> rewardPointsList = rewardPointsRepository.findByCustomerAndMonthAndYear(existingTransaction.getCustomer(), month, year);

        // If no existing RewardPoints record is found, create a new one
        RewardPoints rewardPoints;
        if (rewardPointsList.isEmpty()) {
            rewardPoints = new RewardPoints();
            rewardPoints.setCustomer(existingTransaction.getCustomer());
            rewardPoints.setMonth(month);
            rewardPoints.setYear(year);
            rewardPoints.setPoints(0);  // Initialization with zero points if new entry
        } else {
            // If an existing RewardPoints record is found, it will use the first one
            rewardPoints = rewardPointsList.get(0);
        }

        rewardPoints.setPoints(rewardPoints.getPoints() + points);
        rewardPointsRepository.save(rewardPoints);
        
        updateRewardPointsAsync(customerId, month, year);

        return existingTransaction;
      }catch (DataIntegrityViolationException e) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data, transaction could not be updated", e);
      } catch (Exception e) {
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while editing the transaction", e);
      }
    }

    // Delete a transaction and adjust reward points
    public void deleteTransaction(Long customerId, Long transactionId) {
    
    	try {
    	Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        CustomerTransaction transaction = transactionRepository.findByCustomerIdAndId(customerId,transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        int points = RewardPointCalculator.calculatePoints(transaction.getAmount());
        List<RewardPoints> rewardPointsList = rewardPointsRepository.findByCustomerAndMonthAndYear(customer, transaction.getDate().getMonthValue(), transaction.getDate().getYear());

        if (!rewardPointsList.isEmpty()) {
            RewardPoints rewardPoints = rewardPointsList.get(0);
            rewardPoints.setPoints(rewardPoints.getPoints() - points);
            rewardPointsRepository.save(rewardPoints);
        }
        transactionRepository.delete(transaction);
        
        updateRewardPointsAsync(customerId, transaction.getDate().getMonthValue(), transaction.getDate().getYear());
    } catch (DataIntegrityViolationException e) {
            // Handle foreign key violations or any other data issues
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request, transaction could not be deleted", e);
        } catch (Exception e) {
            // Handle other unexpected exceptions
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while deleting the transaction", e);
        }

    }
    

    @Async
    public void updateRewardPointsAsync(Long customerId, Integer month, Integer year) {
        // Perform the long-running task of updating reward points asynchronously
        updateRewardPoints(customerId, month, year);
    }
    public void updateRewardPoints(Long customerId, Integer month, Integer year) {
    	log.info("updateRewardPoints started running");
        // Fetch the customer from the database
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Retrieve all transactions for the customer within the given month and year
        List<CustomerTransaction> transactions = transactionRepository.findByCustomerIdAndDateBetween(
            customerId, 
            LocalDate.of(year, month, 1), 
            LocalDate.of(year, month, 1).withDayOfMonth(LocalDate.of(year, month, 1).lengthOfMonth())
        );

        // Calculate total points
        int totalPoints = 0;
        for (CustomerTransaction transaction : transactions) {
            totalPoints += RewardPointCalculator.calculatePoints(transaction.getAmount());
        }

        // Retrieve the RewardPoints record for the given customer, month, and year
        List<RewardPoints> rewardPointsList = rewardPointsRepository.findByCustomerAndMonthAndYear(customer, month, year);
        RewardPoints rewardPoints;

        if (rewardPointsList.isEmpty()) {
            // If no reward points record exists, create a new one
            rewardPoints = new RewardPoints();
            rewardPoints.setCustomer(customer);  // Set the correct Customer object
            rewardPoints.setMonth(month);
            rewardPoints.setYear(year);
            rewardPoints.setPoints(totalPoints);  // Set the calculated total points
            rewardPointsRepository.save(rewardPoints);
        } else {
            // If a record exists, update the points
            rewardPoints = rewardPointsList.get(0); // Get the first matching record
            rewardPoints.setPoints(totalPoints);  // Update the points
            rewardPointsRepository.save(rewardPoints);
        }
log.info("updateRewardPoints finished running");
    }


}
