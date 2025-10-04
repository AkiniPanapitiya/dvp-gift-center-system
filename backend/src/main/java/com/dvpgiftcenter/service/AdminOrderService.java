package com.dvpgiftcenter.service;

import com.dvpgiftcenter.dto.admin.*;
import com.dvpgiftcenter.entity.OnlineOrder;
import com.dvpgiftcenter.entity.Payment;
import com.dvpgiftcenter.entity.Transaction;
import com.dvpgiftcenter.entity.TransactionItem;
import com.dvpgiftcenter.repository.OnlineOrderRepository;
import com.dvpgiftcenter.repository.PaymentRepository;
import com.dvpgiftcenter.repository.TransactionItemRepository;
import com.dvpgiftcenter.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminOrderService {

    @Autowired
    private OnlineOrderRepository onlineOrderRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionItemRepository transactionItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public List<AdminOrderSummaryDto> listOrders(String status) {
        List<OnlineOrder> orders;
        if (status != null && !status.isBlank()) {
            orders = onlineOrderRepository.findByOrderStatusOrderByPlacedAtDesc(status);
        } else {
            orders = onlineOrderRepository.findAll();
        }
        return orders.stream()
                .sorted(Comparator.comparing(OnlineOrder::getPlacedAt).reversed())
                .map(this::toOrderSummary)
                .collect(Collectors.toList());
    }

    public AdminOrderDetailDto getOrderDetail(Long orderId) {
        OnlineOrder order = onlineOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return toOrderDetail(order);
    }

    public AdminOrderDetailDto updateOrderStatus(Long orderId, String status, String trackingNumber) {
        OnlineOrder order = onlineOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus(status);
        if (trackingNumber != null && !trackingNumber.isBlank()) {
            order.setTrackingNumber(trackingNumber);
        }
        onlineOrderRepository.save(order);
        return toOrderDetail(order);
    }

    public List<AdminTransactionSummaryDto> listTransactions(String source) {
        List<Transaction> txs;
        if (source != null && !source.isBlank()) {
            // No direct method, filter in memory for simplicity
            txs = transactionRepository.findAll().stream()
                    .filter(t -> source.equalsIgnoreCase(t.getSource()))
                    .collect(Collectors.toList());
        } else {
            txs = transactionRepository.findAll();
        }
        return txs.stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                .map(this::toTxSummary)
                .collect(Collectors.toList());
    }

    public AdminTransactionDetailDto getTransactionDetail(Long transactionId) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return toTxDetail(tx);
    }

    private AdminOrderSummaryDto toOrderSummary(OnlineOrder order) {
        AdminOrderSummaryDto dto = new AdminOrderSummaryDto();
        dto.setOrderId(order.getOrderId());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPlacedAt(order.getPlacedAt());
        dto.setShippingMethod(order.getShippingMethod());
        if (order.getCustomer() != null) {
            dto.setCustomerUsername(order.getCustomer().getUsername());
            dto.setCustomerEmail(order.getCustomer().getEmail());
        }
        if (order.getTransaction() != null) {
            dto.setTransactionId(order.getTransaction().getTransactionId());
            dto.setBillNumber(order.getTransaction().getBillNumber());
            dto.setTotalAmount(order.getTransaction().getTotalAmount());
            dto.setTaxAmount(order.getTransaction().getTaxAmount());
            dto.setNetAmount(order.getTransaction().getNetAmount());
        }
        return dto;
    }

    private AdminOrderDetailDto toOrderDetail(OnlineOrder order) {
        AdminOrderDetailDto dto = new AdminOrderDetailDto();
        dto.setOrderId(order.getOrderId());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPlacedAt(order.getPlacedAt());
        dto.setShippingMethod(order.getShippingMethod());
        dto.setTrackingNumber(order.getTrackingNumber());
        if (order.getCustomer() != null) {
            dto.setCustomerUsername(order.getCustomer().getUsername());
            dto.setCustomerEmail(order.getCustomer().getEmail());
            dto.setCustomerFullName(order.getCustomer().getFullName());
        }
        if (order.getShippingAddress() != null) {
            dto.setAddressLine1(order.getShippingAddress().getAddressLine1());
            dto.setAddressLine2(order.getShippingAddress().getAddressLine2());
            dto.setCity(order.getShippingAddress().getCity());
            dto.setPostalCode(order.getShippingAddress().getPostalCode());
        }
        if (order.getTransaction() != null) {
            Transaction tx = order.getTransaction();
            dto.setTransactionId(tx.getTransactionId());
            dto.setBillNumber(tx.getBillNumber());
            dto.setTotalAmount(tx.getTotalAmount());
            dto.setTaxAmount(tx.getTaxAmount());
            dto.setDiscountAmount(tx.getDiscountAmount());
            dto.setNetAmount(tx.getNetAmount());

            List<TransactionItem> items = transactionItemRepository.findByTransactionTransactionId(tx.getTransactionId());
            dto.setItems(items.stream().map(ti -> {
                AdminOrderDetailDto.ItemDto idto = new AdminOrderDetailDto.ItemDto();
                idto.setProductId(ti.getProduct().getProductId());
                idto.setProductName(ti.getProduct().getProductName());
                idto.setQuantity(ti.getQuantity());
                idto.setUnitPrice(ti.getUnitPrice());
                idto.setLineTotal(ti.getLineTotal());
                return idto;
            }).collect(Collectors.toList()));

            List<Payment> payments = paymentRepository.findByTransactionTransactionId(tx.getTransactionId());
            dto.setPayments(payments.stream().map(p -> {
                AdminOrderDetailDto.PaymentDto pdto = new AdminOrderDetailDto.PaymentDto();
                pdto.setPaymentId(p.getPaymentId());
                pdto.setPaymentDate(p.getPaymentDate());
                pdto.setMethod(p.getPaymentMethod());
                pdto.setAmountPaid(p.getAmountPaid());
                pdto.setReferenceNumber(p.getReferenceNumber());
                pdto.setStatus(p.getStatus());
                return pdto;
            }).collect(Collectors.toList()));
        }
        return dto;
    }

    private AdminTransactionSummaryDto toTxSummary(Transaction tx) {
        AdminTransactionSummaryDto dto = new AdminTransactionSummaryDto();
        dto.setTransactionId(tx.getTransactionId());
        dto.setBillNumber(tx.getBillNumber());
        dto.setTransactionDate(tx.getTransactionDate());
        if (tx.getCustomer() != null) {
            dto.setCustomerUsername(tx.getCustomer().getUsername());
            dto.setCustomerEmail(tx.getCustomer().getEmail());
        }
        dto.setTotalAmount(tx.getTotalAmount());
        dto.setNetAmount(tx.getNetAmount());
        dto.setStatus(tx.getStatus());
        dto.setSource(tx.getSource());
        return dto;
    }

    private AdminTransactionDetailDto toTxDetail(Transaction tx) {
        AdminTransactionDetailDto dto = new AdminTransactionDetailDto();
        dto.setTransactionId(tx.getTransactionId());
        dto.setBillNumber(tx.getBillNumber());
        dto.setTransactionDate(tx.getTransactionDate());
        if (tx.getCustomer() != null) {
            dto.setCustomerUsername(tx.getCustomer().getUsername());
            dto.setCustomerEmail(tx.getCustomer().getEmail());
        }
        dto.setTotalAmount(tx.getTotalAmount());
        dto.setTaxAmount(tx.getTaxAmount());
        dto.setDiscountAmount(tx.getDiscountAmount());
        dto.setNetAmount(tx.getNetAmount());
        dto.setStatus(tx.getStatus());
        dto.setSource(tx.getSource());

        List<TransactionItem> items = transactionItemRepository.findByTransactionTransactionId(tx.getTransactionId());
        dto.setItems(items.stream().map(ti -> {
            AdminTransactionDetailDto.ItemDto idto = new AdminTransactionDetailDto.ItemDto();
            idto.setProductId(ti.getProduct().getProductId());
            idto.setProductName(ti.getProduct().getProductName());
            idto.setQuantity(ti.getQuantity());
            idto.setUnitPrice(ti.getUnitPrice());
            idto.setLineTotal(ti.getLineTotal());
            return idto;
        }).collect(Collectors.toList()));

        List<Payment> payments = paymentRepository.findByTransactionTransactionId(tx.getTransactionId());
        dto.setPayments(payments.stream().map(p -> {
            AdminTransactionDetailDto.PaymentDto pdto = new AdminTransactionDetailDto.PaymentDto();
            pdto.setPaymentId(p.getPaymentId());
            pdto.setPaymentDate(p.getPaymentDate());
            pdto.setMethod(p.getPaymentMethod());
            pdto.setAmountPaid(p.getAmountPaid());
            pdto.setReferenceNumber(p.getReferenceNumber());
            pdto.setStatus(p.getStatus());
            return pdto;
        }).collect(Collectors.toList()));

        return dto;
    }
}
