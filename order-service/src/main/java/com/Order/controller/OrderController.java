package com.Order.controller;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Tracer tracer;

    @Value("${INVENTORY_SERVICE_URL:http://localhost:8082}")
    private String inventoryServiceUrl;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        log.info("Received order request: {}", request);

        // Add custom span tag
        Span currentSpan = this.tracer.currentSpan();
        if (currentSpan != null) {
            currentSpan.tag("order.id", request.getOrderId());
        }

        try {
            String url = String.format("%s/api/inventory/check/%s?quantity=%d", 
                                        inventoryServiceUrl, request.getProductId(), request.getQuantity());
            
            InventoryResponse inventory = restTemplate.getForObject(url, InventoryResponse.class);

            OrderResponse response = new OrderResponse();
            response.setOrderId(request.getOrderId());
            
            if (inventory != null && inventory.isAvailable()) {
                response.setStatus("CONFIRMED");
                response.setInventoryAvailable(true);
            } else {
                response.setStatus("REJECTED");
                response.setInventoryAvailable(false);
            }

            return ResponseEntity.ok(response);

        } catch (ResourceAccessException e) {
            log.error("Inventory service is unavailable: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (Exception e) {
            log.error("Error processing order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Data
    public static class OrderRequest {
        private String orderId;
        private String productId;
        private int quantity;
    }

    @Data
    public static class OrderResponse {
        private String orderId;
        private String status;
        private boolean inventoryAvailable;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InventoryResponse {
        private String productId;
        private boolean available;
        private Integer stock;
    }
}
