package com.dvpgiftcenter.controller;

import com.dvpgiftcenter.dto.admin.*;
import com.dvpgiftcenter.dto.common.ApiResponse;
import com.dvpgiftcenter.service.AdminOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminOrderController {

    @Autowired
    private AdminOrderService adminOrderService;

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<AdminOrderSummaryDto>>> listOrders(
            @RequestParam(value = "status", required = false) String status) {
        try {
            List<AdminOrderSummaryDto> list = adminOrderService.listOrders(status);
            return ResponseEntity.ok(ApiResponse.success("Orders retrieved", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve orders: " + e.getMessage()));
        }
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<AdminOrderDetailDto>> getOrder(@PathVariable Long orderId) {
        try {
            AdminOrderDetailDto dto = adminOrderService.getOrderDetail(orderId);
            return ResponseEntity.ok(ApiResponse.success("Order detail retrieved", dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve order: " + e.getMessage()));
        }
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<ApiResponse<AdminOrderDetailDto>> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        try {
            AdminOrderDetailDto dto = adminOrderService.updateOrderStatus(orderId, request.getStatus(), request.getTrackingNumber());
            return ResponseEntity.ok(ApiResponse.success("Order status updated", dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update order status: " + e.getMessage()));
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<AdminTransactionSummaryDto>>> listTransactions(
            @RequestParam(value = "source", required = false) String source) {
        try {
            List<AdminTransactionSummaryDto> list = adminOrderService.listTransactions(source);
            return ResponseEntity.ok(ApiResponse.success("Transactions retrieved", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve transactions: " + e.getMessage()));
        }
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<ApiResponse<AdminTransactionDetailDto>> getTransaction(@PathVariable Long transactionId) {
        try {
            AdminTransactionDetailDto dto = adminOrderService.getTransactionDetail(transactionId);
            return ResponseEntity.ok(ApiResponse.success("Transaction detail retrieved", dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve transaction: " + e.getMessage()));
        }
    }
}
