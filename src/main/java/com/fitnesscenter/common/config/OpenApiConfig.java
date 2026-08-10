package com.fitnesscenter.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fitnessCenterOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title(
                                        "Fitness Center Management System API"
                                )
                                .version("1.0.0")
                                .description(
                                        "REST API for the Automated Fitness Center Management System"
                                )
                                .contact(
                                        new Contact()
                                                .name("Fitness Center Development Team")
                                )
                );
    }
}