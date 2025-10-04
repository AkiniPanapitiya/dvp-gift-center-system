package com.dvpgiftcenter.service;

import com.dvpgiftcenter.dto.order.OrderDetailDto;
import com.dvpgiftcenter.dto.order.OrderItemDto;
import com.dvpgiftcenter.dto.order.OrderSummaryDto;
import com.dvpgiftcenter.entity.OnlineOrder;
import com.dvpgiftcenter.entity.Transaction;
import com.dvpgiftcenter.entity.TransactionItem;
import com.dvpgiftcenter.entity.User;
import com.dvpgiftcenter.repository.OnlineOrderRepository;
import com.dvpgiftcenter.repository.TransactionItemRepository;
import com.dvpgiftcenter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OnlineOrderService {

    @Autowired
    private OnlineOrderRepository onlineOrderRepository;

    @Autowired
    private TransactionItemRepository transactionItemRepository;

    @Autowired
    private UserRepository userRepository;

    public List<OrderSummaryDto> getMyOrders(String username) {
        User user = userRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<OnlineOrder> orders = onlineOrderRepository.findByCustomerUserIdOrderByPlacedAtDesc(user.getUserId());
        return orders.stream()
                .sorted(Comparator.comparing(OnlineOrder::getPlacedAt).reversed())
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }

    public OrderDetailDto getMyOrderDetail(String username, Long orderId) {
        User user = userRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<OnlineOrder> opt = onlineOrderRepository.findById(orderId);
        OnlineOrder order = opt.orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getCustomer().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Access denied");
        }

        return toDetailDto(order);
    }

    private OrderSummaryDto toSummaryDto(OnlineOrder order) {
        OrderSummaryDto dto = new OrderSummaryDto();
        dto.setOrderId(order.getOrderId());
        if (order.getTransaction() != null) {
            dto.setTransactionId(order.getTransaction().getTransactionId());
            dto.setBillNumber(order.getTransaction().getBillNumber());
            dto.setTotalAmount(order.getTransaction().getTotalAmount());
            dto.setTaxAmount(order.getTransaction().getTaxAmount());
            dto.setNetAmount(order.getTransaction().getNetAmount());
        }
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPlacedAt(order.getPlacedAt());
        dto.setShippingMethod(order.getShippingMethod());
        return dto;
    }

    private OrderDetailDto toDetailDto(OnlineOrder order) {
        OrderDetailDto dto = new OrderDetailDto();
        dto.setOrderId(order.getOrderId());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPlacedAt(order.getPlacedAt());
        dto.setShippingMethod(order.getShippingMethod());
        dto.setTrackingNumber(order.getTrackingNumber());

        if (order.getShippingAddress() != null) {
            dto.setAddressLine1(order.getShippingAddress().getAddressLine1());
            dto.setAddressLine2(order.getShippingAddress().getAddressLine2());
            dto.setCity(order.getShippingAddress().getCity());
            dto.setPostalCode(order.getShippingAddress().getPostalCode());
        }

        Transaction transaction = order.getTransaction();
        if (transaction != null) {
            dto.setTransactionId(transaction.getTransactionId());
            dto.setBillNumber(transaction.getBillNumber());
            dto.setTotalAmount(transaction.getTotalAmount());
            dto.setTaxAmount(transaction.getTaxAmount());
            dto.setNetAmount(transaction.getNetAmount());

            List<TransactionItem> items = transactionItemRepository.findByTransactionTransactionId(transaction.getTransactionId());
            List<OrderItemDto> itemDtos = items.stream().map(this::toItemDto).collect(Collectors.toList());
            dto.setItems(itemDtos);
        }

        return dto;
    }

    private OrderItemDto toItemDto(TransactionItem ti) {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(ti.getProduct().getProductId());
        dto.setProductName(ti.getProduct().getProductName());
        dto.setQuantity(ti.getQuantity());
        dto.setUnitPrice(ti.getUnitPrice());
        dto.setLineTotal(ti.getLineTotal());
        dto.setImageUrl(ti.getProduct().getImageUrl());
        return dto;
    }
}
