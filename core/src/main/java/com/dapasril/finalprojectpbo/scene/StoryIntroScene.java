package com.dapasril.finalprojectpbo.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.dapasril.finalprojectpbo.Main;
import com.dapasril.finalprojectpbo.Global;

public class StoryIntroScene extends CScene {

    private enum SlideState {
        SLIDE_IN,
        SHOWING,
        SLIDE_OUT
    }

    private Texture[] slides;
    private int currentSlideIndex = 0;
    private int nextSlideIndex = 0;

    private Music bgm;

    private boolean isAutoMode = true;
    private float slideTimer = 0f;
    private final float SLIDE_DISPLAY_TIME = 1f;
    private final float SLIDE_ANIMATION_TIME = 0.25f;

    private SlideState currentState = SlideState.SLIDE_IN;
    private float animationTimer = 0f;

    private float currentSlideOffsetX = 0f;
    private float nextSlideOffsetX = 0f;
    private ShapeRenderer shapeRenderer;
    private float loadingWheelRotation = 0f;

    public StoryIntroScene(Main game) {
        super(game);

        slides = new Texture[] {
                Global.assets.get("story/scene1.png", Texture.class),
                Global.assets.get("story/scene2.png", Texture.class),
                Global.assets.get("story/scene3.png", Texture.class),
                Global.assets.get("story/scene4.png", Texture.class),
                Global.assets.get("story/scene5.png", Texture.class),
        };

        bgm = Global.assets.get("audio/intro_bgm.mp3", Music.class);

        bgm.setVolume(1f);
        bgm.setLooping(true);
        bgm.play();

        currentSlideOffsetX = this.stage.getViewport().getWorldWidth();

        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void update2D(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            isAutoMode = !isAutoMode;
            System.out.println("Auto Mode: " + (isAutoMode ? "ON" : "OFF"));
        }

        if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (!isAutoMode || currentState == SlideState.SHOWING) {
                triggerNextSlide();
                if (nextSlideIndex >= slides.length)
                    return;
            }
        }

        if (isAutoMode) {
            updateAutoMode(delta);
            if (nextSlideIndex >= slides.length)
                return;
        }

        updateAnimations(delta);

        loadingWheelRotation += delta * 360f;
        if (loadingWheelRotation >= 360f) {
            loadingWheelRotation -= 360f;
        }

        drawSlides();
    }

    private void updateAutoMode(float delta) {
        if (currentState == SlideState.SHOWING) {
            slideTimer += delta;
            if (slideTimer >= SLIDE_DISPLAY_TIME) {
                triggerNextSlide();
            }
        }
    }

    private void updateAnimations(float delta) {
        animationTimer += delta;
        float progress = Math.min(animationTimer / SLIDE_ANIMATION_TIME, 1.0f);
        float screenWidth = this.stage.getViewport().getWorldWidth();

        switch (currentState) {
            case SLIDE_IN:
                currentSlideOffsetX = Interpolation.smooth.apply(screenWidth, 0, progress);

                if (progress >= 1.0f) {
                    currentState = SlideState.SHOWING;
                    currentSlideOffsetX = 0f;
                    slideTimer = 0f;
                    animationTimer = 0f;
                }
                break;

            case SLIDE_OUT:
                currentSlideOffsetX = Interpolation.smooth.apply(0, -screenWidth, progress);
                nextSlideOffsetX = Interpolation.smooth.apply(screenWidth, 0, progress);

                if (progress >= 1.0f) {
                    currentSlideIndex = nextSlideIndex;
                    currentState = SlideState.SHOWING;
                    currentSlideOffsetX = 0f;
                    nextSlideOffsetX = 0f;
                    slideTimer = 0f;
                    animationTimer = 0f;
                }
                break;

            case SHOWING:
                break;
        }
    }

    private void drawSlides() {
        this.game.spriteBatch.setProjectionMatrix(this.stage.getCamera().combined);

        float screenWidth = this.stage.getViewport().getWorldWidth();
        float screenHeight = this.stage.getViewport().getWorldHeight();

        if (currentSlideIndex < slides.length) {
            Texture currentTexture = slides[currentSlideIndex];
            this.game.spriteBatch.draw(
                    currentTexture,
                    currentSlideOffsetX,
                    0,
                    screenWidth,
                    screenHeight);
        }
        if (currentState == SlideState.SLIDE_OUT && nextSlideIndex < slides.length) {
            Texture nextTexture = slides[nextSlideIndex];
            this.game.spriteBatch.draw(
                    nextTexture,
                    nextSlideOffsetX,
                    0,
                    screenWidth,
                    screenHeight);
        }

        drawLoadingIndicator(screenWidth, screenHeight);
    }

    private void drawLoadingIndicator(float screenWidth, float screenHeight) {
        float padding = 30f;
        float wheelRadius = 15f;
        float wheelCenterX = screenWidth - padding - wheelRadius - 10f;
        float wheelCenterY = padding + wheelRadius;

        this.game.spriteBatch.end();
        shapeRenderer.setProjectionMatrix(this.stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);

        for (int i = 0; i < 8; i++) {
            float angle = loadingWheelRotation + (i * 45f);
            float radians = (float) Math.toRadians(angle);
            float x1 = wheelCenterX + (float) Math.cos(radians) * wheelRadius * 0.3f;
            float y1 = wheelCenterY + (float) Math.sin(radians) * wheelRadius * 0.3f;
            float x2 = wheelCenterX + (float) Math.cos(radians) * wheelRadius;
            float y2 = wheelCenterY + (float) Math.sin(radians) * wheelRadius;
            shapeRenderer.line(x1, y1, x2, y2);
        }

        shapeRenderer.end();
        this.game.spriteBatch.begin();
    }

    private void triggerNextSlide() {
        nextSlideIndex = currentSlideIndex + 1;

        if (nextSlideIndex >= slides.length) {
            changeScreenToMenu();
            return;
        }

        currentState = SlideState.SLIDE_OUT;
        animationTimer = 0f;
        slideTimer = 0f;
    }

    private void changeScreenToMenu() {
        if (bgm.isPlaying()) {
            bgm.stop();
        }

        this.game.setScreen(new MainMenuScene(this.game));
        this.dispose();
    }

    @Override
    public void dispose() {
        super.dispose();

        // Assets are managed by Global.assets, so we don't dispose them here
        // unless we want to unload them specifically.
        // For now, let's keep them loaded or unload them via AssetManager if needed.
        // But since we are just switching screens, we might want to keep them if we
        // come back?
        // Actually, usually we unload scene specific assets.
        // But the user just asked to change loading to Global.assets.
        // I will comment out manual disposal of textures since AssetManager owns them
        // now.
        /*
         * for (Texture t : slides) {
         * if (t != null)
         * t.dispose();
         * }
         */

        /*
         * if (bgm != null) {
         * bgm.dispose();
         * }
         */

        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
