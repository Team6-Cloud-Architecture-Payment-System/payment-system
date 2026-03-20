package com.example.paymentsystem.domain.refund.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateRefundRequest(
        @NotNull(message = "환불 결제건이 필요합니다.")
        Long paymentId,

        @NotNull(message = "환불 금액은 필수입니다.")
        @Positive(message = "환불 금액은 0보다 커야합니다.")
        Long refundPrice,

        @NotBlank(message = "환불 사유는 필수입니다.")
        @Size(min = 5, max = 50, message = "환불 사유는 5글자~50글자 이내입니다.")
        String refundReason
) {
}
