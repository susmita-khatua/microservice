package com.ecommerce.product;

import com.ecommerce.product.dto.ProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Product Service Integration Tests")
class ProductServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Full product lifecycle: create → get → update → delete")
    void productLifecycle() throws Exception {
        // Create
        ProductRequest createRequest = ProductRequest.builder()
                .name("Integration Test Product")
                .description("Created by integration test")
                .price(new BigDecimal("199.99"))
                .skuCode("SKU-INT-001")
                .category("Integration")
                .build();

        String createResponse = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Integration Test Product"))
                .andReturn().getResponse().getContentAsString();

        // Extract ID from response
        Long productId = objectMapper.readTree(createResponse).path("data").path("id").asLong();

        // Get
        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.skuCode").value("SKU-INT-001"));

        // Update
        ProductRequest updateRequest = ProductRequest.builder()
                .name("Updated Integration Product")
                .description("Updated by integration test")
                .price(new BigDecimal("249.99"))
                .skuCode("SKU-INT-001")
                .category("Integration")
                .build();

        mockMvc.perform(put("/api/products/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Integration Product"));

        // Delete (soft delete)
        mockMvc.perform(delete("/api/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
