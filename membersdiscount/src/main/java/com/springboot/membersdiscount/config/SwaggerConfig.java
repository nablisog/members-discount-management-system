package com.springboot.membersdiscount.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private final String description = """
        This API provides endpoints for managing member data,
        applying discount eligibility rules, and integrating with
        external systems for member synchronization and automated email notifications.
        """;
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Member Integration & Discount API")
                        .version("1.0")
                        .description(description));
    }
}
