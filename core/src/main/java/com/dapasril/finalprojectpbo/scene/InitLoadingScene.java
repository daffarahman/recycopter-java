package com.dapasril.finalprojectpbo.scene;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.dapasril.finalprojectpbo.Global;
import com.dapasril.finalprojectpbo.Main;

public class InitLoadingScene extends CScene {

	private boolean isLoaded = false;

	Table table;
	Label loadingLabel;
	LabelStyle labelStyle;

	public InitLoadingScene(Main game) {
		super(game);
		this.loading();

		this.table = new Table();
		this.table.setFillParent(true);
		this.stage.addActor(this.table);

		this.labelStyle = new LabelStyle();
		this.labelStyle.font = Global.fontGeneratorPricedown.generateFont(Global.fontParameters.get(1));
		this.loadingLabel = new Label("Loading", this.labelStyle);
		this.table.add(this.loadingLabel);
	}

	@Override
	public void update2D(float delta) {
		if (isLoaded && Global.assets.update()) {
			this.game.setScreen(new StoryIntroScene(this.game));
		}
	}

	private void loading() {
		Global.assets.load("model/sky1/sky1.g3db", Model.class);
		Global.assets.load("model/heli67/heli67_body.g3db", Model.class);
		Global.assets.load("model/heli67/heli67_prop.g3db", Model.class);
		Global.assets.load("model/island1/island1.g3db", Model.class);
		Global.assets.load("model/water1/water1.g3db", Model.class);
		Global.assets.load("model/barrel1/barrel1.g3db", Model.class);
		Global.assets.load("model/missile1/missile1.g3db", Model.class);
		Global.assets.load("model/island2/island2.g3db", Model.class);
		Global.assets.load("model/water2/water2.g3db", Model.class);
		Global.assets.load("model/boat1/boat1.g3db", Model.class);
		Global.assets.load("model/trash1/trash1.g3db", Model.class);
		Global.assets.load("model/hq/hq1.g3db", Model.class);
		Global.assets.load("model/hq/hq2.g3db", Model.class);
		Global.assets.load("model/tower1/tower1.g3db", Model.class);

		// Story Assets
		for (int i = 1; i <= 5; i++) {
			Global.assets.load("story/scene" + i + ".png", Texture.class);
		}
		Global.assets.load("audio/intro_bgm.mp3", Music.class);
		Global.assets.load("audio/collect1.mp3", Music.class);
		Global.assets.load("audio/notification1.mp3", Music.class);
		Global.assets.load("audio/heli1.mp3", Music.class);
		Global.assets.load("audio/click1.mp3", Music.class);

		Global.assets.load("sprite/placeholder.png", Texture.class);

		Global.assets.load("sprite/recyclable/plastic_bottle.png", Texture.class);
		Global.assets.load("sprite/recyclable/aluminum_can.png", Texture.class);
		Global.assets.load("sprite/recyclable/glass_bottle.png", Texture.class);
		Global.assets.load("sprite/recyclable/newspaper.png", Texture.class);
		Global.assets.load("sprite/recyclable/cardboard_box.png", Texture.class);
		Global.assets.load("sprite/recyclable/e_waste.png", Texture.class);
		Global.assets.load("sprite/recyclable/old_tire.png", Texture.class);
		Global.assets.load("sprite/recyclable/scrap_metal.png", Texture.class);
		Global.assets.load("sprite/recyclable/car_battery.png", Texture.class);
		Global.assets.load("sprite/recyclable/old_clothes.png", Texture.class);

		Global.assets.load("sprite/product/recycled_plastic.png", Texture.class);
		Global.assets.load("sprite/product/aluminum_ingot.png", Texture.class);
		Global.assets.load("sprite/product/glass_block.png", Texture.class);
		Global.assets.load("sprite/product/recycled_paper.png", Texture.class);
		Global.assets.load("sprite/product/cardboard_box.png", Texture.class);
		Global.assets.load("sprite/product/circuit_board.png", Texture.class);
		Global.assets.load("sprite/product/rubber_mat.png", Texture.class);
		Global.assets.load("sprite/product/steel_beam.png", Texture.class);
		Global.assets.load("sprite/product/power_cell.png", Texture.class);
		Global.assets.load("sprite/product/textile_roll.png", Texture.class);

		Global.assets.load("sprite/character_switch/heli.png", Texture.class);
		Global.assets.load("sprite/character_switch/boat.png", Texture.class);
		Global.assets.load("sprite/character_switch/base.png", Texture.class);

		this.isLoaded = true;
	}

}
