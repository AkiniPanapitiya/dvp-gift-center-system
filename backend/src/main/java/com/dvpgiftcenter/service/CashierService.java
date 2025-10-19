package com.dvpgiftcenter.service;
import com.dvpgiftcenter.dto.cashier.CashierProductDto;
import com.dvpgiftcenter.dto.cashier.PosTransactionRequest;
import com.dvpgiftcenter.dto.cashier.PosTransactionResponse;
import com.dvpgiftcenter.dto.cashier.PosTransactionSummaryDto;
import com.dvpgiftcenter.entity.Inventory;
import com.dvpgiftcenter.entity.Payment;
import com.dvpgiftcenter.entity.Product;
import com.dvpgiftcenter.entity.StockMovement;
import com.dvpgiftcenter.entity.Transaction;
import com.dvpgiftcenter.entity.TransactionItem;
import com.dvpgiftcenter.entity.User;
import com.dvpgiftcenter.repository.InventoryRepository;
import com.dvpgiftcenter.repository.PaymentRepository;
import com.dvpgiftcenter.repository.ProductRepository;
import com.dvpgiftcenter.repository.StockMovementRepository;
import com.dvpgiftcenter.repository.TransactionItemRepository;
import com.dvpgiftcenter.repository.TransactionRepository;
import com.dvpgiftcenter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CashierService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private TransactionItemRepository transactionItemRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private StockMovementRepository stockMovementRepository;

	@Autowired
	private UserRepository userRepository;

	@Value("${app.tax.rate:0.05}")
	private BigDecimal taxRate;

	// List all active products from Product table for POS
	public List<CashierProductDto> getInStoreProducts() {
		return productRepository.findByIsActiveTrueOrderByProductName()
				.stream()
				.map(this::mapToProductDto)
				.collect(Collectors.toList());
	}

	// Search by name/code/barcode
	public List<CashierProductDto> searchProducts(String query) {
		return productRepository
				.findByProductNameContainingIgnoreCaseOrProductCodeContainingIgnoreCaseOrBarcodeContainingIgnoreCase(query, query, query)
				.stream()
				.filter(p -> Boolean.TRUE.equals(p.getIsActive()))
				.map(this::mapToProductDto)
				.collect(Collectors.toList());
	}

	// Barcode quick lookup
	public CashierProductDto getProductByBarcode(String barcode) {
		return productRepository.findByBarcodeAndIsActiveTrue(barcode)
				.map(this::mapToProductDto)
				.orElse(null);
	}

	// List recent POS transactions with optional filters
	public List<PosTransactionSummaryDto> listPosTransactions(String bill, LocalDateTime start, LocalDateTime end) {
		List<Transaction> txs;
		if (bill != null && !bill.isBlank()) {
			txs = transactionRepository.findBySourceAndBillNumberContainingIgnoreCaseOrderByTransactionDateDesc("pos_sale", bill);
		} else if (start != null && end != null) {
			txs = transactionRepository.findBySourceAndTransactionDateBetweenOrderByTransactionDateDesc("pos_sale", start, end);
		} else {
			txs = transactionRepository.findTop100BySourceOrderByTransactionDateDesc("pos_sale");
		}
		return txs.stream().map(this::toSummaryDto).collect(Collectors.toList());
	}

	private PosTransactionSummaryDto toSummaryDto(Transaction tx) {
		PosTransactionSummaryDto dto = new PosTransactionSummaryDto();
		dto.setTransactionId(tx.getTransactionId());
		dto.setBillNumber(tx.getBillNumber());
		dto.setCustomerName(tx.getCustomer() != null ? tx.getCustomer().getFullName() : "Walk-in Customer");
		dto.setCashierName(tx.getUser() != null ? tx.getUser().getFullName() : null);
		dto.setTransactionDate(tx.getTransactionDate());
		dto.setNetAmount(tx.getNetAmount());
		dto.setStatus(tx.getStatus());
		return dto;
	}

	// Process POS transaction
	@Transactional
	public PosTransactionResponse processTransaction(PosTransactionRequest request) {
	// Customer (optional). If null, treat as Walk-in Customer.
	User customer = null;
	if (request.getCustomerId() != null) {
	    customer = userRepository.findById(request.getCustomerId())
		    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
	}

		// Cashier from security
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User cashier = userRepository.findByUsername(auth.getName())
				.orElseThrow(() -> new IllegalArgumentException("Cashier not found"));

		Transaction tx = new Transaction();
	tx.setCustomer(customer); // may be null -> allowed by schema and entity
		tx.setUser(cashier);
		tx.setBillNumber(generateBillNumber());
		tx.setTransactionDate(LocalDateTime.now());
		tx.setTransactionType("sale");
		tx.setStatus("completed");
		tx.setSource("pos_sale");

		// First pass: validate stock and compute totals
		BigDecimal total = BigDecimal.ZERO;
		BigDecimal tax = request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO;
		BigDecimal discount = request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO;

		for (PosTransactionRequest.PosTransactionItem it : request.getItems()) {
			Product product = productRepository.findById(it.getProductId())
					.orElseThrow(() -> new IllegalArgumentException("Product not found: " + it.getProductId()));
			Inventory inv = inventoryRepository.findByProductProductId(product.getProductId())
					.orElseThrow(() -> new IllegalArgumentException("Product not in inventory: " + product.getProductName()));
			if (inv.getCurrentStock() < it.getQuantity()) {
				throw new IllegalArgumentException("Insufficient stock for product: " + product.getProductName());
			}
			BigDecimal line = it.getUnitPrice()
					.multiply(BigDecimal.valueOf(it.getQuantity()))
					.subtract(it.getDiscountAmount() == null ? BigDecimal.ZERO : it.getDiscountAmount());
			total = total.add(line);
		}

		if (tax.compareTo(BigDecimal.ZERO) == 0) {
			tax = total.multiply(taxRate);
		}

		tx.setTotalAmount(total);
		tx.setTaxAmount(tax);
		tx.setDiscountAmount(discount);
		tx.setNetAmount(total.add(tax).subtract(discount));

		// Save transaction header
		tx = transactionRepository.save(tx);

		// Persist items + update inventory + stock movement
		for (PosTransactionRequest.PosTransactionItem it : request.getItems()) {
			Product product = productRepository.findById(it.getProductId())
					.orElseThrow(() -> new IllegalArgumentException("Product not found: " + it.getProductId()));

			TransactionItem ti = new TransactionItem();
			ti.setTransaction(tx);
			ti.setProduct(product);
			ti.setQuantity(it.getQuantity());
			ti.setUnitPrice(it.getUnitPrice());
			ti.setDiscountAmount(it.getDiscountAmount() == null ? BigDecimal.ZERO : it.getDiscountAmount());
			ti.setTaxAmount(BigDecimal.ZERO);
			ti.setLineTotal(it.getUnitPrice()
					.multiply(BigDecimal.valueOf(it.getQuantity()))
					.subtract(it.getDiscountAmount() == null ? BigDecimal.ZERO : it.getDiscountAmount()));
			ti.setReturnQuantity(0);
			transactionItemRepository.save(ti);

			Inventory inv = inventoryRepository.findByProductProductId(product.getProductId())
					.orElseThrow(() -> new IllegalArgumentException("Product not in inventory: " + product.getProductName()));
			int prev = inv.getCurrentStock();
			inv.setCurrentStock(prev - it.getQuantity());
			inv.setLastUpdated(LocalDateTime.now());
			inventoryRepository.save(inv);

			StockMovement sm = new StockMovement();
			sm.setProduct(product);
			sm.setMovementType("sale");
			sm.setQuantityChange(-it.getQuantity());
			sm.setPreviousStock(prev);
			sm.setNewStock(inv.getCurrentStock());
			sm.setMovementDate(LocalDateTime.now());
			sm.setNotes("POS sale - " + product.getProductName());
			sm.setTransaction(tx);
			stockMovementRepository.save(sm);
		}

		Payment payment = new Payment();
		payment.setTransaction(tx);
		payment.setAmountPaid(tx.getNetAmount());
		payment.setPaymentMethod(request.getPaymentMethod());
		payment.setPaymentDate(LocalDateTime.now());
		payment.setStatus("success");
		payment.setReferenceNumber(generatePaymentReference());
		paymentRepository.save(payment);

		return buildResponse(tx);
	}

	public PosTransactionResponse getTransactionReceipt(Long transactionId) {
		Optional<Transaction> opt = transactionRepository.findById(transactionId);
		if (opt.isEmpty() || !"pos_sale".equals(opt.get().getSource())) {
			return null;
		}
		return buildResponse(opt.get());
	}

	private CashierProductDto mapToProductDto(Product p) {
		String categoryName = p.getCategory() != null ? p.getCategory().getCategoryName() : "Uncategorized";
		Integer stock = inventoryRepository.findByProductProductId(p.getProductId())
				.map(Inventory::getCurrentStock)
				.orElse(0);
		return new CashierProductDto(
				p.getProductId(),
				p.getProductName(),
				p.getProductCode(),
				p.getBarcode(),
				p.getDescription(),
				p.getUnitPrice(),
				categoryName,
				p.getImageUrl(),
				stock,
				p.getIsActive()
		);
	}

	private String generateBillNumber() {
		String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		Long count = transactionRepository.countByBillNumberStartingWith("DVP" + dateStr) + 1;
		return String.format("DVP%s%04d", dateStr, count);
	}

	private String generatePaymentReference() {
		return "REF-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
	}

	private PosTransactionResponse buildResponse(Transaction tx) {
		PosTransactionResponse r = new PosTransactionResponse();
		r.setTransactionId(tx.getTransactionId());
		r.setBillNumber(tx.getBillNumber());
		r.setTransactionDate(tx.getTransactionDate());
		r.setCustomerName(tx.getCustomer() != null ? tx.getCustomer().getFullName() : "Walk-in Customer");
		r.setTotalAmount(tx.getTotalAmount());
		r.setTaxAmount(tx.getTaxAmount());
		r.setDiscountAmount(tx.getDiscountAmount());
		r.setNetAmount(tx.getNetAmount());
		r.setStatus(tx.getStatus());
		r.setCashierName(tx.getUser() != null ? tx.getUser().getFullName() : null);

		Payment payment = paymentRepository.findByTransaction(tx);
		if (payment != null) {
			r.setPaymentMethod(payment.getPaymentMethod());
		}

		List<TransactionItem> items = transactionItemRepository.findByTransactionOrderByItemId(tx);
		r.setItems(items.stream().map(this::mapToItemDto).collect(Collectors.toList()));
		return r;
	}

	private PosTransactionResponse.TransactionItemDto mapToItemDto(TransactionItem item) {
		return new PosTransactionResponse.TransactionItemDto(
				item.getProduct().getProductId(),
				item.getProduct().getProductName(),
				item.getProduct().getProductCode(),
				item.getQuantity(),
				item.getUnitPrice(),
				item.getDiscountAmount(),
				item.getLineTotal()
		);
	}
}
