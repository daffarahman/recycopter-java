package com.dapasril.finalprojectpbo.entity.vehicle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.dapasril.finalprojectpbo.Global;
import com.dapasril.finalprojectpbo.entity.CInstance;
import com.dapasril.finalprojectpbo.entity.trash.Trash;

public class Helicopter extends CVehicle {
	public boolean isRotorMoving;
	public float rotorSpeed = 25f;

	public boolean collision;

	private final float ROTOR_POWER = 32000f;
	private final float HOVER_FACTOR = 0.433f;
	private final float PITCH_ROLL_TORQUE = 35000f;
	private final float YAW_TORQUE = 100000f;
	private final float DRAG_LINEAR = 0.04f;

	private Trash grabbedTrash = null;
	private final float PICKUP_DISTANCE = 8f;
	private final Vector3 TRASH_ATTACH_OFFSET = new Vector3(0, -3f, 0);

	private Music heliSound;

	public Helicopter() {
		super("Helicopter", createCollisionShape(), 1590);
		this.instances.put("Fuselage",
				new CInstance(
						new ModelInstance(
								Global.assets.get("model/heli67/heli67_body.g3db", Model.class))));
		this.instances.put("Rotor",
				new CInstance(
						new ModelInstance(
								Global.assets.get("model/heli67/heli67_prop.g3db", Model.class))));

		this.heliSound = Global.assets.get("audio/heli1.mp3", Music.class);
		this.heliSound.setLooping(true);
		this.heliSound.setVolume(0.75f);

		this.rb.setDamping(0.05f, 0.35f);
	}

	private static btBoxShape createCollisionShape() {
		Model bodyModel = Global.assets.get("model/heli67/heli67_body.g3db", Model.class);
		BoundingBox box = new BoundingBox();
		bodyModel.calculateBoundingBox(box);

		Vector3 dimensions = new Vector3();
		box.getDimensions(dimensions);
		return new btBoxShape(dimensions.scl(0.5f));
	}

	@Override
	public void update() {
		this.rb.getWorldTransform(this.rootTransform);
		super.update();
		this.rb.activate();

		Matrix4 transform = this.rootTransform;
		Vector3 linearVel = this.rb.getLinearVelocity();
		Vector3 angularVel = this.rb.getAngularVelocity();

		Vector3 forwardDir = new Vector3(0, 0, -1).rot(transform).nor();
		Vector3 upDir = new Vector3(0, 1, 0).rot(transform).nor();
		Vector3 rightDir = new Vector3(1, 0, 0).rot(transform).nor();

		float engineForce = 0;

		if (this.currentFuel > 0 && this.isEngineOn) {
			if (!this.heliSound.isPlaying()) {
				this.heliSound.play();
			}
		} else {
			if (this.heliSound.isPlaying()) {
				this.heliSound.stop();
			}
		}

		if (this.vehicleMode == eVehicleStatus.PLAYABLE && this.currentFuel > 0 && this.isEngineOn) {
			if (Gdx.input.isKeyPressed(Input.Keys.W)) {
				engineForce = ROTOR_POWER;
			} else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
				engineForce = ROTOR_POWER * -0.05f;
			} else {
				engineForce = ROTOR_POWER * HOVER_FACTOR;
			}

			this.rb.applyCentralForce(upDir.cpy().scl(engineForce));

			Vector3 torque = new Vector3(0, 0, 0);

			if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
				torque.add(rightDir.cpy().scl(PITCH_ROLL_TORQUE * 3f));
			}

			if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
				torque.add(rightDir.cpy().scl(-PITCH_ROLL_TORQUE * 3f));
			}

			if (Gdx.input.isKeyPressed(Input.Keys.A)) {
				torque.add(forwardDir.cpy().scl(PITCH_ROLL_TORQUE));
			}

			if (Gdx.input.isKeyPressed(Input.Keys.D)) {
				torque.add(forwardDir.cpy().scl(-PITCH_ROLL_TORQUE));
			}

			if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
				torque.add(upDir.cpy().scl(YAW_TORQUE));
			}

			if (Gdx.input.isKeyPressed(Input.Keys.E)) {
				torque.add(upDir.cpy().scl(-YAW_TORQUE));
			}

			this.rb.applyTorque(torque);

			this.rb.setAngularVelocity(angularVel.scl(0.85f));

			Vector3 dragForce = linearVel.cpy().scl(-1).scl(linearVel.len() * DRAG_LINEAR);
			this.rb.applyCentralForce(dragForce);
		}

		if (this.isRotorMoving && this.currentFuel > 0 && this.isEngineOn) {
			this.instances.get("Rotor").localTransform.rotate(new Vector3(0, 1f, 0), this.rotorSpeed);
		}

		if (this.currentFuel > 0 && this.isEngineOn) {
			this.currentFuel -= this.fuelConsumptionRate * Gdx.graphics.getDeltaTime();
			if (this.currentFuel < 0) {
				this.currentFuel = 0;
			}
		}

		if (this.grabbedTrash != null) {
			updateGrabbedTrashPosition(transform);
		}
	}

	public void pickupTrash(Trash trash) {
		if (trash != null && trash.isCollectable()) {
			this.grabbedTrash = trash;
			trash.grab();

			trash.rb.setCollisionFlags(trash.rb.getCollisionFlags() |
					btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE |
					btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
			trash.rb.setActivationState(4);
			trash.rb.setLinearVelocity(Vector3.Zero);
			trash.rb.setAngularVelocity(Vector3.Zero);
		}
	}

	public void dropTrash() {
		if (this.grabbedTrash != null) {
			this.grabbedTrash.release();

			this.grabbedTrash.rb.setCollisionFlags(this.grabbedTrash.rb.getCollisionFlags() &
					~(btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE
							| btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT));
			this.grabbedTrash.rb.activate();

			Vector3 dropVelocity = new Vector3(0, -2f, 0);
			this.grabbedTrash.rb.setLinearVelocity(dropVelocity);

			this.grabbedTrash = null;
		}
	}

	private void updateGrabbedTrashPosition(Matrix4 heliTransform) {
		if (this.grabbedTrash != null) {

			Matrix4 trashTransform = new Matrix4(heliTransform);

			trashTransform.translate(TRASH_ATTACH_OFFSET);

			this.grabbedTrash.rb.setWorldTransform(trashTransform);
			this.grabbedTrash.rootTransform.set(trashTransform);
			this.grabbedTrash.updateVisuals();
		}
	}

	public Trash getGrabbedTrash() {
		return this.grabbedTrash;
	}

	public float getPickupDistance() {
		return this.PICKUP_DISTANCE;
	}

	public void refillFuel() {
		this.currentFuel = this.maxFuel;
	}

	public void updateSound(Vector3 camPos, Vector3 camDir, Vector3 camUp) {
		if (this.currentFuel <= 0 || !this.isEngineOn) {
			return;
		}

		Vector3 heliPos = this.getPosition();
		float distance = camPos.dst(heliPos);
		float maxDistance = 150f;

		// Volume based on distance
		float volume = MathUtils.clamp(1f - (distance / maxDistance), 0f, 1f);
		volume *= 0.75f; // Max volume scaling

		// Pan based on direction
		Vector3 camRight = new Vector3(camDir).crs(camUp).nor();
		Vector3 toHeli = new Vector3(heliPos).sub(camPos).nor();
		float pan = toHeli.dot(camRight); // -1 (left) to 1 (right)

		this.heliSound.setPan(pan, volume);
	}

	public void stopSound() {
		if (this.heliSound.isPlaying()) {
			this.heliSound.pause();
		}
	}

	public void resumeSound() {
		if (this.currentFuel > 0 && this.isEngineOn && !this.heliSound.isPlaying()) {
			this.heliSound.play();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}
