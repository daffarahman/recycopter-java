package com.dapasril.finalprojectpbo.entity.vehicle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.dapasril.finalprojectpbo.Global;
import com.dapasril.finalprojectpbo.entity.CInstance;
import com.dapasril.finalprojectpbo.entity.trash.Trash;

public class Boat extends CVehicle {

	private static final float THRUST_FORCE = 25000f;
	private static final float TURN_SPEED = 2.0f;

	// Trash pickup fields
	private Trash grabbedTrash = null;
	private final float PICKUP_DISTANCE = 8f;
	private final Vector3 TRASH_ATTACH_OFFSET = new Vector3(0, 2f, -3f); // Attach di belakang boat

	public Boat() {
		super("Boat", createCollisionShape(), 1500);
		this.instances.put("Boat Body",
				new CInstance(
						new ModelInstance(Global.assets.get("model/boat1/boat1.g3db", Model.class))));

		this.rb.setDamping(0.2f, 0.4f);
		this.rb.setAngularFactor(new Vector3(0, 1, 0));
	}

	private static btBoxShape createCollisionShape() {
		Model bodyModel = Global.assets.get("model/boat1/boat1.g3db", Model.class);
		BoundingBox box = new BoundingBox();
		bodyModel.calculateBoundingBox(box);

		Vector3 dimensions = new Vector3();
		box.getDimensions(dimensions);
		return new btBoxShape(dimensions.scl(0.5f));
	}

	@Override
	public void update() {
		super.update();
		this.rb.activate();

		if (this.vehicleMode == eVehicleStatus.PLAYABLE && this.isEngineOn && this.currentFuel > 0) {
			Vector3 forwardDir = new Vector3(0, 0, 1).rot(this.rootTransform).nor();

			if (Gdx.input.isKeyPressed(Input.Keys.W)) {
				this.rb.applyCentralForce(forwardDir.cpy().scl(THRUST_FORCE));
			}

			if (Gdx.input.isKeyPressed(Input.Keys.S)) {
				this.rb.applyCentralForce(forwardDir.cpy().scl(-THRUST_FORCE));
			}

			if (Gdx.input.isKeyPressed(Input.Keys.A)) {
				this.rb.setAngularVelocity(new Vector3(0, TURN_SPEED, 0));
			} else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
				this.rb.setAngularVelocity(new Vector3(0, -TURN_SPEED, 0));
			} else {
				this.rb.setAngularVelocity(Vector3.Zero);
			}

			// Lateral Drag (Grip)
			Vector3 rightDir = new Vector3(1, 0, 0).rot(this.rootTransform).nor();
			Vector3 velocity = this.rb.getLinearVelocity();
			float lateralSpeed = velocity.dot(rightDir);
			Vector3 lateralForce = rightDir.cpy().scl(-lateralSpeed * 2000f); // Adjust factor for grip strength
			this.rb.applyCentralForce(lateralForce);
		}

		if (this.isEngineOn && this.currentFuel > 0) {
			this.currentFuel -= this.fuelConsumptionRate * Gdx.graphics.getDeltaTime();
			if (this.currentFuel < 0) {
				this.currentFuel = 0;
			}
		}

		// Update grabbed trash position
		if (this.grabbedTrash != null) {
			updateGrabbedTrashPosition(this.rootTransform);
		}

		// Limit Y position
		Vector3 pos = new Vector3();
		this.rootTransform.getTranslation(pos);
		if (pos.y > -39f) {
			Matrix4 transform = this.rb.getWorldTransform();
			transform.setTranslation(pos.x, -39f, pos.z);
			this.rb.setWorldTransform(transform);
			this.rootTransform.set(transform);

			Vector3 velocity = this.rb.getLinearVelocity();
			velocity.y = 0;
			this.rb.setLinearVelocity(velocity);
		}
	}

	// Pickup trash method
	public void pickupTrash(Trash trash) {
		if (trash != null && trash.isCollectable()) {
			this.grabbedTrash = trash;
			trash.grab();

			// Make trash kinematic and disable collision
			trash.rb.setCollisionFlags(trash.rb.getCollisionFlags() |
					btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE |
					btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
			trash.rb.setActivationState(4); // DISABLE_DEACTIVATION
			trash.rb.setLinearVelocity(Vector3.Zero);
			trash.rb.setAngularVelocity(Vector3.Zero);
		}
	}

	// Drop trash method
	public void dropTrash() {
		if (this.grabbedTrash != null) {
			this.grabbedTrash.release();

			// Restore physics properties
			this.grabbedTrash.rb.setCollisionFlags(this.grabbedTrash.rb.getCollisionFlags() &
					~(btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE
							|
							btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT));
			this.grabbedTrash.rb.activate();

			// Drop with slight downward velocity
			Vector3 dropVelocity = new Vector3(0, -2f, 0);
			this.grabbedTrash.rb.setLinearVelocity(dropVelocity);

			this.grabbedTrash = null;
		}
	}

	// Update grabbed trash position to follow boat
	private void updateGrabbedTrashPosition(Matrix4 boatTransform) {
		if (this.grabbedTrash != null) {
			// Create transform for trash relative to boat
			Matrix4 trashTransform = new Matrix4(boatTransform);

			// Apply offset (behind the boat)
			trashTransform.translate(TRASH_ATTACH_OFFSET);

			// Update trash position
			this.grabbedTrash.rb.setWorldTransform(trashTransform);
			this.grabbedTrash.rootTransform.set(trashTransform);
			this.grabbedTrash.updateVisuals();
		}
	}

	// Getter for grabbed trash
	public Trash getGrabbedTrash() {
		return this.grabbedTrash;
	}

	// Getter for pickup distance
	public float getPickupDistance() {
		return this.PICKUP_DISTANCE;
	}

	// Get boat position
	public Vector3 getPosition() {
		Vector3 pos = new Vector3();
		this.rootTransform.getTranslation(pos);
		return pos;
	}

	public void refillFuel() {
		this.currentFuel = this.maxFuel;
	}

	@Override
	public void dispose() {
		super.dispose();
	}

}
