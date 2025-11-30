package com.dapasril.finalprojectpbo.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.dapasril.finalprojectpbo.Global;
import com.dapasril.finalprojectpbo.managers.EconomyManager;
import com.dapasril.finalprojectpbo.scene.DemoWorldScene;
import com.dapasril.finalprojectpbo.scene.MainMenuScene;

public class HUDManager implements EventListener {

    private DemoWorldScene scene;
    private Stage stage;

    public Table hudTable, pauseTable, altTable, gameOverTable, fuelTable, levelTable;
    public TextButton buttonQuit, buttonBackToGame, buttonRestart;
    public List<String> characterList;
    public Label moneyLabel, levelLabel, trashTitleLabel, trashValueLabel;
    public ProgressBar fuelBar;

    private TextButtonStyle buttonStyle;
    private ListStyle listStyle;
    private Label.LabelStyle labelStyle;
    private ProgressBarStyle fuelBarStyle;
    private Pixmap pixmap;
    private Texture texDark, texRed, texBlue, texGreen, texOrange, texFuelBg, texFuelFill, texFuelFillBlue, texWhite;
    private Drawable darkMenuBackground, redMenuBackground, blueMenuBackground, greenMenuBackground,
            orangeMenuBackground, whiteBorder, hudBackground;

    private Texture texWheelHeli, texWheelBoat, texWheelBase;
    private com.badlogic.gdx.scenes.scene2d.ui.Image characterWheel;
    private String selectedCharacter = "Heli";
    private String lastHoveredCharacter = "";

    private ShopUI shopUI;
    private CraftingUI craftingUI;

    private Music notificationSound, clickSound;

    private TextField usernameInput;

    public HUDManager(DemoWorldScene scene, Stage stage) {
        this.scene = scene;
        this.stage = stage;

        this.shopUI = new ShopUI(scene, stage);
        this.craftingUI = new CraftingUI(scene, stage);

        this.notificationSound = Global.assets.get("audio/notification1.mp3", Music.class);
        this.clickSound = Global.assets.get("audio/click1.mp3", Music.class);
        this.clickSound.setLooping(false);

        this.init();
    }

    private void init() {
        createStyles();
        createTables();
        createButtons();
        createLists();
        createLabels();
    }

    private void createStyles() {
        this.pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);

        this.pixmap.setColor(0, 0, 0, 0.8f);
        this.pixmap.fill();
        this.texDark = new Texture(this.pixmap);
        this.darkMenuBackground = new TextureRegionDrawable(this.texDark);

        this.pixmap.setColor(0, 0, 0, 0.5f);
        this.pixmap.fill();
        this.hudBackground = new TextureRegionDrawable(new Texture(this.pixmap));

        this.pixmap.setColor(0.27f, 0.02f, 0.003f, 0.6f);
        this.pixmap.fill();
        this.texRed = new Texture(this.pixmap);
        this.redMenuBackground = new TextureRegionDrawable(this.texRed);

        this.pixmap.setColor(0.003f, 0.10f, 0.27f, 0.6f);
        this.pixmap.fill();
        this.texBlue = new Texture(this.pixmap);
        this.blueMenuBackground = new TextureRegionDrawable(this.texBlue);

        this.pixmap.setColor(0.1f, 0.6f, 0.1f, 0.6f);
        this.pixmap.fill();
        this.texGreen = new Texture(this.pixmap);
        this.greenMenuBackground = new TextureRegionDrawable(this.texGreen);

        this.pixmap.setColor(1.0f, 0.5f, 0.0f, 0.6f);
        this.pixmap.fill();
        this.texOrange = new Texture(this.pixmap);
        this.orangeMenuBackground = new TextureRegionDrawable(this.texOrange);

        this.buttonStyle = new TextButtonStyle();
        this.buttonStyle.font = Global.fontGeneratorPricedown.generateFont(Global.fontParameters.get(1));
        this.buttonStyle.overFontColor = Color.LIGHT_GRAY;

        this.listStyle = new ListStyle();
        this.listStyle.font = Global.fontGeneratorChalet.generateFont(Global.fontParameters.get(2));
        this.listStyle.fontColorSelected = Color.WHITE;
        this.listStyle.fontColorUnselected = Color.DARK_GRAY;

        this.pixmap.setColor(0, 0, 0, 0.5f);
        this.pixmap.fill();
        this.listStyle.selection = new TextureRegionDrawable(new Texture(this.pixmap));

        this.labelStyle = new Label.LabelStyle();
        this.labelStyle.font = Global.fontGeneratorPricedown.generateFont(Global.fontParameters.get(1));
        this.labelStyle.fontColor = Color.WHITE;

        this.pixmap.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        this.pixmap.fill();
        this.texFuelBg = new Texture(this.pixmap);

        this.pixmap.setColor(0.2f, 0.8f, 0.2f, 1f);
        this.pixmap.fill();
        this.texFuelFill = new Texture(this.pixmap);

        this.pixmap.setColor(0.2f, 0.2f, 0.8f, 1f);
        this.pixmap.fill();
        this.texFuelFillBlue = new Texture(this.pixmap);

        this.pixmap.setColor(1f, 1f, 1f, 1f);
        this.pixmap.fill();
        this.texWhite = new Texture(this.pixmap);
        this.whiteBorder = new TextureRegionDrawable(this.texWhite);

        this.fuelBarStyle = new ProgressBarStyle();
        TextureRegionDrawable bgDrawable = new TextureRegionDrawable(this.texFuelBg);
        bgDrawable.setMinHeight(30);
        TextureRegionDrawable fillDrawable = new TextureRegionDrawable(this.texFuelFill);
        fillDrawable.setMinHeight(30);
        this.fuelBarStyle.background = bgDrawable;
        this.fuelBarStyle.knobBefore = fillDrawable;

        this.texWheelHeli = Global.assets.get("sprite/character_switch/heli.png", Texture.class);
        this.texWheelBoat = Global.assets.get("sprite/character_switch/boat.png", Texture.class);
        this.texWheelBase = Global.assets.get("sprite/character_switch/base.png", Texture.class);
    }

    private void createTables() {
        // Game HUD
        this.hudTable = new Table();
        this.hudTable.setFillParent(true);
        this.hudTable.align(Align.top);
        this.stage.addActor(this.hudTable);

        // Pause Menu
        this.pauseTable = new Table();
        this.pauseTable.setFillParent(true);
        this.pauseTable.setVisible(false);
        this.pauseTable.setBackground(this.darkMenuBackground);
        this.stage.addActor(this.pauseTable);

        // Alt Control
        this.altTable = new Table();
        this.altTable.setFillParent(true);
        this.altTable.setVisible(false);
        this.altTable.setBackground(this.darkMenuBackground);
        this.altTable.align(Align.center);
        this.stage.addActor(this.altTable);

        // Game Over
        this.gameOverTable = new Table();
        this.gameOverTable.setFillParent(true);
        this.gameOverTable.setVisible(false);
        this.gameOverTable.setBackground(this.redMenuBackground);
        this.stage.addActor(this.gameOverTable);
    }

    private void createButtons() {
        this.buttonBackToGame = new TextButton("Back To Game", this.buttonStyle);
        this.buttonBackToGame.addListener(this);
        this.pauseTable.add(this.buttonBackToGame);

        this.pauseTable.row();

        this.buttonQuit = new TextButton("Quit To Main Menu", this.buttonStyle);
        this.buttonQuit.addListener(this);
        this.pauseTable.add(this.buttonQuit);

        Label gameOverLabel = new Label("WASTED!", this.labelStyle);
        gameOverLabel.setFontScale(2f);
        this.gameOverTable.add(gameOverLabel).padBottom(10).row();

        Label.LabelStyle reasonStyle = new Label.LabelStyle();
        reasonStyle.font = Global.fontGeneratorChalet.generateFont(Global.fontParameters.get(0));
        reasonStyle.fontColor = Color.WHITE;

        this.gameOverReasonLabel = new Label("", reasonStyle);
        this.gameOverTable.add(this.gameOverReasonLabel).padBottom(20).row();

        // Input Field for Username
        com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle textFieldStyle = new com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle();
        textFieldStyle.font = Global.fontGeneratorChalet.generateFont(Global.fontParameters.get(0));
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.cursor = new TextureRegionDrawable(this.texWhite);
        textFieldStyle.selection = new TextureRegionDrawable(this.texBlue);
        textFieldStyle.background = this.darkMenuBackground;

        this.usernameInput = new com.badlogic.gdx.scenes.scene2d.ui.TextField(
                "", textFieldStyle);
        this.usernameInput.setMessageText("Enter Name");
        this.usernameInput.setAlignment(Align.center);
        this.gameOverTable.add(this.usernameInput).width(200).height(40).padBottom(20).row();

        TextButton saveButton = new TextButton("Save Score", this.buttonStyle);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String username = HUDManager.this.usernameInput.getText();
                if (username.isEmpty()) {
                    showNotification("Please enter a name!");
                    return;
                }

                int level = scene.getTrashPooler().currentLevel;
                int collected = scene.getTrashPooler().getCollectedTrashCount();
                int recycled = com.dapasril.finalprojectpbo.recycling.Inventory.getInstance().getRecycledItemsCount();

                com.dapasril.finalprojectpbo.managers.SaveManager.getInstance().saveGame(username, level, collected,
                        recycled);
                showNotification("Score Saved!");
                saveButton.setDisabled(true);
                saveButton.setText("Saved");
            }
        });
        this.gameOverTable.add(saveButton).padBottom(20).row();

        this.buttonRestart = new TextButton("Play Again", this.buttonStyle);
        this.buttonRestart.addListener(this);
        this.gameOverTable.add(this.buttonRestart).padBottom(20).row();

        TextButton quitFromGameOver = new TextButton("Quit To Main Menu", this.buttonStyle);
        quitFromGameOver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                scene.getGame().setScreen(new MainMenuScene(scene.getGame()));
            }
        });
        this.gameOverTable.add(quitFromGameOver);
    }

    private void createLists() {

        this.characterList = new List<>(this.listStyle);
        this.characterList.setAlignment(Align.right | Align.bottom);
        this.characterList.setItems("Heli", "Boat", "Base");

        this.characterWheel = new com.badlogic.gdx.scenes.scene2d.ui.Image(this.texWheelHeli);
        this.altTable.add(this.characterWheel).size(400, 400);
    }

    private void createLabels() {
        this.moneyLabel = new Label("$0", this.labelStyle);

        this.fuelBar = new ProgressBar(0, 100, 1, false, this.fuelBarStyle);
        this.fuelBar.setValue(100);
        this.fuelBar.setWidth(200);
        this.fuelBar.setHeight(30);

        Table borderContainer = new Table();
        borderContainer.setBackground(this.whiteBorder);
        borderContainer.add(fuelBar).width(200).height(30).pad(2);

        this.fuelTable = new Table();
        this.fuelTable.align(Align.bottom | Align.left);
        this.fuelTable.setFillParent(true);
        this.fuelTable.add(borderContainer).padLeft(20).padBottom(20);
        this.stage.addActor(this.fuelTable);

        Table statsTable = new Table();
        statsTable.align(Align.top | Align.right);
        statsTable.setFillParent(true);

        statsTable.add(moneyLabel).padRight(20).padTop(20).row();

        this.stage.addActor(statsTable);

        this.levelTable = new Table();
        this.levelTable.align(Align.bottom | Align.left);
        this.levelTable.setFillParent(true);

        Label.LabelStyle levelStyle = new Label.LabelStyle();
        levelStyle.font = Global.fontGeneratorChalet.generateFont(Global.fontParameters.get(1));
        levelStyle.fontColor = Color.WHITE;
        this.levelLabel = new Label("Level 1", levelStyle);

        Label.LabelStyle progressStyle = new Label.LabelStyle();
        progressStyle.font = Global.fontGeneratorChalet.generateFont(Global.fontParameters.get(0));
        progressStyle.fontColor = Color.WHITE;
        this.trashTitleLabel = new Label("Trash:", progressStyle);
        this.trashValueLabel = new Label("0/10", progressStyle);

        Table trashContainer = new Table();
        trashContainer.setBackground(this.hudBackground);
        trashContainer.add(this.trashTitleLabel).left().expandX().padLeft(10).padTop(5).padBottom(5);
        trashContainer.add(this.trashValueLabel).right().padRight(10).padTop(5).padBottom(5);

        this.levelTable.add(this.levelLabel).padLeft(20).padBottom(5).left().row();
        // Match fuel bar container width (200 + 2*2 padding = 204)
        this.levelTable.add(trashContainer).width(204).padLeft(20).left();

        this.stage.addActor(this.levelTable);
    }

    public void update(float delta) {
        if (this.scene.getTrashPooler() != null) {
            this.levelLabel.setText("Level " + this.scene.getTrashPooler().currentLevel);
            this.trashValueLabel.setText(this.scene.getCurrentLevelCollectedTrashCount() + "/"
                    + this.scene.getTrashPooler().targetTrashCount);
        }

        this.moneyLabel.setText("$" + EconomyManager.getInstance().getMoney());

        // If not accessible, use getSelectedCharacter() but ensure it's synced.
        // Actually, let's use getSelectedCharacter() as it drives the mode.
        String selected = getSelectedCharacter();

        if (selected.equals("Base")) {
            this.fuelTable.setVisible(false);
        } else {
            this.fuelTable.setVisible(true);
            float currentFuel = 0;
            float maxFuel = 1;
            boolean isHeli = selected.equals("Heli");

            if (isHeli && scene.playerHeli != null) {
                currentFuel = scene.playerHeli.currentFuel;
                maxFuel = scene.playerHeli.maxFuel;
            } else if (selected.equals("Boat") && scene.playerBoat != null) {
                currentFuel = scene.playerBoat.currentFuel;
                maxFuel = scene.playerBoat.maxFuel;
            }

            float fuelPercent = (currentFuel / maxFuel) * 100;
            this.fuelBar.setValue(fuelPercent);

            if (fuelPercent < 20) {
                Pixmap lowFuelPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
                lowFuelPixmap.setColor(0.8f, 0.2f, 0.2f, 1f);
                lowFuelPixmap.fill();
                TextureRegionDrawable lowFuelDrawable = new TextureRegionDrawable(new Texture(lowFuelPixmap));
                lowFuelDrawable.setMinHeight(30);
                this.fuelBarStyle.knobBefore = lowFuelDrawable;
                lowFuelPixmap.dispose();
            } else {
                TextureRegionDrawable normalFuelDrawable;
                if (isHeli) {
                    normalFuelDrawable = new TextureRegionDrawable(this.texFuelFill);
                } else {
                    normalFuelDrawable = new TextureRegionDrawable(this.texFuelFillBlue);
                }
                normalFuelDrawable.setMinHeight(30);
                this.fuelBarStyle.knobBefore = normalFuelDrawable;
            }
        }

        float targetPad = this.fuelTable.isVisible() ? 70 : 20;
        if (this.levelTable.getPadBottom() != targetPad) {
            this.levelTable.padBottom(targetPad);
            this.levelTable.invalidate();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.B) && !this.scene.isPaused() && !this.gameOverTable.isVisible()) {
            if (this.shopUI.isVisible) {
                this.shopUI.hide();
            } else {
                this.shopUI.show();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.I) && !this.scene.isPaused() && this.scene.isGroundCrewMode
                && !this.gameOverTable.isVisible()) {
            this.craftingUI.show();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !this.gameOverTable.isVisible()) {
            if (this.shopUI.isVisible) {
                this.shopUI.hide();
            } else if (this.craftingUI.isVisible) {
                this.craftingUI.hide();
            } else {
                this.hudTable.setVisible(false);
                this.pauseTable.setVisible(true);
                this.scene.setPaused(true);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) && !this.pauseTable.isVisible()
                && !this.scene.isPaused() && !this.gameOverTable.isVisible()) {
            this.altTable.setVisible(true);
            this.hudTable.setVisible(false);
        } else {
            this.altTable.setVisible(false);
            this.hudTable.setVisible(true);
        }

        if (this.altTable.isVisible()) {
            updateCharacterWheel();
        }
    }

    private void updateCharacterWheel() {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Invert Y

        float wheelCenterX = this.stage.getWidth() / 2;
        float wheelCenterY = this.stage.getHeight() / 2;

        float dx = mouseX - wheelCenterX;
        float dy = mouseY - wheelCenterY;

        // Calculate angle (0 is right, 90 is top, 180 is left, 270/-90 is bottom)
        float angle = com.badlogic.gdx.math.MathUtils.atan2(dy, dx) * com.badlogic.gdx.math.MathUtils.radiansToDegrees;
        if (angle < 0)
            angle += 360;

        String hovered = selectedCharacter;

        if (angle >= 45 && angle < 135) {
            hovered = "Heli";
            this.characterWheel.setDrawable(new TextureRegionDrawable(this.texWheelHeli));
        } else if (angle >= 135 && angle < 225) {
            hovered = "Boat";
            this.characterWheel.setDrawable(new TextureRegionDrawable(this.texWheelBoat));
        } else if ((angle >= 315 && angle <= 360) || (angle >= 0 && angle < 45)) {
            hovered = "Base";
            this.characterWheel.setDrawable(new TextureRegionDrawable(this.texWheelBase));
        }

        if (!hovered.equals(this.lastHoveredCharacter)) {
            if (this.clickSound.isPlaying()) {
                this.clickSound.stop();
            }
            this.clickSound.play();
            this.lastHoveredCharacter = hovered;

            if (hovered.equals("Heli")) {
                this.altTable.setBackground(this.greenMenuBackground);
            } else if (hovered.equals("Boat")) {
                this.altTable.setBackground(this.blueMenuBackground);
            } else {
                this.altTable.setBackground(this.orangeMenuBackground);
            }
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            this.selectedCharacter = hovered;
            this.characterList.setSelected(selectedCharacter);
        }
    }

    public String getSelectedCharacter() {
        return selectedCharacter;
    }

    public void setSelectedCharacter(String character) {
        this.selectedCharacter = character;
        this.characterList.setSelected(character);
    }

    public void showGameOver(String reason) {
        this.hudTable.setVisible(false);
        this.pauseTable.setVisible(false);
        this.altTable.setVisible(false);
        if (this.shopUI.isVisible)
            this.shopUI.hide();
        if (this.craftingUI.isVisible)
            this.craftingUI.hide();

        this.gameOverReasonLabel.setText(reason);
        this.gameOverTable.setVisible(true);

        this.scene.setPaused(true);
        this.stage.setKeyboardFocus(this.usernameInput);
        this.usernameInput.setText(""); // Clear previous input
    }

    @Override
    public boolean handle(Event event) {
        if (event instanceof ChangeEvent) {
            ChangeEvent e = (ChangeEvent) event;
            if (e.getTarget() == this.buttonQuit) {
                this.scene.getGame().setScreen(new MainMenuScene(this.scene.getGame()));
            }

            if (e.getTarget() == this.buttonBackToGame) {
                this.scene.setPaused(false);
                this.hudTable.setVisible(true);
                this.pauseTable.setVisible(false);
            }

            if (e.getTarget() == this.buttonRestart) {
                this.scene.getGame().setScreen(new DemoWorldScene(this.scene.getGame()));
            }
        }
        return false;
    }

    private Table notificationTable;
    private Label notificationLabel;
    private Label gameOverReasonLabel;

    public void showNotification(String message) {
        if (notificationTable == null) {
            notificationTable = new Table();
            notificationTable.setBackground(this.darkMenuBackground);
            notificationTable.align(Align.left);

            // Use Chalet font, smaller size (index 0)
            Label.LabelStyle notificationStyle = new Label.LabelStyle();
            notificationStyle.font = Global.fontGeneratorChalet.generateFont(Global.fontParameters.get(0));
            notificationStyle.fontColor = Color.WHITE;

            notificationLabel = new Label(message, notificationStyle);
            notificationLabel.setWrap(true);
            notificationTable.add(notificationLabel).width(300).pad(10);

            this.stage.addActor(notificationTable);
        } else {
            notificationLabel.setText(message);
        }

        notificationTable.pack();
        notificationTable.setPosition(20, this.stage.getHeight() - notificationTable.getHeight() - 20);

        notificationTable.setVisible(true);
        notificationTable.getColor().a = 1f;
        notificationTable.clearActions();
        this.notificationSound.play();

        // No fade in, just wait and fade out
        notificationTable.addAction(Actions.sequence(
                Actions.delay(4f),
                Actions.fadeOut(0.5f),
                Actions.run(() -> notificationTable.setVisible(false))));
    }

    public void dispose() {
        if (this.texDark != null)
            this.texDark.dispose();
        if (this.texRed != null)
            this.texRed.dispose();
        if (this.texBlue != null)
            this.texBlue.dispose();
        if (this.texFuelBg != null)
            this.texFuelBg.dispose();
        if (this.texFuelFill != null)
            this.texFuelFill.dispose();
        if (this.texFuelFillBlue != null)
            this.texFuelFillBlue.dispose();
        if (this.texWhite != null)
            this.texWhite.dispose();
        if (this.pixmap != null)
            this.pixmap.dispose();
        if (this.shopUI != null)
            this.shopUI.dispose();
        if (this.craftingUI != null)
            this.craftingUI.dispose();
    }
}
