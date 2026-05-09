package com.inventory.controller;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@Slf4j
public class InventoryController {

    private final Map<String, Integer> stockMap = new HashMap<>();

    @Autowired
    private Tracer tracer;

    public InventoryController() {
        // Seeded data
        stockMap.put("prod-123", 10);
    }

    @GetMapping("/check/{productId}")
    public InventoryResponse checkStock(@PathVariable String productId, @RequestParam(defaultValue = "1") int quantity) {
        log.info("Checking inventory for product: {} with quantity: {}", productId, quantity);

        // Add custom span tag
        Span currentSpan = this.tracer.currentSpan();
        if (currentSpan != null) {
            currentSpan.tag("inventory.productId", productId);
        }

        Integer stock = stockMap.getOrDefault(productId, 0);
        boolean available = stock >= quantity;

        return new InventoryResponse(productId, available, stock);
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
