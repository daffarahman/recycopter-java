package com.dapasril.finalprojectpbo.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class CTowerCam {
    private PerspectiveCamera cam;
    private Vector3 towerPosition;
    private float yaw = 0f;
    private float pitch = 0f;
    private float lookSpeed = 2f;

    private Vector3 startPosition = new Vector3();
    private Vector3 currentPosition = new Vector3();
    private float transitionTimer = 0f;
    private float transitionDuration = 0.5f;
    private boolean isTransitioning = false;

    public CTowerCam(PerspectiveCamera cam, Vector3 towerPosition) {
        this.cam = cam;
        this.towerPosition = towerPosition.cpy();
        this.currentPosition.set(towerPosition);
    }

    public void startTransition(Vector3 fromPosition) {
        this.startPosition.set(fromPosition);
        this.transitionTimer = 0f;
        this.isTransitioning = true;
    }

    public void update(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            yaw += lookSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            yaw -= lookSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            pitch += lookSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            pitch -= lookSpeed;
        }

        // Zoom
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            cam.fieldOfView -= 50f * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            cam.fieldOfView += 50f * delta;
        }
        cam.fieldOfView = MathUtils.clamp(cam.fieldOfView, 20f, 100f);

        pitch = MathUtils.clamp(pitch, -80f, 80f);

        float radYaw = MathUtils.degreesToRadians * yaw;
        float radPitch = MathUtils.degreesToRadians * pitch;

        Vector3 direction = new Vector3(
                MathUtils.cos(radPitch) * MathUtils.sin(radYaw),
                MathUtils.sin(radPitch),
                MathUtils.cos(radPitch) * MathUtils.cos(radYaw));

        if (isTransitioning) {
            transitionTimer += delta;
            float alpha = MathUtils.clamp(transitionTimer / transitionDuration, 0f, 1f);
            // Ease out cubic
            alpha = 1f - (float) Math.pow(1f - alpha, 3);

            currentPosition.set(startPosition).lerp(towerPosition, alpha);

            if (transitionTimer >= transitionDuration) {
                isTransitioning = false;
                currentPosition.set(towerPosition);
            }
        } else {
            currentPosition.set(towerPosition);
        }

        cam.position.set(currentPosition);
        cam.direction.set(direction).nor();
        cam.up.set(Vector3.Y);
        cam.update();
    }
}
