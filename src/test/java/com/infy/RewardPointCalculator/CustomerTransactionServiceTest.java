package com.infy.RewardPointCalculator;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.infy.RewardPointCalculator.dto.CustomerTransactionDTO;
import com.infy.RewardPointCalculator.model.Customer;
import com.infy.RewardPointCalculator.model.CustomerTransaction;
import com.infy.RewardPointCalculator.repository.CustomerRepository;
import com.infy.RewardPointCalculator.repository.CustomerTransactionRepository;
import com.infy.RewardPointCalculator.repository.RewardPointsRepository;
import com.infy.RewardPointCalculator.service.CustomerTransactionService;

import java.time.LocalDate;
import java.util.*;

public class CustomerTransactionServiceTest {

    @Mock
    private CustomerTransactionRepository transactionRepository;

    @Mock
    private RewardPointsRepository rewardPointsRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerTransactionService customerTransactionService;

    private CustomerTransactionDTO validTransactionDTO;
    private Customer customer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");

        validTransactionDTO = new CustomerTransactionDTO();
        validTransactionDTO.setAmount(120.0);
        validTransactionDTO.setSpentDetails("Shopping");
        validTransactionDTO.setTransactionDate(LocalDate.of(2025, 1, 10));
    }

    @Test
    public void testGetTransactions_ShouldReturnTransactions_WhenTransactionsExist() {
        CustomerTransaction transaction = new CustomerTransaction();
        transaction.setCustomer(customer);
        transaction.setAmount(120.0);
        transaction.setSpentDetails("Shopping");
        transaction.setDate(LocalDate.of(2025, 1, 10));
        List<CustomerTransaction> transactions = Collections.singletonList(transaction);
        
        when(transactionRepository.findByCustomerId(customer.getId())).thenReturn(transactions);

        List<CustomerTransaction> result = customerTransactionService.getTransactions(customer.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Shopping", result.get(0).getSpentDetails());
    }

    @Test
    public void testGetTransactions_ShouldThrowNotFound_WhenNoTransactionsExist() {
        when(transactionRepository.findByCustomerId(customer.getId())).thenReturn(Collections.emptyList());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customerTransactionService.getTransactions(customer.getId());
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No transactions found for this customer", exception.getReason());
    }

    @Test
    public void testAddTransaction_ShouldReturnTransaction_WhenValidData() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        CustomerTransaction savedTransaction = new CustomerTransaction();
        savedTransaction.setId(10L);
        savedTransaction.setCustomer(customer);
        savedTransaction.setAmount(120.0);
        savedTransaction.setSpentDetails("Shopping");
        savedTransaction.setDate(LocalDate.of(2025, 1, 10));

        when(transactionRepository.save(any(CustomerTransaction.class))).thenReturn(savedTransaction);

        CustomerTransaction result = customerTransactionService.addTransaction(customer.getId(), validTransactionDTO);

        assertNotNull(result);
        assertEquals("Shopping", result.getSpentDetails());
        assertEquals(120.0, result.getAmount());
    }

    @Test
    public void testAddTransaction_ShouldThrowBadRequest_WhenCustomerNotFound() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customerTransactionService.addTransaction(customer.getId(), validTransactionDTO);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("An error occurred while adding the transaction", exception.getReason());
    }

    @Test
    public void testEditTransaction_ShouldReturnUpdatedTransaction_WhenValidData() {
        CustomerTransaction existingTransaction = new CustomerTransaction();
        existingTransaction.setId(10L);
        existingTransaction.setCustomer(customer);
        existingTransaction.setAmount(100.0);
        existingTransaction.setSpentDetails("Old Transaction");
        existingTransaction.setDate(LocalDate.of(2025, 1, 10));

        when(transactionRepository.findByCustomerIdAndId(customer.getId(), 10L)).thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.save(any(CustomerTransaction.class))).thenReturn(existingTransaction);

        CustomerTransaction updatedTransaction = customerTransactionService.editTransaction(customer.getId(), 10L, validTransactionDTO);

        assertNotNull(updatedTransaction);
        assertEquals("Shopping", updatedTransaction.getSpentDetails());
        assertEquals(120.0, updatedTransaction.getAmount());
    }

    @Test
    public void testEditTransaction_ShouldThrowNotFound_WhenTransactionDoesNotExist() {
        when(transactionRepository.findByCustomerIdAndId(customer.getId(), 10L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customerTransactionService.editTransaction(customer.getId(), 10L, validTransactionDTO);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("An error occurred while editing the transaction", exception.getReason());
    }

    @Test
    public void testDeleteTransaction_ShouldSuccessfullyDeleteTransaction() {
        CustomerTransaction transactionToDelete = new CustomerTransaction();
        transactionToDelete.setId(10L);
        transactionToDelete.setCustomer(customer);
        transactionToDelete.setAmount(120.0);
        transactionToDelete.setSpentDetails("Shopping");
        transactionToDelete.setDate(LocalDate.of(2025, 1, 10));

        when(transactionRepository.findByCustomerIdAndId(customer.getId(), 10L)).thenReturn(Optional.of(transactionToDelete));
        doNothing().when(transactionRepository).delete(any(CustomerTransaction.class));

        customerTransactionService.deleteTransaction(customer.getId(), 10L);

        verify(transactionRepository, times(1)).delete(transactionToDelete);
    }

    @Test
    public void testDeleteTransaction_ShouldThrowNotFound_WhenTransactionDoesNotExist() {
        when(transactionRepository.findByCustomerIdAndId(customer.getId(), 10L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customerTransactionService.deleteTransaction(customer.getId(), 10L);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("An error occurred while deleting the transaction", exception.getReason());
    }

    @Test
    public void testDeleteTransaction_ShouldThrowNotFound_WhenCustomerDoesNotExist() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customerTransactionService.deleteTransaction(customer.getId(), 10L);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("An error occurred while deleting the transaction", exception.getReason());
    }
}
