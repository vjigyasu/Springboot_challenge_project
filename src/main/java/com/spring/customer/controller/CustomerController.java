package com.spring.customer.controller;

import com.spring.customer.dto.CustomerDto;
import com.spring.customer.entity.Customer;
import com.spring.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path="/Customer/api")
@Tag(name = "Customer API", description = "Operations related to customers")
public class CustomerController {
    private final CustomerService customerService;
    CustomerController(CustomerService customerService ){
        this.customerService=customerService;
    }

    @PostMapping(path="/customers")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new Customer", description = "Its create new customer")
    CustomerDto AddCustomer(@RequestBody @Valid Customer customer){

        CustomerDto customerDto = this.customerService.createCustomer(customer);
        //System.out.println(customer.getId()+" "+ customer.getEmail());
        return customerDto;
    }

    @GetMapping(path="/customers/{id}")
    @Operation(summary = "Get customer by ID", description = "Fetch a customer using its ID")
    CustomerDto getCustomerbyId(@PathVariable UUID id){

        return this.customerService.getCustomerById(id);
    }

    @GetMapping(path="/customers")
    @Operation(summary = "Get customer by name", description = "Fetch a customer using its name")
    List<CustomerDto> getCustomerByName(@RequestParam(required = true) String name){

        return this.customerService.getCustomerByName(name);
    }

    @GetMapping(path="/customers/byEmail")
    @Operation(summary = "Get customer by email", description = "Fetch a customer using its email")
    CustomerDto getCustomerByEmail(@RequestParam(required = true) String email){
       // System.out.println(email);
        return this.customerService.getCustomerByEmail(email);
    }

    @PutMapping(path="/customers/{id}")
    @Operation(summary = "Update Customer By Id", description = "Updating the customer by Id")
    CustomerDto updateCustomer(@RequestBody @Valid CustomerDto customerDto, @PathVariable UUID id){

        return this.customerService.updateCustomer(customerDto,id);
    }

    @DeleteMapping(path = "/customers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete Customer by Id", description = "It deleted customer by id")
    void deleteCustomer(@PathVariable UUID id){
       this.customerService.deleteCustomer(id);
    }
}
