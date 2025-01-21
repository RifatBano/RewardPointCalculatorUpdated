package com.infy.RewardPointCalculator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infy.RewardPointCalculator.model.Customer;
import com.infy.RewardPointCalculator.model.RewardPoints;

@Repository
public interface RewardPointsRepository extends JpaRepository<RewardPoints, Long> {
	List<RewardPoints> findByCustomer(Customer customer);
    List<RewardPoints> findByCustomerAndMonthAndYear(Customer customer, Integer month, Integer year);

}
