package com.example.paymentsystem.domain.pointHistory.dto;

import com.example.paymentsystem.domain.pointHistory.entity.PointHistory;
import com.example.paymentsystem.domain.pointHistory.entity.Type;

import java.time.LocalDateTime;
// 응답 목록에 들어갈 데이터 한 건 정의하는 DTO,
// ex) 편지 1장에 적혀있는 내용
public record PointHistorySummaryResponse(
        Long id,
        Long orderId,
        Long point,
        Type type,
        LocalDateTime createdAt,
        LocalDateTime expiredAt
) {
    public PointHistorySummaryResponse(PointHistory pointHistory) {
        this(
                pointHistory.getId(),
                pointHistory.getOrder().getId() != null ? pointHistory.getOrder().getId() : null,
                // OrderId는 NOTNULL이 아니기 때문에, null 체크
                pointHistory.getPoint(),
                pointHistory.getType(),
                pointHistory.getCreatedAt(),
                pointHistory.getExpiredAt()
        );
    }
}

