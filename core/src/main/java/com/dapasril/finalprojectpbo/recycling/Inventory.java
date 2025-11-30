package com.dapasril.finalprojectpbo.recycling;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private static Inventory instance;
    private final Map<RecyclableItem, Integer> items;
    private final Map<Product, Integer> products;

    private Inventory() {
        items = new HashMap<>();
        products = new HashMap<>();
    }

    public static Inventory getInstance() {
        if (instance == null) {
            instance = new Inventory();
        }
        return instance;
    }

    public void addItem(RecyclableItem item, int amount) {
        items.put(item, items.getOrDefault(item, 0) + amount);
    }

    public boolean hasItem(RecyclableItem item, int amount) {
        return items.getOrDefault(item, 0) >= amount;
    }

    public void removeItem(RecyclableItem item, int amount) {
        if (hasItem(item, amount)) {
            items.put(item, items.get(item) - amount);
        }
    }

    public void addProduct(Product product, int amount) {
        products.put(product, products.getOrDefault(product, 0) + amount);
    }

    public boolean hasProduct(Product product, int amount) {
        return products.getOrDefault(product, 0) >= amount;
    }

    public void removeProduct(Product product, int amount) {
        if (hasProduct(product, amount)) {
            products.put(product, products.get(product) - amount);
        }
    }

    public Map<RecyclableItem, Integer> getItems() {
        return items;
    }

    public Map<Product, Integer> getProducts() {
        return products;
    }

    public boolean canCraft(Recipe recipe) {
        for (Map.Entry<RecyclableItem, Integer> entry : recipe.getRequirements().entrySet()) {
            if (!hasItem(entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    private int recycledItemsCount = 0;

    public void craft(Recipe recipe) {
        if (canCraft(recipe)) {
            for (Map.Entry<RecyclableItem, Integer> entry : recipe.getRequirements().entrySet()) {
                removeItem(entry.getKey(), entry.getValue());
                recycledItemsCount += entry.getValue();
            }
            addProduct(recipe.getResult(), 1);
        }
    }

    public int getRecycledItemsCount() {
        return recycledItemsCount;
    }

    public void reset() {
        items.clear();
        products.clear();
        recycledItemsCount = 0;
    }
}
