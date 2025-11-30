package com.dapasril.finalprojectpbo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.dapasril.finalprojectpbo.scene.InitLoadingScene;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public class Main extends Game {

    public ModelBatch modelBatch;
    public SpriteBatch spriteBatch;
    public AssetManager assets;

    public void create() {
        Bullet.init();
        Global.init();

        this.modelBatch = new ModelBatch();
        this.spriteBatch = new SpriteBatch();

        this.setScreen(new InitLoadingScene(this));
    }

    public void render() {
        super.render();
    }

    public void dispose() {
    }
}
