package com.example.paymentsystem.domain.pointHistory.dto;

import com.example.paymentsystem.domain.pointHistory.entity.PointHistory;
import com.example.paymentsystem.domain.pointHistory.entity.Type;

import java.time.LocalDateTime;
import java.util.List;

// 최종 응답용 커스텀 DTO
public record GetPointTransactionHistory (
        // 편지 묶음
        List<PointHistorySummaryResponse> pointHistory,
        // 응답에 나갈 Page 필드값들
        int currentPage,
        int size,
        long totalCount,
        int totalPages
) {}

