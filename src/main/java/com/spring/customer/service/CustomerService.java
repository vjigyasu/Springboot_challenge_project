package com.spring.customer.service;

import com.spring.customer.dto.CustomerDto;
import com.spring.customer.entity.Customer;
import com.spring.customer.enumerator.Membership;

import java.util.List;
import java.util.UUID;

public interface CustomerService {

    CustomerDto createCustomer(Customer customer);
    CustomerDto getCustomerById(UUID id);
    List<CustomerDto> getCustomerByName(String name);
    CustomerDto getCustomerByEmail(String email);
    CustomerDto updateCustomer(CustomerDto customerDto,UUID id);
    void deleteCustomer(UUID id);
     Membership calculateTier(Customer customer);

}
