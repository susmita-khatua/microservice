package com.ecommerce.payment.dto;

import com.ecommerce.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private String orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String method;
    private String transactionId;
    private LocalDateTime createdAt;
}
