package com.spring.customer.service.serviceimpl;

import com.spring.customer.dto.CustomerDto;
import com.spring.customer.entity.Customer;
import com.spring.customer.enumerator.Membership;
import com.spring.customer.exceptionhandling.CustomerNotFoundException;
import com.spring.customer.repository.CustomerRepo;
import com.spring.customer.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service

public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepo customerRepo;
    private final ModelMapper modelMapper;

    private static final Logger log =
            LoggerFactory.getLogger(CustomerServiceImpl.class);

    public CustomerServiceImpl(CustomerRepo customerRepo, ModelMapper modelMapper) {
        this.customerRepo = customerRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public CustomerDto createCustomer(Customer customer) {

        log.info("Request received to create customer. Email: {}", customer.getEmail());

        Customer savedCustomer = customerRepo.save(customer);

        log.debug("Customer saved successfully with id: {}", savedCustomer.getId());

        CustomerDto customerDto = modelMapper.map(savedCustomer, CustomerDto.class);
        customerDto.setTier(calculateTier(savedCustomer));

        log.info("Customer created successfully. Id: {}, Tier: {}",
                customerDto.getId(), customerDto.getTier());

        return customerDto;
    }


    @Override
    public CustomerDto getCustomerById(UUID id) {

        log.info("Fetching customer with id: {}", id);

        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer not found with id: {}", id);
                    return new CustomerNotFoundException(
                            "Customer not found with id: " + id
                    );
                });

        log.info("Customer found with id: {}", id);

        customer.setLastPurchaseDate(LocalDate.now());
        log.debug("Updated lastPurchaseDate for customer id: {}", id);

        Customer updatedCustomer = customerRepo.save(customer);
        log.info("Customer updated successfully with id: {}", id);

        CustomerDto response = modelMapper.map(updatedCustomer, CustomerDto.class);
        response.setTier(calculateTier(customer));

        log.info("Customer response prepared for id: {}, tier: {}",
                id, response.getTier());

        return response;
    }


    @Override
    public List<CustomerDto> getCustomerByName(String name) {

        log.info("Fetching customers with name: {}", name);

        List<CustomerDto> customerDtoList = customerRepo.findAllByName(name)
                .stream()
                .map(customer -> {
                    log.debug("Mapping customer entity with id: {}", customer.getId());

                    CustomerDto dto = modelMapper.map(customer, CustomerDto.class);
                    dto.setTier(calculateTier(customer));

                    log.debug("Calculated tier {} for customer id: {}",
                            dto.getTier(), customer.getId());

                    return dto;
                })
                .toList();

        if (customerDtoList.isEmpty()) {
            log.warn("No customers found with name: {}", name);
            throw new CustomerNotFoundException(
                    "Customer not found with name: " + name
            );
        }

        log.info("Successfully fetched {} customer(s) with name: {}",
                customerDtoList.size(), name);

        return customerDtoList;
    }


    @Override
    public CustomerDto getCustomerByEmail(String email) {

        log.info("Fetching customer with email: {}", email);

        Customer customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Customer not found with email: {}", email);
                    return new CustomerNotFoundException(
                            "Customer not found with email: " + email
                    );
                });

        log.debug("Customer found with id: {}", customer.getId());

        customer.setLastPurchaseDate(LocalDate.now());
        log.debug("Updated lastPurchaseDate for customer id: {}",
                customer.getId());

        Customer updatedCustomer = customerRepo.save(customer);
        log.info("Customer updated successfully with id: {}",
                updatedCustomer.getId());

        CustomerDto response = modelMapper.map(updatedCustomer, CustomerDto.class);
        response.setTier(calculateTier(customer));

        log.info("Returning customer response for email: {}", email);
        log.debug("Customer tier calculated as: {}", response.getTier());

        return response;
    }



    @Override
    public CustomerDto updateCustomer(CustomerDto customerDto, UUID id) {

        log.info("Updating customer with id: {}", id);

        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Customer not found with id: {}", id);
                    return new CustomerNotFoundException(
                            "Customer not found with id: " + id
                    );
                });

        log.debug("Existing customer found. Updating fields for id: {}", id);

        customer.setName(customerDto.getName());
        customer.setEmail(customerDto.getEmail());
        customer.setLastPurchaseDate(customerDto.getLastPurchaseDate());
        customer.setAnnualSpend(customerDto.getAnnualSpend());

        log.debug("Customer fields updated for id: {}", id);

        customerDto.setTier(calculateTier(customer));
        log.debug("Customer tier calculated as: {} for id: {}",
                customerDto.getTier(), id);

        Customer updatedCustomer = customerRepo.save(customer);
        log.info("Customer updated successfully with id: {}", id);

        CustomerDto response =
                modelMapper.map(updatedCustomer, CustomerDto.class);

        log.info("Returning updated customer response for id: {}", id);

        return response;
    }


    @Override
    @Transactional
    public void deleteCustomer(UUID id) {

        log.info("Request received to delete customer with id: {}", id);

        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Delete failed. Customer not found with id: {}", id);
                    return new RuntimeException(
                            "Customer not found or already removed with id: " + id
                    );
                });

        log.debug("Customer found for deletion. Id: {}, Name: {}",
                id, customer.getName());

        customerRepo.delete(customer);

        log.info("Customer deleted successfully. Id: {}, Name: {}",
                id, customer.getName());
    }


    @Override
    public Membership calculateTier(Customer customer) {

        log.debug("Calculating membership tier for customer. Id: {}",
                customer.getId());

        BigDecimal spend = customer.getAnnualSpend();
        double spends = spend != null ? spend.doubleValue() : 0.0;

        LocalDate lastPurchase = customer.getLastPurchaseDate();
        LocalDate now = LocalDate.now();

        log.debug("Customer spend: {}, Last purchase date: {}",
                spends, lastPurchase);

        Membership membership;

        if (spends >= 10000 && lastPurchase != null && lastPurchase.isAfter(now.minusMonths(6))) {
            membership = Membership.PLATINUM;
        } else if (spends >= 1000 && lastPurchase != null && lastPurchase.isAfter(now.minusMonths(12))) {
            membership = Membership.GOLD;
        } else {
            membership = Membership.SILVER;
        }

        log.info("Calculated membership tier: {} for customer Id: {}",
                membership, customer.getId());

        return membership;
    }

}
