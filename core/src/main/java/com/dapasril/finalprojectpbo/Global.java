package com.dapasril.finalprojectpbo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Array;

public class Global {
	public static AssetManager assets;

	private static final int fontSizeCount = 6;
	private static final int initialFontSize = 16;
	public static Array<FreeTypeFontParameter> fontParameters;

	 
	public static Array<BitmapFont> bitmapFontsPricedown;
	public static Array<BitmapFont> bitmapFontsChalet;
	public static FreeTypeFontGenerator fontGeneratorPricedown;
	public static FreeTypeFontGenerator fontGeneratorChalet;

	 
	public static final float WORLD_SIZE = 300f;
	public static boolean debugMode = false;

	public static void init() {
		assets = new AssetManager();

		 
		fontParameters = new Array<FreeTypeFontParameter>();

		fontGeneratorPricedown = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pricedown.otf"));
		fontGeneratorChalet = new FreeTypeFontGenerator(Gdx.files.internal("fonts/chalet.ttf"));
		bitmapFontsPricedown = new Array<BitmapFont>();
		bitmapFontsChalet = new Array<BitmapFont>();

		for (int i = 0; i < fontSizeCount; i++) {
			fontParameters.add(new FreeTypeFontParameter());
			fontParameters.get(i).size = (int) (initialFontSize * (1.61803398875f * (i + 1)));
			fontParameters.get(i).borderWidth = 1.5f;
			fontParameters.get(i).borderColor = Color.BLACK;
			bitmapFontsPricedown.add(fontGeneratorPricedown.generateFont(fontParameters.get(i)));
			bitmapFontsChalet.add(fontGeneratorChalet.generateFont(fontParameters.get(i)));
		}
	}
}
