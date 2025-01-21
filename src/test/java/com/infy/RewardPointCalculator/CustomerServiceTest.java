package com.infy.RewardPointCalculator;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.infy.RewardPointCalculator.dto.CustomerDTO;
import com.infy.RewardPointCalculator.model.Customer;
import com.infy.RewardPointCalculator.repository.CustomerRepository;
import com.infy.RewardPointCalculator.service.CustomerService;

public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CustomerDTO validCustomerDTO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        validCustomerDTO = new CustomerDTO();
        validCustomerDTO.setFirstName("John");
        validCustomerDTO.setLastName("Doe");
        validCustomerDTO.setEmail("john.doe@example.com");
        validCustomerDTO.setPassword("securePassword123");
    }

    @Test
    public void testRegister_ShouldReturnSavedCustomer_WhenValidData() {
        Customer savedCustomer = new Customer();
        savedCustomer.setFirstName(validCustomerDTO.getFirstName());
        savedCustomer.setLastName(validCustomerDTO.getLastName());
        savedCustomer.setEmail(validCustomerDTO.getEmail());
        String encodedPassword = new BCryptPasswordEncoder().encode(validCustomerDTO.getPassword());
        savedCustomer.setPassword("encodedPassword");

        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        Customer result = customerService.register(validCustomerDTO);

        assertNotNull(result);
        assertEquals(validCustomerDTO.getFirstName(), result.getFirstName());
        assertEquals(validCustomerDTO.getLastName(), result.getLastName());
        assertEquals(validCustomerDTO.getEmail(), result.getEmail());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    public void testRegister_ShouldThrowBadRequest_WhenFirstNameIsNull() {
        validCustomerDTO.setFirstName(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customerService.register(validCustomerDTO);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("First name is required", exception.getReason());
    }

    @Test
    public void testRegister_ShouldThrowBadRequest_WhenLastNameIsEmpty() {
        validCustomerDTO.setLastName("");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customerService.register(validCustomerDTO);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Last name is required", exception.getReason());
    }

    @Test
    public void testRegister_ShouldThrowBadRequest_WhenEmailIsNull() {
        validCustomerDTO.setEmail(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customerService.register(validCustomerDTO);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Email is required", exception.getReason());
    }

    @Test
    public void testRegister_ShouldThrowBadRequest_WhenPasswordIsEmpty() {
        validCustomerDTO.setPassword("");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customerService.register(validCustomerDTO);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Password is required", exception.getReason());
    }

    @Test
    public void testRegister_ShouldThrowConflict_WhenEmailAlreadyExists() {
        when(customerRepository.save(any(Customer.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customerService.register(validCustomerDTO);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Email already in use", exception.getReason());
    }

    @Test
    public void testRegister_ShouldThrowInternalServerError_WhenUnexpectedErrorOccurs() {
        when(customerRepository.save(any(Customer.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            customerService.register(validCustomerDTO);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("An unexpected error occurred", exception.getReason());
    }
}
