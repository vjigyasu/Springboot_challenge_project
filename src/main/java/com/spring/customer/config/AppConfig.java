package com.spring.customer.config;

import com.spring.customer.dto.CustomerDto;
import com.spring.customer.entity.Customer;
import com.spring.customer.service.CustomerService;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {


    @Bean
    public ModelMapper modelMapper(){

        return   new ModelMapper();
    }

    // Swagger Open ApI
    @Bean
    public OpenAPI customerManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Customer Management API")
                        .description("APIs for managing customers, tiers, and transactions")
                        .version("1.0.0"));
    }
}
