package com.example.paymentsystem.domain.point.service;

import com.example.paymentsystem.domain.auth.entity.User;
import com.example.paymentsystem.domain.auth.repository.UserRepository;
import com.example.paymentsystem.domain.point.dto.GetMyPointResponse;
import com.example.paymentsystem.domain.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public GetMyPointResponse getMyPoint(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("유저를 찾지 못했습니다.")
        );
        return new GetMyPointResponse(user.getId(), user.getPoint());

    }
}
