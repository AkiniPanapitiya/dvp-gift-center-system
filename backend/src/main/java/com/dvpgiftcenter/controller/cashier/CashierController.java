package com.dvpgiftcenter.controller.cashier;

import com.dvpgiftcenter.dto.cashier.CashierProductDto;
import com.dvpgiftcenter.dto.cashier.PosTransactionRequest;
import com.dvpgiftcenter.dto.cashier.PosTransactionResponse;
import com.dvpgiftcenter.dto.cashier.PosTransactionSummaryDto;
import com.dvpgiftcenter.dto.common.ApiResponse;
import com.dvpgiftcenter.service.CashierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/cashier")
@PreAuthorize("hasRole('CASHIER') or hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class CashierController {
    
    @Autowired
    private CashierService cashierService;
    
    /**
     * Get all products available for in-store sale (not online products)
     */
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<CashierProductDto>>> getInStoreProducts() {
        try {
            List<CashierProductDto> products = cashierService.getInStoreProducts();
            
            return ResponseEntity.ok(
                ApiResponse.success("In-store products retrieved successfully", products)
            );
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to retrieve products: " + e.getMessage()));
        }
    }

    /**
     * List recent POS transactions (past bills) with optional filters
     */
    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<PosTransactionSummaryDto>>> listTransactions(
            @RequestParam(required = false) String bill,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        try {
            LocalDateTime startDt = null;
            LocalDateTime endDt = null;
            if (start != null && end != null) {
                startDt = LocalDateTime.parse(start);
                endDt = LocalDateTime.parse(end);
            }
            List<PosTransactionSummaryDto> list = cashierService.listPosTransactions(bill, startDt, endDt);
            return ResponseEntity.ok(ApiResponse.success("Transactions retrieved", list));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to list transactions: " + e.getMessage()));
        }
    }
    
    /**
     * Search products by name, code, or barcode for POS system
     */
    @GetMapping("/products/search")
    public ResponseEntity<ApiResponse<List<CashierProductDto>>> searchProducts(@RequestParam String query) {
        try {
            List<CashierProductDto> products = cashierService.searchProducts(query);
            
            return ResponseEntity.ok(
                ApiResponse.success("Products found", products)
            );
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Search failed: " + e.getMessage()));
        }
    }
    
    /**
     * Get product by barcode for quick POS scanning
     */
    @GetMapping("/products/barcode/{barcode}")
    public ResponseEntity<ApiResponse<CashierProductDto>> getProductByBarcode(@PathVariable String barcode) {
        try {
            CashierProductDto product = cashierService.getProductByBarcode(barcode);
            
            if (product == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(
                ApiResponse.success("Product found", product)
            );
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to find product: " + e.getMessage()));
        }
    }
    
    /**
     * Process POS transaction (in-store sale)
     */
    @PostMapping("/transactions")
    public ResponseEntity<ApiResponse<PosTransactionResponse>> processTransaction(
            @Valid @RequestBody PosTransactionRequest request) {
        try {
            PosTransactionResponse response = cashierService.processTransaction(request);
            
            return ResponseEntity.ok(
                ApiResponse.success("Transaction processed successfully", response)
            );
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Invalid transaction data: " + e.getMessage()));
                
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Transaction failed: " + e.getMessage()));
        }
    }
    
    /**
     * Get transaction receipt data
     */
    @GetMapping("/transactions/{transactionId}/receipt")
    public ResponseEntity<ApiResponse<PosTransactionResponse>> getTransactionReceipt(
            @PathVariable Long transactionId) {
        try {
            PosTransactionResponse receipt = cashierService.getTransactionReceipt(transactionId);
            
            if (receipt == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(
                ApiResponse.success("Receipt retrieved successfully", receipt)
            );
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to get receipt: " + e.getMessage()));
        }
    }
}