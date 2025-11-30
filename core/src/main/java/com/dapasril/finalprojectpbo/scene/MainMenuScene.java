package com.dapasril.finalprojectpbo.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.dapasril.finalprojectpbo.Global;
import com.dapasril.finalprojectpbo.Main;
import com.dapasril.finalprojectpbo.ui.LeaderboardUI;

public class MainMenuScene extends CScene implements EventListener {

    Table table;
    TextButton buttonPlay, buttonQuit;
    TextButtonStyle buttonStyle;

    LabelStyle labelStyle, titleStyle;
    Label labelTitle;

    private Texture bgTexture;
    private BitmapFont titleFont;

    private LeaderboardUI leaderboardUI;

    public MainMenuScene(Main game) {
        super(game);

        bgTexture = new Texture(Gdx.files.internal("story/intro1.png"));

        this.table = new Table();
        this.table.setFillParent(true);
        this.stage.addActor(this.table);

        FreeTypeFontParameter titleParam = new FreeTypeFontParameter();
        titleParam.size = 90;
        titleParam.borderColor = Color.BLACK;
        titleParam.borderWidth = 4;
        titleParam.shadowOffsetY = 5;
        titleParam.shadowColor = new Color(0, 0, 0, 0.5f);

        titleFont = Global.fontGeneratorPricedown.generateFont(titleParam);

        this.titleStyle = new LabelStyle();
        this.titleStyle.font = titleFont;
        this.titleStyle.fontColor = Color.WHITE;

        this.labelTitle = new Label("RECYCOPTER", this.titleStyle);
        this.labelTitle.setAlignment(Align.left);

        Table titleTable = new Table();
        titleTable.setFillParent(true);
        titleTable.top();
        titleTable.left();
        titleTable.add(this.labelTitle).padTop(50).padLeft(50);
        this.stage.addActor(titleTable);

        this.labelStyle = new LabelStyle();
        this.labelStyle.font = Global.fontGeneratorChalet.generateFont(Global.fontParameters.get(0));

        this.buttonStyle = new TextButtonStyle();
        this.buttonStyle.font = Global.fontGeneratorPricedown.generateFont(Global.fontParameters.get(1));
        this.buttonStyle.fontColor = Color.WHITE;
        this.buttonStyle.overFontColor = Color.ORANGE;

        this.buttonPlay = new TextButton("Start", this.buttonStyle);
        this.buttonPlay.addListener(this);

        this.buttonQuit = new TextButton("Exit", this.buttonStyle);
        this.buttonQuit.addListener(this);

        TextButton buttonLeaderboard = new TextButton("Leaderboard", this.buttonStyle);
        buttonLeaderboard.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                leaderboardUI.show();
            }
        });

        this.table.bottom().left().pad(50);
        this.table.add(this.buttonPlay).align(Align.left).row();
        this.table.add(buttonLeaderboard).align(Align.left).row();
        this.table.add(this.buttonQuit).align(Align.left);

        this.leaderboardUI = new LeaderboardUI(this.stage);
    }

    @Override
    public void update2D(float delta) {

        this.game.spriteBatch.setProjectionMatrix(this.stage.getCamera().combined);
        this.game.spriteBatch.draw(
                bgTexture,
                0,
                0,
                this.stage.getViewport().getWorldWidth(),
                this.stage.getViewport().getWorldHeight());
    }

    @Override
    public boolean handle(Event event) {
        if (event instanceof ChangeEvent) {
            ChangeEvent e = (ChangeEvent) event;
            if (e.getTarget() == this.buttonPlay) {
                this.game.setScreen(new DemoWorldScene(this.game));
            }

            if (e.getTarget() == this.buttonQuit) {
                Gdx.app.exit();
            }
        }
        return false;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (bgTexture != null)
            bgTexture.dispose();
        if (titleFont != null)
            titleFont.dispose();
        if (leaderboardUI != null)
            leaderboardUI.dispose();
    }
}
