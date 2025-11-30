package com.dapasril.finalprojectpbo.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.dapasril.finalprojectpbo.Main;

public abstract class CScene3D extends CScene {

	protected PerspectiveCamera cam;
	protected Environment environment;

	Array<ModelBatch> passBatches;

	protected Array<ModelInstance> instances;

	public CScene3D(Main game) {
		super(game);

		 
		this.cam = new PerspectiveCamera(
				67, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		this.cam.position.set(0, 5f, -10f);
		this.cam.lookAt(0, 0, 0);
		this.cam.near = 0.1f;
		this.cam.far = 2000f;
		this.cam.update();

		 
		this.environment = new Environment();
		this.environment.set(
				new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		this.environment.add(
				new DirectionalLight()
						.set(1f, 1f, 1f, -1f, -0.8f, -0.2f));

		 
		this.game.modelBatch = new ModelBatch();

		this.instances = new Array<ModelInstance>();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(
				this.backgroundColor.r,
				this.backgroundColor.g,
				this.backgroundColor.b,
				this.backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		if (!isPaused) {
			this.update3D(delta);
		}

		this.game.modelBatch.begin(this.cam);
		this.game.modelBatch.render(this.instances, this.environment);
		this.game.modelBatch.end();

		this.cam.update();
		this.cam.up.set(Vector3.Y);

		this.game.spriteBatch.begin();
		this.update2D(delta);
		this.game.spriteBatch.end();

		this.stage.act(delta);
		this.stage.draw();
	}

	protected abstract void update3D(float delta);

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		this.cam.viewportWidth = width;
		this.cam.viewportHeight = height;
		this.cam.update();
	}
}
