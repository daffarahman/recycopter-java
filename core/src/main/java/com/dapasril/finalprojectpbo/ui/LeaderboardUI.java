package com.dapasril.finalprojectpbo.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.dapasril.finalprojectpbo.Global;
import com.dapasril.finalprojectpbo.managers.SaveManager;
import com.dapasril.finalprojectpbo.managers.SaveManager.PlayerData;

public class LeaderboardUI {
    private Stage stage;
    private Table rootTable;
    private Table mainTable;
    private Table contentTable;
    public boolean isVisible = false;

    private Texture bgTexture;
    private TextureRegionDrawable bgDrawable;

    public LeaderboardUI(Stage stage) {
        this.stage = stage;
        init();
    }

    private void init() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.5f);
        pixmap.fill();
        bgTexture = new Texture(pixmap);
        bgDrawable = new TextureRegionDrawable(bgTexture);

        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.setVisible(false);

        mainTable = new Table();
        mainTable.setBackground(bgDrawable);

        // Layout configuration using Value for responsiveness
        // padLeft 35% of width to clear buttons
        rootTable.add(mainTable).grow().pad(50).padLeft(Value.percentWidth(0.35f, rootTable));

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = Global.fontGeneratorPricedown.generateFont(Global.fontParameters.get(1));
        titleStyle.fontColor = Color.WHITE;

        Label titleLabel = new Label("LEADERBOARD", titleStyle);
        mainTable.add(titleLabel).pad(20).top().left().row();

        contentTable = new Table();
        contentTable.align(Align.top);

        ScrollPane scrollPane = new ScrollPane(contentTable);
        mainTable.add(scrollPane).grow().pad(20).row();

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = Global.fontGeneratorPricedown.generateFont(Global.fontParameters.get(1));
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.ORANGE;

        TextButton closeButton = new TextButton("Close", buttonStyle);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        mainTable.add(closeButton).pad(20).bottom().right();

        stage.addActor(rootTable);
    }

    public void show() {
        updateLeaderboard();
        rootTable.setVisible(true);
        isVisible = true;
    }

    public void hide() {
        rootTable.setVisible(false);
        isVisible = false;
    }

    private void updateLeaderboard() {
        contentTable.clear();
        Array<PlayerData> data = SaveManager.getInstance().getLeaderboard();

        // Use Pricedown for headers and rows as requested
        Label.LabelStyle headerStyle = new Label.LabelStyle();
        headerStyle.font = Global.fontGeneratorPricedown.generateFont(Global.fontParameters.get(0));
        headerStyle.fontColor = Color.YELLOW;

        Label.LabelStyle rowStyle = new Label.LabelStyle();
        rowStyle.font = Global.fontGeneratorPricedown.generateFont(Global.fontParameters.get(0));
        rowStyle.fontColor = Color.WHITE;

        if (data.size == 0) {
            Label noSavesLabel = new Label("No Saves", rowStyle);
            contentTable.add(noSavesLabel).center().padTop(50);
            return;
        }

        // Header
        Table headerTable = new Table();
        headerTable.add(new Label("Name", headerStyle)).expandX().left();
        headerTable.add(new Label("Lvl", headerStyle)).width(50).center();
        headerTable.add(new Label("Coll", headerStyle)).width(80).center();
        headerTable.add(new Label("Recy", headerStyle)).width(80).center();
        contentTable.add(headerTable).growX().padBottom(10).row();

        // Rows
        for (PlayerData player : data) {
            Table rowTable = new Table();
            rowTable.add(new Label(player.username, rowStyle)).expandX().left();
            rowTable.add(new Label(String.valueOf(player.highestLevel), rowStyle)).width(50).center();
            rowTable.add(new Label(String.valueOf(player.totalCollected), rowStyle)).width(80).center();
            rowTable.add(new Label(String.valueOf(player.totalRecycled), rowStyle)).width(80).center();
            contentTable.add(rowTable).growX().padBottom(5).row();
        }
    }

    public void dispose() {
        if (bgTexture != null) {
            bgTexture.dispose();
        }
    }
}
