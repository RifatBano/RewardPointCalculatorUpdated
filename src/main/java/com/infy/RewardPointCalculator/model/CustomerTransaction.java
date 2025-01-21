package com.infy.RewardPointCalculator.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;


@Entity
public class CustomerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customer customer;

    private Double amount;
    private String spentDetails;
    private LocalDate date;
    
    
	public CustomerTransaction() {
		super();
	}
	public CustomerTransaction(Long id, Customer customer, Double amount, String spentDetails, LocalDate date) {
		super();
		this.id = id;
		this.customer = customer;
		this.amount = amount;
		this.spentDetails = spentDetails;
		this.date = date;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getSpentDetails() {
		return spentDetails;
	}
	public void setSpentDetails(String spentDetails) {
		this.spentDetails = spentDetails;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}

    
}
