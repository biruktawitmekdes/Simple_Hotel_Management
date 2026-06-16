package service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple in-memory price list for menu items and services.
 * Keys are string identifiers such as "food.tibs" or "bev.wine".
 */
public class PriceList {
    private final Map<String, Double> prices = new LinkedHashMap<>();

    public PriceList() {
        // Default prices (editable at runtime)
        // Food
        prices.put("food.tibs", 50.0);
        prices.put("food.dorowet", 55.0);
        prices.put("food.bfe", 40.0);
        prices.put("food.breakfast", 20.0);
        prices.put("food.fruits", 10.0);
        // Beverages
        prices.put("bev.wine", 15.0);
        prices.put("bev.alcoholic", 12.0);
        prices.put("bev.water", 2.0);
        prices.put("bev.juice", 4.0);
        prices.put("bev.non_alcoholic", 5.0);
        // Services
        prices.put("service.gym", 8.0); // per session
        prices.put("service.spa", 25.0); // per session
    }

    public double getPrice(String key) {
        return prices.getOrDefault(key, 0.0);
    }

    public void setPrice(String key, double price) {
        if (price < 0) price = 0.0;
        prices.put(key, price);
    }

    public Map<String, Double> getAllPrices() {
        return Collections.unmodifiableMap(prices);
    }
}
