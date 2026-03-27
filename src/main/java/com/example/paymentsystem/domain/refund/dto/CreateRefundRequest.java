package com.example.paymentsystem.domain.refund.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRefundRequest(

        @NotBlank(message = "환불 사유는 필수입니다.")
        @Size(min = 5, max = 50, message = "환불 사유는 5글자~50글자 이내입니다.")
        String refundReason
) {}
