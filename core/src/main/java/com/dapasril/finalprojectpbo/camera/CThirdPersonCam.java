package com.dapasril.finalprojectpbo.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class CThirdPersonCam extends InputAdapter {
	public PerspectiveCamera cam;

	Matrix4 rootTransform = new Matrix4().setToTranslation(0, 0, 0);
	Matrix4 localTransform = new Matrix4();
	Vector3 pivot = new Vector3();

	public float distanceFromTarget = 17f;

	private float minZoom = 5f;
	private float maxZoom = 50f;
	public float zoomSpeed = 1f;

	private float yaw = 0;
	private float pitch = 20;

	private Vector3 cameraTarget = new Vector3();

	private float smoothnessFollow = 10f;
	private float smoothnessRecenter = 3f;

	public Matrix4 targetTransform;
	public Vector3 targetPos = new Vector3();
	public Quaternion targetRot = new Quaternion();

	public boolean isMouseActive = true;

	private Vector3 startPosition = new Vector3();
	private float transitionTimer = 0f;
	private float transitionDuration = 0.5f;
	private boolean isTransitioning = false;

	public CThirdPersonCam(PerspectiveCamera cam, Matrix4 targetTransform) {
		this.cam = cam;
		this.targetTransform = targetTransform;
	}

	public void startTransition(Vector3 fromPosition) {
		this.startPosition.set(fromPosition);
		this.transitionTimer = 0f;
		this.isTransitioning = true;
	}

	public void updateLerp() {
		this.targetTransform.getTranslation(this.targetPos);
		this.targetTransform.getRotation(this.targetRot);
		this.cameraTarget.lerp(this.targetPos, Gdx.graphics.getDeltaTime() * this.smoothnessFollow);
	}

	public void updatePivot() {
		this.pivot.set(cameraTarget);

		float mouseDeltaX = -Gdx.input.getDeltaX();
		float mouseDeltaY = -Gdx.input.getDeltaY();

		if ((mouseDeltaX != 0 || mouseDeltaY != 0) && this.isMouseActive) {
			yaw += mouseDeltaX * 0.3f;
			pitch += mouseDeltaY * 0.3f;
		} else if (Gdx.input.isKeyPressed(Input.Keys.W) || true) {
			float targetYaw = this.targetRot.getYaw();
			float cameraTargetYaw = targetYaw + 180f;
			yaw = MathUtils.lerpAngleDeg(yaw, cameraTargetYaw, Gdx.graphics.getDeltaTime() * smoothnessRecenter);

		}

		pitch = MathUtils.clamp(pitch, -80f, 80f);

		float radYaw = MathUtils.degreesToRadians * yaw;
		float radPitch = MathUtils.degreesToRadians * pitch;

		float x = this.pivot.x + distanceFromTarget * MathUtils.cos(radPitch) * MathUtils.sin(radYaw);
		float y = this.pivot.y + distanceFromTarget * MathUtils.sin(radPitch);
		float z = this.pivot.z + distanceFromTarget * MathUtils.cos(radPitch) * MathUtils.cos(radYaw);

		cam.position.set(x, y, z);
		cam.lookAt(this.pivot);

		if (isTransitioning) {
			transitionTimer += Gdx.graphics.getDeltaTime();
			float alpha = MathUtils.clamp(transitionTimer / transitionDuration, 0f, 1f);
			// Ease out cubic
			alpha = 1f - (float) Math.pow(1f - alpha, 3);

			cam.position.set(startPosition).lerp(new Vector3(x, y, z), alpha);
			cam.lookAt(this.pivot); // Keep looking at pivot during transition? Or lerp lookAt?
			// For simplicity, let's just lerp position and look at pivot.
			// Ideally we should lerp the lookAt target too if it changes drastically,
			// but pivot is already lerping via updateLerp().

			if (transitionTimer >= transitionDuration) {
				isTransitioning = false;
			}
		}
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		if (amountY > 0) {
			this.distanceFromTarget += this.zoomSpeed;
		} else if (amountY < 0) {
			this.distanceFromTarget -= this.zoomSpeed;
		}

		this.distanceFromTarget = MathUtils.clamp(this.distanceFromTarget, minZoom, maxZoom);

		return true;
	}
}
