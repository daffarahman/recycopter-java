package com.dapasril.finalprojectpbo.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.dapasril.finalprojectpbo.Main;

public abstract class CScene implements Screen {

	protected final Main game;
	protected Stage stage;
	protected Color backgroundColor;

	protected boolean isPaused = false;

	public CScene(Main game) {
		this.game = game;
		this.stage = new Stage(new ScreenViewport());
		this.backgroundColor = Color.BLACK;
	}

	public Stage getStage() {
		return this.stage;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(
				this.backgroundColor.r,
				this.backgroundColor.g,
				this.backgroundColor.b,
				this.backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		this.game.spriteBatch.begin();
		this.update2D(delta);
		this.game.spriteBatch.end();

		this.stage.act(delta);
		this.stage.draw();
	}

	public abstract void update2D(float delta);

	@Override
	public void resize(int width, int height) {
		this.stage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose() {
		this.stage.dispose();
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(this.stage);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	public Main getGame() {
		return this.game;
	}

	public boolean isPaused() {
		return this.isPaused;
	}

	public void setPaused(boolean paused) {
		this.isPaused = paused;
	}
}
