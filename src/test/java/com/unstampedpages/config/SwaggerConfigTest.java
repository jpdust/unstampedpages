package com.unstampedpages.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwaggerConfigTest {

    private SwaggerConfig swaggerConfig;

    @BeforeEach
    void setUp() {
        swaggerConfig = new SwaggerConfig();
    }

    @Test
    void openAPI_shouldReturnNonNullOpenAPIInstance() {
        OpenAPI openAPI = swaggerConfig.openAPI();
        Info info = openAPI.getInfo();

        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("Unstamped Pages API", info.getTitle());
        assertEquals("0.0.1-SNAPSHOT", info.getVersion());
        assertNotNull(openAPI.getInfo().getContact());
        assertEquals("Unstamped Pages", openAPI.getInfo().getContact().getName());
    }

}
