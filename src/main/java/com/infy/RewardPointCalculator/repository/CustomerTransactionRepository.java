package com.infy.RewardPointCalculator.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infy.RewardPointCalculator.model.CustomerTransaction;

@Repository
public interface CustomerTransactionRepository extends JpaRepository<CustomerTransaction, Long> {

	List<CustomerTransaction> findByCustomerId(Long customerId);
	Optional<CustomerTransaction> findByCustomerIdAndId(Long customerId, Long transactionId);
	List<CustomerTransaction> findByCustomerIdAndDateBetween(Long customerId, LocalDate of, LocalDate withDayOfMonth);

	}
