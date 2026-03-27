package com.example.paymentsystem.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WebhookRequestDto(

        @JsonProperty("type")
       String eventStatus,
       WebhookData data
){}
