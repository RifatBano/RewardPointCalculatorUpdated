package com.infy.RewardPointCalculator.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infy.RewardPointCalculator.Util.UserUtil;
import com.infy.RewardPointCalculator.model.Customer;
import com.infy.RewardPointCalculator.model.RewardPoints;
import com.infy.RewardPointCalculator.repository.CustomerRepository;
import com.infy.RewardPointCalculator.service.RewardPointsService;


@RestController
@RequestMapping("/api/customers/reward-points")
public class RewardPointController {

    @Autowired
    private RewardPointsService rewardPointsService;

    @Autowired
    private CustomerRepository customerRepository;  // To fetch customer details

    // Get reward points for the logged-in customer, specific month and year
    @GetMapping("/{month}/{year}")
    public ResponseEntity<RewardPoints> getRewardPoints(@PathVariable Integer month, @PathVariable Integer year) {
        String loggedInUsername = UserUtil.getLoggedInUsername();  // Get logged-in username

        if (loggedInUsername == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // If no user is logged in
        }

        // this will Find the customer by email (username)
        Customer customer = customerRepository.findByEmail(loggedInUsername)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // this will Fetch reward points for the authenticated user
        RewardPoints rewardPoints = rewardPointsService.getRewardPoints(customer.getId(), month, year);
        return ResponseEntity.ok(rewardPoints);
    }

    // Get all reward points for the logged-in customer
    @GetMapping("/all")
    public ResponseEntity<List<RewardPoints>> getAllRewardPoints() {
        String loggedInUsername = UserUtil.getLoggedInUsername();  // Get logged-in username

        if (loggedInUsername == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // If no user is logged in
        }

        // this will Find the customer by email (username)
        Customer customer = customerRepository.findByEmail(loggedInUsername)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // this will Fetch all reward points for the authenticated user
        List<RewardPoints> allPoints = rewardPointsService.getAllRewardPoints(customer.getId());
        return ResponseEntity.ok(allPoints);
    }
}
