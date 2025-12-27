package com.spring.customer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.spring.customer.enumerator.Membership;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


public class CustomerDto {
   private UUID id;
   private String name;
   private  String email;
    private BigDecimal annualSpend;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastPurchaseDate;
    private  static Membership tier;

    public CustomerDto() { }

    public CustomerDto(UUID id, String name, String email, BigDecimal annualSpend, LocalDate lastPurchaseDate, Membership tier) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.annualSpend = annualSpend;
        this.lastPurchaseDate = lastPurchaseDate;
        this.tier = tier;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getAnnualSpend() {
        return annualSpend;
    }

    public void setAnnualSpend(BigDecimal annualSpend) {
        this.annualSpend = annualSpend;
    }

    public LocalDate getLastPurchaseDate() {
        return lastPurchaseDate;
    }

    public void setLastPurchaseDate(LocalDate lastPurchaseDate) {
        this.lastPurchaseDate = lastPurchaseDate;
    }

    public Membership getTier() {
        return tier;
    }

    public void setTier(Membership tier) {
        this.tier = tier;
    }
}

