package com.spring.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.customer.controller.CustomerController;
import com.spring.customer.dto.CustomerDto;
import com.spring.customer.entity.Customer;
import com.spring.customer.enumerator.Membership;
import com.spring.customer.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    // Create Customer
    @Test
    void addCustomer_shouldReturn201() throws Exception {

        Customer customer = new Customer();
        customer.setName("sonu");
        customer.setEmail("hhh@gmail.com");
        customer.setAnnualSpend(BigDecimal.valueOf(2000));

        CustomerDto request = new CustomerDto(
                null,
                "sonu",
                "hhh@gmail.com",
                BigDecimal.valueOf(2000),
                LocalDate.of(2025, 10, 12),
                Membership.GOLD
        );

        when(customerService.createCustomer(any(Customer.class)))
                .thenReturn(request);

        mockMvc.perform(post("/Customer/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("sonu"))
                .andExpect(jsonPath("$.data.email").value("hhh@gmail.com"))
                .andExpect(jsonPath("$.data.tier").value("GOLD"))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Success"));

    }

    // get by Id
    @Test
    void getCustomerById_shouldReturnCustomer() throws Exception {
        UUID id = UUID.randomUUID();

        CustomerDto response = new CustomerDto();
        response.setId(id);
        response.setName("sonu");
        response.setEmail("hhh@gmail.com");
        response.setAnnualSpend(BigDecimal.valueOf(2000));
        response.setLastPurchaseDate(LocalDate.of(2025, 10, 12));
        response.setTier(Membership.GOLD);

        when(customerService.getCustomerById(id)).thenReturn(response);

        mockMvc.perform(get("/Customer/api/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.name").value("sonu"));

    }

    // get customer by name
    @Test
    void getCustomerByName_shouldReturnList() throws Exception {

        UUID id = UUID.randomUUID();

        List<CustomerDto> list = List.of(
                new CustomerDto(
                        id,
                        "sonu",
                        "hhh@gmail.com",
                        BigDecimal.valueOf(2000),
                        LocalDate.of(2025, 10, 12),
                        Membership.SILVER
                )
        );

        when(customerService.getCustomerByName("sonu")).thenReturn(list);

        mockMvc.perform(get("/Customer/api/customers")
                        .param("name", "sonu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("sonu"))
                .andExpect(jsonPath("$.data[0].tier").value("SILVER"));
    }


     // get by email
    @Test
    void getCustomerByEmail_shouldReturnCustomer() throws Exception {

        UUID id = UUID.randomUUID();

        CustomerDto response = new CustomerDto(
                id,
                "sonu",
                "hhh@gmail.com",
                BigDecimal.valueOf(2000),
                LocalDate.of(2025, 10, 12),
                Membership.GOLD
        );

        when(customerService.getCustomerByEmail("hhh@gmail.com"))
                .thenReturn(response);

        mockMvc.perform(get("/Customer/api/customers/byEmail")
                        .param("email", "hhh@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("hhh@gmail.com"));
    }


    // update customer
    @Test
    void updateCustomer_shouldReturnUpdatedCustomer() throws Exception {

        UUID id = UUID.randomUUID();

        CustomerDto updated = new CustomerDto(
                id,
                "sonu",
                "hhh@gmail.com",
                BigDecimal.valueOf(2000),
                LocalDate.of(2025, 10, 12),
                Membership.GOLD
        );

        when(customerService.updateCustomer(any(CustomerDto.class), eq(id)))
                .thenReturn(updated);

        mockMvc.perform(put("/Customer/api/customers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.tier").value("GOLD"));
    }


    // delete
    @Test
    void deleteCustomer_shouldReturn204() throws Exception {

        UUID id = UUID.randomUUID();

        doNothing().when(customerService).deleteCustomer(id);

        mockMvc.perform(delete("/Customer/api/customers/{id}", id))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).deleteCustomer(id);
    }

}
