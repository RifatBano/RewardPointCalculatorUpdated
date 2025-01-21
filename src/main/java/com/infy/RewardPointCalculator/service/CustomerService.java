package com.infy.RewardPointCalculator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.infy.RewardPointCalculator.dto.CustomerDTO;
import com.infy.RewardPointCalculator.model.Customer;
import com.infy.RewardPointCalculator.repository.CustomerRepository;
import com.infy.RewardPointCalculator.exception.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.ArrayList;
@Service
public class CustomerService implements UserDetailsService{

    @Autowired
    private CustomerRepository customerRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Customer register(CustomerDTO customerDTO) {
        try {
            if (customerDTO.getFirstName() == null || customerDTO.getFirstName().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "First name is required");
            }
            if (customerDTO.getLastName() == null || customerDTO.getLastName().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Last name is required");
            }
            if (customerDTO.getEmail() == null || customerDTO.getEmail().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
            }
            if (customerDTO.getPassword() == null || customerDTO.getPassword().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
            }

            // Create a new customer from the DTO and hash the password
            Customer customer = new Customer();
            customer.setFirstName(customerDTO.getFirstName());
            customer.setLastName(customerDTO.getLastName());
            customer.setEmail(customerDTO.getEmail());
            String encodedPassword = new BCryptPasswordEncoder().encode(customerDTO.getPassword());

            customer.setPassword(encodedPassword);  // Hashing the password
            
            return customerRepository.save(customer);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e);
        }
    }
   

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find the user by email from the database
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        // Return the user details with the email and encoded password
        return new org.springframework.security.core.userdetails.User(
                customer.getEmail(), customer.getPassword(), new ArrayList<>()
        );
    }
    
}