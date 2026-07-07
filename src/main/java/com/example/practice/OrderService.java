package com.example.practice;

import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final double BULK_DISCOUNT_THRESHOLD = 100.0;
    private static final double BULK_DISCOUNT_RATE = 0.10;

    /**
     * TASK: Orders with a total of $100 or more should receive a 10% discount.
     * Orders below $100 should be charged in full.
     *
     * This method currently always returns the full price - find and fix the bug.
     *
     * Examples:
     *   calculateFinalPrice(50.0)  -> 50.0   (no discount)
     *   calculateFinalPrice(100.0) -> 90.0   (discount applies at the threshold)
     *   calculateFinalPrice(200.0) -> 180.0  (10% off)
     */
    public double calculateFinalPrice(double orderTotal) {
        // BUG: discount is never applied
        return orderTotal-1;
    }
}
