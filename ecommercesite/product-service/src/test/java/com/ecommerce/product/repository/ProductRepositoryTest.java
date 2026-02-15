package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Product Repository Tests")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Should save and find product by SKU code")
    void findBySkuCode_Success() {
        Product product = Product.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("29.99"))
                .skuCode("SKU-REPO-001")
                .category("Test")
                .active(true)
                .build();

        productRepository.save(product);

        Optional<Product> found = productRepository.findBySkuCode("SKU-REPO-001");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Product");
        assertThat(found.get().getPrice()).isEqualByComparingTo(new BigDecimal("29.99"));
    }

    @Test
    @DisplayName("Should return empty for non-existent SKU")
    void findBySkuCode_NotFound() {
        Optional<Product> found = productRepository.findBySkuCode("NON-EXISTENT");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should check if SKU exists")
    void existsBySkuCode_Success() {
        Product product = Product.builder()
                .name("Existing Product")
                .price(new BigDecimal("10.00"))
                .skuCode("SKU-EXISTS")
                .active(true)
                .build();

        productRepository.save(product);

        assertThat(productRepository.existsBySkuCode("SKU-EXISTS")).isTrue();
        assertThat(productRepository.existsBySkuCode("SKU-NOT-EXISTS")).isFalse();
    }
}
