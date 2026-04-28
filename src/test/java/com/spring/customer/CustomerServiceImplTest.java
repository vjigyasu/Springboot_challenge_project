package com.spring.customer;

import com.spring.customer.dto.CustomerDto;
import com.spring.customer.entity.Customer;
import com.spring.customer.enumerator.Membership;
import com.spring.customer.exceptionhandling.CustomerNotFoundException;
import com.spring.customer.exceptionhandling.GlobalExceptionHandler;
import com.spring.customer.repository.CustomerRepo;
import com.spring.customer.response.ApiResponse;
import com.spring.customer.service.serviceimpl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    private CustomerRepo customerRepo;
    private ModelMapper modelMapper;
    private CustomerServiceImpl service;

    @BeforeEach
    void setup() {
        customerRepo = mock(CustomerRepo.class);
        modelMapper = new ModelMapper(); // real mapper is fine
        service = new CustomerServiceImpl(customerRepo, modelMapper);
    }

    private Customer buildCustomer() {
        Customer c = new Customer();
        c.setId(UUID.randomUUID());
        c.setName("Test");
        c.setEmail("test@test.com");
        c.setAnnualSpend(BigDecimal.valueOf(20000));
        c.setLastPurchaseDate(LocalDate.now());
        return c;
    }


    @Test
    void shouldCreateCustomerSuccessfully() {
        Customer customer = buildCustomer();

        when(customerRepo.save(any())).thenReturn(customer);

        CustomerDto result = service.createCustomer(customer);

        assertNotNull(result);
        assertEquals(customer.getEmail(), result.getEmail());
        assertNotNull(result.getTier());
        verify(customerRepo, times(1)).save(customer);
    }


    @Test
    void shouldReturnCustomerById() {
        Customer customer = buildCustomer();

        when(customerRepo.findById(customer.getId()))
                .thenReturn(Optional.of(customer));
        when(customerRepo.save(any())).thenReturn(customer);

        CustomerDto result = service.getCustomerById(customer.getId());

        assertNotNull(result);
        verify(customerRepo).findById(customer.getId());
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFoundById() {
        UUID id = UUID.randomUUID();

        when(customerRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> service.getCustomerById(id));
    }


    @Test
    void shouldReturnCustomersByName() {
        Customer customer = buildCustomer();

        when(customerRepo.findAllByName("Test"))
                .thenReturn(List.of(customer));

        List<CustomerDto> result = service.getCustomerByName("Test");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void shouldThrowExceptionWhenNameNotFound() {
        when(customerRepo.findAllByName("Unknown"))
                .thenReturn(Collections.emptyList());

        assertThrows(CustomerNotFoundException.class,
                () -> service.getCustomerByName("Unknown"));
    }


    @Test
    void shouldReturnCustomerByEmail() {
        Customer customer = buildCustomer();

        when(customerRepo.findByEmail(customer.getEmail()))
                .thenReturn(Optional.of(customer));
        when(customerRepo.save(any())).thenReturn(customer);

        CustomerDto result = service.getCustomerByEmail(customer.getEmail());

        assertNotNull(result);
    }

    @Test
    void shouldThrowExceptionWhenEmailNotFound() {
        when(customerRepo.findByEmail("wrong@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> service.getCustomerByEmail("wrong@test.com"));
    }


    @Test
    void shouldUpdateCustomer() {
        Customer customer = buildCustomer();

        CustomerDto dto = new CustomerDto();
        dto.setName("Updated");
        dto.setEmail("updated@test.com");
        dto.setAnnualSpend(BigDecimal.valueOf(5000));
        dto.setLastPurchaseDate(LocalDate.now());

        when(customerRepo.findById(customer.getId()))
                .thenReturn(Optional.of(customer));
        when(customerRepo.save(any())).thenReturn(customer);

        CustomerDto result = service.updateCustomer(dto, customer.getId());

        assertNotNull(result);
        verify(customerRepo).save(any());
    }


    @Test
    void shouldDeleteCustomer() {
        Customer customer = buildCustomer();

        when(customerRepo.findById(customer.getId()))
                .thenReturn(Optional.of(customer));

        service.deleteCustomer(customer.getId());

        verify(customerRepo).delete(customer);
    }

    @Test
    void shouldThrowExceptionWhenDeleteCustomerNotFound() {
        UUID id = UUID.randomUUID();

        when(customerRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.deleteCustomer(id));
    }

    @Test
    void shouldReturnPlatinumTier() {
        Customer customer = buildCustomer();
        customer.setAnnualSpend(BigDecimal.valueOf(20000));
        customer.setLastPurchaseDate(LocalDate.now());

        Membership tier = service.calculateTier(customer);

        assertEquals(Membership.PLATINUM, tier);
    }

    @Test
    void shouldReturnGoldTier() {
        Customer customer = buildCustomer();
        customer.setAnnualSpend(BigDecimal.valueOf(2000));
        customer.setLastPurchaseDate(LocalDate.now().minusMonths(6));

        Membership tier = service.calculateTier(customer);

        assertEquals(Membership.GOLD, tier);
    }

    @Test
    void shouldReturnSilverTier() {
        Customer customer = buildCustomer();
        customer.setAnnualSpend(BigDecimal.valueOf(100));
        customer.setLastPurchaseDate(LocalDate.now().minusYears(2));

        Membership tier = service.calculateTier(customer);

        assertEquals(Membership.SILVER, tier);
    }
}