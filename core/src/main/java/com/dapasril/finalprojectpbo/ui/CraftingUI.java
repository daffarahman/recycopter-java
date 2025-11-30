package com.dapasril.finalprojectpbo.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.dapasril.finalprojectpbo.Global;
import com.dapasril.finalprojectpbo.managers.EconomyManager;
import com.dapasril.finalprojectpbo.recycling.CraftingManager;
import com.dapasril.finalprojectpbo.recycling.Inventory;
import com.dapasril.finalprojectpbo.recycling.Product;
import com.dapasril.finalprojectpbo.recycling.Recipe;
import com.dapasril.finalprojectpbo.recycling.RecyclableItem;
import com.dapasril.finalprojectpbo.scene.DemoWorldScene;

import java.util.Map;

public class CraftingUI {
    private DemoWorldScene scene;
    private Stage stage;
    private Table mainTable;
    public boolean isVisible = false;

    private Table recipeGrid;
    private int selectedRecipeIndex = -1;
    private Label recipeDetailsLabel;
    private Table requirementsTable;
    private Table inventoryGrid;
    private TextButton craftButton;
    private TextButton sellButton;
    private TextButton closeButton;

    private TextureRegionDrawable backgroundDrawable;
    private TextureRegionDrawable selectionDrawable;
    private Texture bgTexture;
    private Texture selectionTexture;
    private Pixmap pixmap;

    public CraftingUI(DemoWorldScene scene, Stage stage) {
        this.scene = scene;
        this.stage = stage;
        init();
    }

    private void init() {
        pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.1f, 0.1f, 0.1f, 0.9f);
        pixmap.fill();
        bgTexture = new Texture(pixmap);
        backgroundDrawable = new TextureRegionDrawable(bgTexture);

        pixmap.setColor(1f, 1f, 0f, 0.3f);
        pixmap.fill();
        selectionTexture = new Texture(pixmap);
        selectionDrawable = new TextureRegionDrawable(selectionTexture);

        mainTable = new Table();
        mainTable.setBackground(backgroundDrawable);
        mainTable.setFillParent(true);
        mainTable.setVisible(false);
        stage.addActor(mainTable);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = Global.fontGeneratorChalet.generateFont(Global.fontParameters.get(1));
        labelStyle.fontColor = Color.WHITE;

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = Global.fontGeneratorChalet.generateFont(Global.fontParameters.get(1));
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.YELLOW;

        List.ListStyle listStyle = new List.ListStyle();
        listStyle.font = Global.fontGeneratorChalet.generateFont(Global.fontParameters.get(2));
        listStyle.fontColorSelected = Color.YELLOW;
        listStyle.fontColorUnselected = Color.WHITE;
        listStyle.selection = backgroundDrawable; // Reuse for simplicity

        // Inventory Section
        inventoryGrid = new Table();
        inventoryGrid.align(Align.topLeft);
        ScrollPane inventoryScroll = new ScrollPane(inventoryGrid);

        // Recipe Section
        recipeGrid = new Table();
        recipeGrid.align(Align.topLeft);
        ScrollPane recipeScroll = new ScrollPane(recipeGrid);
        updateRecipeGrid();

        recipeDetailsLabel = new Label("Select a recipe", labelStyle);
        recipeDetailsLabel.setWrap(true);
        recipeDetailsLabel.setAlignment(Align.center);

        requirementsTable = new Table();
        requirementsTable.align(Align.center);

        // Buttons
        craftButton = new TextButton("Craft", buttonStyle);
        sellButton = new TextButton("Sell Product", buttonStyle);
        closeButton = new TextButton("Close", buttonStyle);

        // Layout
        Table leftTable = new Table();
        leftTable.add(new Label("INVENTORY", labelStyle)).padBottom(10).row();
        leftTable.add(inventoryScroll).width(300).height(400).top();

        Table centerTable = new Table();
        centerTable.add(new Label("RECIPES", labelStyle)).padBottom(10).row();
        centerTable.add(recipeScroll).width(300).height(400).top();

        Table rightTable = new Table();
        rightTable.add(new Label("REQUIRES", labelStyle)).padBottom(10).row();
        rightTable.add(requirementsTable).width(200).padBottom(20).row();
        rightTable.add(recipeDetailsLabel).width(200).padBottom(20).row();

        Table bottomTable = new Table();
        bottomTable.add(craftButton).padRight(20).width(150);
        bottomTable.add(sellButton).padRight(20).width(150);
        bottomTable.add(closeButton).width(150);

        mainTable.add(leftTable).padRight(20).top();
        mainTable.add(centerTable).padRight(20).top();
        mainTable.add(rightTable).top().padTop(50).row();
        mainTable.add(bottomTable).colspan(3).padTop(50).bottom();

        // Listeners

        craftButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                craftSelectedRecipe();
            }
        });

        sellButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sellSelectedProduct();
            }
        });

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
    }

    private void updateInventoryDisplay() {
        inventoryGrid.clear();
        inventoryGrid.top().left();

        int columns = 4;
        int currentColumn = 0;

        // Items
        for (Map.Entry<RecyclableItem, Integer> entry : Inventory.getInstance().getItems().entrySet()) {
            if (entry.getValue() > 0) {
                addInventorySlot(entry.getKey().getTexturePath(), entry.getValue(), entry.getKey().getDisplayName());
                currentColumn++;
                if (currentColumn >= columns) {
                    inventoryGrid.row();
                    currentColumn = 0;
                }
            }
        }

        // Products
        for (Map.Entry<Product, Integer> entry : Inventory.getInstance().getProducts().entrySet()) {
            if (entry.getValue() > 0) {
                addInventorySlot(entry.getKey().getTexturePath(), entry.getValue(), entry.getKey().getDisplayName());
                currentColumn++;
                if (currentColumn >= columns) {
                    inventoryGrid.row();
                    currentColumn = 0;
                }
            }
        }
    }

    private void addInventorySlot(String texturePath, int count, String tooltipText) {
        Stack slotStack = new Stack();

        // Background (simple dark square with border)
        Image bg = new Image(backgroundDrawable);
        bg.setColor(0.3f, 0.3f, 0.3f, 1f);
        slotStack.add(bg);

        // Icon
        Texture iconTexture = Global.assets.get(texturePath, Texture.class);
        if (iconTexture != null) {
            Image icon = new Image(iconTexture);
            slotStack.add(icon);
        }

        // Count Label
        Label.LabelStyle countStyle = new Label.LabelStyle();
        countStyle.font = Global.fontGeneratorChalet.generateFont(Global.fontParameters.get(0)); // Small font
        countStyle.fontColor = Color.WHITE;
        Label countLabel = new Label(String.valueOf(count), countStyle);
        countLabel.setAlignment(Align.bottomRight);
        slotStack.add(countLabel);

        inventoryGrid.add(slotStack).size(64, 64).pad(5);
    }

    private void updateRecipeDetails() {
        requirementsTable.clear();
        if (selectedRecipeIndex < 0) {
            recipeDetailsLabel.setText("Select a recipe");
            return;
        }

        Recipe recipe = CraftingManager.getInstance().getRecipes().get(selectedRecipeIndex);

        // Populate requirements table
        for (Map.Entry<RecyclableItem, Integer> entry : recipe.getRequirements().entrySet()) {
            addRequirementSlot(entry.getKey().getTexturePath(), entry.getValue());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Value: $").append(recipe.getResult().getSellValue());
        recipeDetailsLabel.setText(sb.toString());
    }

    private void addRequirementSlot(String texturePath, int count) {
        Stack slotStack = new Stack();

        // Background
        Image bg = new Image(backgroundDrawable);
        bg.setColor(0.3f, 0.3f, 0.3f, 1f);
        slotStack.add(bg);

        // Icon
        Texture iconTexture = Global.assets.get(texturePath, Texture.class);
        if (iconTexture != null) {
            Image icon = new Image(iconTexture);
            slotStack.add(icon);
        }

        // Count Label
        Label.LabelStyle countStyle = new Label.LabelStyle();
        countStyle.font = Global.fontGeneratorChalet.generateFont(Global.fontParameters.get(0)); // Small font
        countStyle.fontColor = Color.WHITE;
        Label countLabel = new Label(String.valueOf(count), countStyle);
        countLabel.setAlignment(Align.bottomRight);
        slotStack.add(countLabel);

        requirementsTable.add(slotStack).size(64, 64).pad(5);
    }

    private void craftSelectedRecipe() {
        if (selectedRecipeIndex < 0)
            return;

        Recipe recipe = CraftingManager.getInstance().getRecipes().get(selectedRecipeIndex);
        if (Inventory.getInstance().canCraft(recipe)) {
            Inventory.getInstance().craft(recipe);
            updateInventoryDisplay();
        } else {
            // Play error sound or show message
        }
    }

    private void sellSelectedProduct() {
        if (selectedRecipeIndex < 0)
            return;

        Recipe recipe = CraftingManager.getInstance().getRecipes().get(selectedRecipeIndex);
        Product product = recipe.getResult();

        if (Inventory.getInstance().hasProduct(product, 1)) {
            Inventory.getInstance().removeProduct(product, 1);
            EconomyManager.getInstance().addMoney(product.getSellValue());
            updateInventoryDisplay();
        }
    }

    private void updateRecipeGrid() {
        recipeGrid.clear();
        recipeGrid.top().left();

        int columns = 4;
        int currentColumn = 0;

        java.util.List<Recipe> recipes = CraftingManager.getInstance().getRecipes();
        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            addRecipeSlot(recipe.getResult().getTexturePath(), i);
            currentColumn++;
            if (currentColumn >= columns) {
                recipeGrid.row();
                currentColumn = 0;
            }
        }
    }

    private void addRecipeSlot(String texturePath, final int index) {
        Stack slotStack = new Stack();

        // Background
        Image bg = new Image(backgroundDrawable);
        bg.setColor(0.3f, 0.3f, 0.3f, 1f);
        slotStack.add(bg);

        // Icon
        Texture iconTexture = Global.assets.get(texturePath, Texture.class);
        if (iconTexture != null) {
            Image icon = new Image(iconTexture);
            slotStack.add(icon);
        }

        // Selection Overlay
        if (index == selectedRecipeIndex) {
            Image overlay = new Image(selectionDrawable);
            slotStack.add(overlay);
        }

        // Click Listener
        slotStack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedRecipeIndex = index;
                updateRecipeGrid(); // Re-render to show selection highlight
                updateRecipeDetails();
            }
        });

        recipeGrid.add(slotStack).size(64, 64).pad(5);
    }

    public void show() {
        updateInventoryDisplay();
        updateRecipeDetails();
        mainTable.setVisible(true);
        isVisible = true;
        scene.setPaused(true);
    }

    public void hide() {
        mainTable.setVisible(false);
        isVisible = false;
        scene.setPaused(false);
    }

    public void dispose() {
        if (bgTexture != null)
            bgTexture.dispose();
        if (selectionTexture != null)
            selectionTexture.dispose();
        if (pixmap != null)
            pixmap.dispose();
    }
}
