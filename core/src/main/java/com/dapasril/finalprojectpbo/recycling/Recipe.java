package com.dapasril.finalprojectpbo.recycling;

import java.util.HashMap;
import java.util.Map;

public class Recipe {
    private final Product result;
    private final Map<RecyclableItem, Integer> requirements;

    public Recipe(Product result) {
        this.result = result;
        this.requirements = new HashMap<>();
    }

    public void addRequirement(RecyclableItem item, int amount) {
        requirements.put(item, amount);
    }

    public Product getResult() {
        return result;
    }

    public Map<RecyclableItem, Integer> getRequirements() {
        return requirements;
    }
}
