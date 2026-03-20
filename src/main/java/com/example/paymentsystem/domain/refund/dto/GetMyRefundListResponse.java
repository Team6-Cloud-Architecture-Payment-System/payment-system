package com.example.paymentsystem.domain.refund.dto;


import java.util.List;

public record GetMyRefundListResponse(
        List<RefundSummaryResponse> refunds,
        int currentPage,
        int size,
        long totalCount,
        int totalPages
){}
