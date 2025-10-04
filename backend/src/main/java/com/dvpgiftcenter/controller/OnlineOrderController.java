package com.dvpgiftcenter.controller;

import com.dvpgiftcenter.dto.common.ApiResponse;
import com.dvpgiftcenter.dto.order.OrderDetailDto;
import com.dvpgiftcenter.dto.order.OrderSummaryDto;
import com.dvpgiftcenter.service.OnlineOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/online/orders")
@CrossOrigin(origins = "*")
public class OnlineOrderController {

    @Autowired
    private OnlineOrderService onlineOrderService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderSummaryDto>>> getMyOrders(Authentication authentication) {
        try {
            String username = authentication.getName();
            List<OrderSummaryDto> orders = onlineOrderService.getMyOrders(username);
            return ResponseEntity.ok(ApiResponse.success("Orders fetched", orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to fetch orders: " + e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailDto>> getMyOrderDetail(@PathVariable Long orderId,
                                                                        Authentication authentication) {
        try {
            String username = authentication.getName();
            OrderDetailDto dto = onlineOrderService.getMyOrderDetail(username, orderId);
            return ResponseEntity.ok(ApiResponse.success("Order detail fetched", dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to fetch order detail: " + e.getMessage()));
        }
    }
}
