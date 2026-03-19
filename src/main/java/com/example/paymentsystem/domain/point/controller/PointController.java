package com.example.paymentsystem.domain.point.controller;

//import com.example.paymentsystem.common.dto.ApiResponse;
//import com.example.paymentsystem.domain.point.dto.GetMyPointResponse;
//import com.example.paymentsystem.domain.point.service.PointService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/points")
//public class PointController {
//    private final PointService pointService;
//
//    // 현재 내 포인트 조회
//    @GetMapping("/me")
//    public ResponseEntity<ApiResponse<GetMyPointResponse>> getMyPoint(
//            @RequestParam Long userId
//    ) {
//        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(pointService.getMyPoint(userId)));
//    }
//
//}
