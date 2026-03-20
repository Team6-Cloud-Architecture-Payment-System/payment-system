package com.example.paymentsystem.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PortOneResponseDto {

    //가져와야할 항목 : paymentId=(포트원:id), 결제 상태(성공인지), 결제된 총금액
    @JsonProperty("id")
    String status;
    String id;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentAmount{
        private int total;
    }

}
