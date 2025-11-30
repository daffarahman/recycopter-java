package com.dapasril.finalprojectpbo.entity.vehicle;

import java.util.HashMap;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.dapasril.finalprojectpbo.entity.CInstance;
import com.dapasril.finalprojectpbo.entity.CEntityPhysical;

public abstract class CVehicle extends CEntityPhysical {
	public static final float DEFAULT_MAX_FUEL = 100f;
	public static final float DEFAULT_FUEL_CONSUMPTION_RATE = 0.25f;

	public static enum eVehicleStatus {
		PLAYABLE,
		NPC,
		AI,
		STATIC
	}

	public Matrix4 rootTransform;
	public eVehicleStatus vehicleMode = eVehicleStatus.NPC;

	public float currentFuel = DEFAULT_MAX_FUEL;
	public float maxFuel = DEFAULT_MAX_FUEL;
	public float fuelConsumptionRate = DEFAULT_FUEL_CONSUMPTION_RATE;
	public boolean isEngineOn = false;

	public CVehicle(String name, btCollisionShape collisionShape, float mass) {
		super(name, collisionShape, mass);
		this.rootTransform = new Matrix4().setToTranslation(0, 0, 0);
		this.instances = new HashMap<String, CInstance>();
		this.rb.setFriction(0.8f);
	}

	public void update() {
		if (!this.isSafe()) {
			return;
		}

		this.rb.getWorldTransform(this.rootTransform);

		for (CInstance i : this.instances.values()) {
			i.instance.transform.set(this.rootTransform).mul(i.localTransform);
		}
	}

	public Vector3 getPosition() {
		Vector3 pos = new Vector3();
		this.rootTransform.getTranslation(pos);
		return pos;
	}

	public Quaternion getRotation() {
		Quaternion rot = new Quaternion();
		this.rootTransform.getRotation(rot);
		return rot;
	}

	public void toggleEngine() {
		this.isEngineOn = !this.isEngineOn;
	}
}
