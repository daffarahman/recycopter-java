package com.dapasril.finalprojectpbo.recycling;

import java.util.ArrayList;
import java.util.List;

public class CraftingManager {
    private static CraftingManager instance;
    private final List<Recipe> recipes;

    private CraftingManager() {
        recipes = new ArrayList<>();
        initRecipes();
    }

    public static CraftingManager getInstance() {
        if (instance == null) {
            instance = new CraftingManager();
        }
        return instance;
    }

    private void initRecipes() {
        // Plastic Bottle -> Recycled Plastic
        Recipe r1 = new Recipe(Product.RECYCLED_PLASTIC);
        r1.addRequirement(RecyclableItem.PLASTIC_BOTTLE, 3);
        recipes.add(r1);

        // Aluminum Can -> Aluminum Ingot
        Recipe r2 = new Recipe(Product.ALUMINUM_INGOT);
        r2.addRequirement(RecyclableItem.ALUMINUM_CAN, 3);
        recipes.add(r2);

        // Glass Shard -> Glass Block
        Recipe r3 = new Recipe(Product.GLASS_BLOCK);
        r3.addRequirement(RecyclableItem.GLASS_BOTTLE, 4);
        recipes.add(r3);

        // Paper Scrap -> Recycled Paper
        Recipe r4 = new Recipe(Product.RECYCLED_PAPER);
        r4.addRequirement(RecyclableItem.NEWSPAPER, 5);
        recipes.add(r4);

        // Cardboard -> Cardboard Box
        Recipe r5 = new Recipe(Product.CARDBOARD_BOX);
        r5.addRequirement(RecyclableItem.CARDBOARD_BOX, 3);
        recipes.add(r5);

        // Electronic Chip -> Circuit Board
        Recipe r6 = new Recipe(Product.CIRCUIT_BOARD);
        r6.addRequirement(RecyclableItem.E_WASTE, 2);
        recipes.add(r6);

        // Rubber Tire -> Rubber Mat
        Recipe r7 = new Recipe(Product.RUBBER_MAT);
        r7.addRequirement(RecyclableItem.OLD_TIRE, 1); // 1 tire is enough? maybe 2
        recipes.add(r7);

        // Scrap Metal -> Steel Beam
        Recipe r8 = new Recipe(Product.STEEL_BEAM);
        r8.addRequirement(RecyclableItem.SCRAP_METAL, 4);
        recipes.add(r8);

        // Battery -> Power Cell
        Recipe r9 = new Recipe(Product.POWER_CELL);
        r9.addRequirement(RecyclableItem.CAR_BATTERY, 2);
        recipes.add(r9);

        // Cloth Rag -> Textile Roll
        Recipe r10 = new Recipe(Product.TEXTILE_ROLL);
        r10.addRequirement(RecyclableItem.OLD_CLOTHES, 5);
        recipes.add(r10);
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }
}
