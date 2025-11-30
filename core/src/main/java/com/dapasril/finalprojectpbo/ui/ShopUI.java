package com.dapasril.finalprojectpbo.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import com.dapasril.finalprojectpbo.Global;
import com.dapasril.finalprojectpbo.managers.EconomyManager;
import com.dapasril.finalprojectpbo.scene.DemoWorldScene;

public class ShopUI {
    private DemoWorldScene scene;
    private Stage stage;
    private Table shopTable;

    private TextButtonStyle buttonStyle;
    private Label.LabelStyle labelStyle;

    private Texture bgTexture, headerTexture, selectionTexture;
    private TextureRegionDrawable bgDrawable, headerDrawable, selectionDrawable;

    public boolean isVisible = false;

    public ShopUI(DemoWorldScene scene, Stage stage) {
        this.scene = scene;
        this.stage = stage;
        init();
    }

    private void init() {
        createStyles();
        createTable();
    }

    private void createStyles() {
        buttonStyle = new TextButtonStyle();
        buttonStyle.font = Global.fontGeneratorChalet.generateFont(Global.fontParameters.get(0));
        buttonStyle.fontColor = Color.WHITE;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 1f, 1f, 0.2f); // Selection color
        pixmap.fill();
        selectionTexture = new Texture(pixmap);
        selectionDrawable = new TextureRegionDrawable(selectionTexture);

        buttonStyle.over = selectionDrawable; // Highlight on hover
        // Align text to left

        labelStyle = new Label.LabelStyle();
        labelStyle.font = Global.fontGeneratorChalet.generateFont(Global.fontParameters.get(1)); // Larger for header
        labelStyle.fontColor = Color.WHITE;

        // Header Background (Blue)
        pixmap.setColor(0.003f, 0.10f, 0.27f, 1f);
        pixmap.fill();
        headerTexture = new Texture(pixmap);
        headerDrawable = new TextureRegionDrawable(headerTexture);

        // Body Background (Black/Dark)
        pixmap.setColor(0, 0, 0, 0.8f);
        pixmap.fill();
        bgTexture = new Texture(pixmap);
        bgDrawable = new TextureRegionDrawable(bgTexture);

        pixmap.dispose();
    }

    private void createTable() {
        shopTable = new Table();
        shopTable.align(Align.top | Align.right);
        shopTable.setFillParent(true);
        shopTable.setVisible(false);

        // Container for the menu itself
        Table menuContainer = new Table();
        menuContainer.setBackground(bgDrawable);

        // Header
        Table headerTable = new Table();
        headerTable.setBackground(headerDrawable);
        Label titleLabel = new Label("SHOP", labelStyle);
        headerTable.add(titleLabel).pad(10).expandX().left();

        menuContainer.add(headerTable).growX().row();

        // Items
        addShopItem(menuContainer, "Refill Fuel", "$" + EconomyManager.FUEL_REFILL_PRICE, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String charMode = scene.getHudManager().getSelectedCharacter();
                if (charMode.equals("Heli")) {
                    if (scene.isPlayerInShopZone()) {
                        if (EconomyManager.getInstance().spendMoney(EconomyManager.FUEL_REFILL_PRICE)) {
                            if (scene.playerHeli != null) {
                                scene.playerHeli.refillFuel();
                            }
                        } else {
                            scene.getHudManager().showNotification("Not enough money!");
                        }
                    } else {
                        scene.getHudManager().showNotification("Must be on Helipad to refuel!");
                    }
                } else if (charMode.equals("Boat")) {
                    if (EconomyManager.getInstance().spendMoney(EconomyManager.FUEL_REFILL_PRICE)) {
                        if (scene.playerBoat != null) {
                            scene.playerBoat.refillFuel();
                        }
                    } else {
                        scene.getHudManager().showNotification("Not enough money!");
                    }
                } else {
                    scene.getHudManager().showNotification("Cannot refuel in this mode");
                }
            }
        });

        addShopItem(menuContainer, "Close", "", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        // Add menu container to main table with padding for position
        shopTable.add(menuContainer).width(300).padTop(120).padRight(20);

        stage.addActor(shopTable);
    }

    private void addShopItem(Table container, String name, String price, ClickListener listener) {
        TextButton btn = new TextButton(name, buttonStyle);
        btn.getLabel().setAlignment(Align.left);
        btn.addListener(listener);

        Table rowTable = new Table();
        rowTable.add(btn).growX().left().pad(5).padLeft(10);
        if (!price.isEmpty()) {
            Label priceLabel = new Label(price, new Label.LabelStyle(
                    Global.fontGeneratorChalet.generateFont(Global.fontParameters.get(0)), Color.WHITE));
            rowTable.add(priceLabel).right().pad(5).padRight(10);
        }

        com.badlogic.gdx.scenes.scene2d.ui.Button rowBtn = new com.badlogic.gdx.scenes.scene2d.ui.Button(buttonStyle);
        rowBtn.clearChildren();
        Label nameLabel = new Label(name, new Label.LabelStyle(
                Global.fontGeneratorChalet.generateFont(Global.fontParameters.get(0)), Color.WHITE));
        rowBtn.add(nameLabel).expandX().left().padLeft(10).padTop(5).padBottom(5);

        if (!price.isEmpty()) {
            Label priceLabel = new Label(price, new Label.LabelStyle(
                    Global.fontGeneratorChalet.generateFont(Global.fontParameters.get(0)), Color.WHITE));
            rowBtn.add(priceLabel).right().padRight(10).padTop(5).padBottom(5);
        }

        rowBtn.addListener(listener);
        container.add(rowBtn).growX().height(40).row();
    }

    public void show() {
        shopTable.setVisible(true);
        isVisible = true;
    }

    public void hide() {
        shopTable.setVisible(false);
        isVisible = false;
    }

    public void dispose() {
        if (bgTexture != null)
            bgTexture.dispose();
        if (headerTexture != null)
            headerTexture.dispose();
        if (selectionTexture != null)
            selectionTexture.dispose();
    }
}
