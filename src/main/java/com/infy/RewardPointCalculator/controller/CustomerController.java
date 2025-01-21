package com.infy.RewardPointCalculator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.infy.RewardPointCalculator.dto.CustomerDTO;
import com.infy.RewardPointCalculator.model.Customer;
import com.infy.RewardPointCalculator.service.CustomerService;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<Customer> register(@RequestBody @Valid CustomerDTO customerDTO) {
        try{
        	Customer newCustomer = customerService.register(customerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCustomer);
    }catch (DataIntegrityViolationException e) {
        // Handling case where email already exists (unique constraint violation)
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use", e);
    } catch (Exception e) {
        // Handling unexpected exceptions
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while registering the customer", e);
    }
    }
}