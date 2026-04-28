package com.spring.customer.repository;

import com.spring.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
@Transactional
public interface CustomerRepo extends JpaRepository<Customer, UUID> {


    List<Customer> findAllByName(String name);
    Optional<Customer> findByEmail(String email);

}
