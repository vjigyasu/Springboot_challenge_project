package com.spring.customer;

import com.spring.customer.dto.CustomerDto;
import com.spring.customer.entity.Customer;
import com.spring.customer.enumerator.Membership;
import com.spring.customer.exceptionhandling.CustomerNotFoundException;
import com.spring.customer.repository.CustomerRepo;
import com.spring.customer.service.serviceimpl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepo customerRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setName("Sonu");
        customer.setEmail("sonu@gmail.com");
        customer.setAnnualSpend(BigDecimal.valueOf(12000));
        customer.setLastPurchaseDate(LocalDate.now().minusMonths(2));

        customerDto = new CustomerDto();
        customerDto.setId(customer.getId());
        customerDto.setName(customer.getName());
        customerDto.setEmail(customer.getEmail());
    }

    @Test
    void createCustomer_shouldReturnCustomerDtoWithTier() {
        when(customerRepo.save(any(Customer.class))).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        CustomerDto result = customerService.createCustomer(customer);
        assertNotNull(result);
        assertEquals(customer.getId(), result.getId());
        assertEquals(Membership.PLATINUM, result.getTier());

        verify(customerRepo, times(1)).save(customer);
        verify(modelMapper, times(1))
                .map(customer, CustomerDto.class);
    }

    @Test
    void getCustomerById_shouldReturnCustomerDto_whenCustomerExists() {
        UUID id = UUID.randomUUID();

        Customer customer = new Customer();
        customer.setId(id);
        customer.setName("Sonu");
        customer.setEmail("sonu@gmail.com");
        customer.setAnnualSpend(BigDecimal.valueOf(12000));
        customer.setLastPurchaseDate(LocalDate.now().minusMonths(2));

        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(id);
        customerDto.setName("Sonu");
        customerDto.setEmail("sonu@gmail.com");

        when(customerRepo.findById(id)).thenReturn(Optional.of(customer));
        when(customerRepo.save(any(Customer.class))).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto result = customerService.getCustomerById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(Membership.PLATINUM, result.getTier());

        verify(customerRepo).findById(id);
        verify(customerRepo).save(customer);
        verify(modelMapper).map(customer, CustomerDto.class);
    }

    @Test
    void getCustomerById_shouldThrowException_whenCustomerNotFound() {
        UUID id = UUID.randomUUID();

        when(customerRepo.findById(id)).thenReturn(Optional.empty());

        CustomerNotFoundException ex = assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomerById(id)
        );

        assertEquals("Customer not found with id: " + id, ex.getMessage());

        verify(customerRepo).findById(id);
        verify(customerRepo, never()).save(any());
    }

    @Test
    void getCustomerByEmail_shouldReturnCustomerDto_whenExists() {
        String email = "sonu@gmail.com";

        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setEmail(email);
        customer.setAnnualSpend(BigDecimal.valueOf(2000));
        customer.setLastPurchaseDate(LocalDate.now().minusMonths(6));

        CustomerDto customerDto = new CustomerDto();
        customerDto.setEmail(email);

        when(customerRepo.findByEmail(email)).thenReturn(Optional.of(customer));
        when(customerRepo.save(any(Customer.class))).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto result = customerService.getCustomerByEmail(email);

        assertNotNull(result);
        assertEquals(Membership.GOLD, result.getTier());

        verify(customerRepo).findByEmail(email);
        verify(customerRepo).save(customer);
    }

    @Test
    void getCustomerByEmail_shouldThrowException_whenNotFound() {
        String email = "notfound@gmail.com";

        when(customerRepo.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomerByEmail(email)
        );

        verify(customerRepo).findByEmail(email);
    }

    @Test
    void getCustomerByName_shouldReturnListOfCustomerDto() {
        String name = "Sonu";

        Customer customer = new Customer();
        customer.setName(name);
        customer.setAnnualSpend(BigDecimal.valueOf(500));
        customer.setLastPurchaseDate(LocalDate.now());

        CustomerDto dto = new CustomerDto();
        dto.setName(name);

        when(customerRepo.findAllByName(name)).thenReturn(List.of(customer));
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(dto);

        List<CustomerDto> result = customerService.getCustomerByName(name);

        assertEquals(1, result.size());
        assertEquals(Membership.SILVER, result.get(0).getTier());

        verify(customerRepo).findAllByName(name);
    }

    @Test
    void updateCustomer_shouldThrowException_whenCustomerNotFound() {
        UUID id = UUID.randomUUID();
        CustomerDto dto = new CustomerDto();

        when(customerRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.updateCustomer(dto, id)
        );

        verify(customerRepo).findById(id);
    }

    @Test
    void deleteCustomer_shouldDeleteCustomer_whenExists() {
        UUID id = UUID.randomUUID();

        Customer customer = new Customer();
        customer.setId(id);

        when(customerRepo.findById(id)).thenReturn(Optional.of(customer));

        customerService.deleteCustomer(id);

        verify(customerRepo).delete(customer);
    }
}
