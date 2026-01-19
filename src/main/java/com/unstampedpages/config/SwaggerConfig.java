package com.unstampedpages.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Unstamped Pages API")
                        .description("Backend API for Unstamped Pages")
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact().name("Unstamped Pages")));
    }
}
