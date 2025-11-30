package com.dapasril.finalprojectpbo.entity.trash;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.dapasril.finalprojectpbo.Global;
import com.dapasril.finalprojectpbo.entity.CEntityPhysical;
import com.dapasril.finalprojectpbo.entity.CInstance;

public class Trash extends CEntityPhysical {
	public static enum eTrashGroup {
		SINGLES,
		PACKED
	}

	public static enum eTrashType {
		PLASTIC_BOTTLE,
		METAL_CAN,
		CARDBOARD_BOX,
		PLASTIC_BAG,
		TIRE,
		BARREL
	}

	public enum eTrashState {
		IDLE,
		GRABBED,
		COLLECTED
	}

	private eTrashGroup trashGroup;
	private eTrashType trashType;
	private eTrashState state;

	public Matrix4 rootTransform;

	public Trash(String name, eTrashGroup trashGroup, eTrashType trashType, Vector3 position, Model trashModel) {
		super(name, new btSphereShape(1f), 10);

		this.trashGroup = trashGroup;
		this.trashType = trashType;
		this.state = eTrashState.IDLE;

		this.rootTransform = new Matrix4();
		this.instances.put(name, new CInstance(new ModelInstance(trashModel)));
		this.rootTransform.setTranslation(position);
		this.instances.get(name).instance.transform.set(this.rootTransform);

		this.rb.setWorldTransform(this.rootTransform);
		this.rb.setFriction(0.8f);
		this.rb.setRestitution(0.3f);
		this.rb.activate();

		this.contents = new java.util.HashMap<>();
		generateContents();
	}

	private java.util.Map<com.dapasril.finalprojectpbo.recycling.RecyclableItem, Integer> contents;

	private void generateContents() {
		int itemCount = com.badlogic.gdx.math.MathUtils.random(1, 3);
		com.dapasril.finalprojectpbo.recycling.RecyclableItem item = null;

		switch (this.trashType) {
			case PLASTIC_BOTTLE:
				item = com.dapasril.finalprojectpbo.recycling.RecyclableItem.PLASTIC_BOTTLE;
				break;
			case METAL_CAN:
				item = com.dapasril.finalprojectpbo.recycling.RecyclableItem.ALUMINUM_CAN;
				break;
			case CARDBOARD_BOX:
				item = com.dapasril.finalprojectpbo.recycling.RecyclableItem.CARDBOARD_BOX;
				break;
			case TIRE:
				item = com.dapasril.finalprojectpbo.recycling.RecyclableItem.OLD_TIRE;
				break;
			case BARREL:
				item = com.dapasril.finalprojectpbo.recycling.RecyclableItem.SCRAP_METAL;
				break;
			case PLASTIC_BAG:
			default:
				// Random mix for generic trash bags
				com.dapasril.finalprojectpbo.recycling.RecyclableItem[] possibleItems = {
						com.dapasril.finalprojectpbo.recycling.RecyclableItem.PLASTIC_BOTTLE,
						com.dapasril.finalprojectpbo.recycling.RecyclableItem.ALUMINUM_CAN,
						com.dapasril.finalprojectpbo.recycling.RecyclableItem.NEWSPAPER,
						com.dapasril.finalprojectpbo.recycling.RecyclableItem.GLASS_BOTTLE
				};
				item = possibleItems[com.badlogic.gdx.math.MathUtils.random(0, possibleItems.length - 1)];
				break;
		}

		if (item != null) {
			contents.put(item, itemCount);
		}
	}

	public java.util.Map<com.dapasril.finalprojectpbo.recycling.RecyclableItem, Integer> getContents() {
		return contents;
	}

	public void update(float delta) {
		this.rb.getWorldTransform(this.rootTransform);
		checkWorldBounds(Global.WORLD_SIZE);
		updateVisuals();
	}

	private void checkWorldBounds(float worldSize) {
		Vector3 pos = new Vector3();
		this.rootTransform.getTranslation(pos);
		boolean teleported = false;

		if (pos.x > worldSize) {
			this.rootTransform.setTranslation(-worldSize, pos.y, pos.z);
			teleported = true;
		} else if (pos.x < -worldSize) {
			this.rootTransform.setTranslation(worldSize, pos.y, pos.z);
			teleported = true;
		}

		this.rootTransform.getTranslation(pos);

		if (pos.z > worldSize) {
			this.rootTransform.setTranslation(pos.x, pos.y, -worldSize);
			teleported = true;
		} else if (pos.z < -worldSize) {
			this.rootTransform.setTranslation(pos.x, pos.y, worldSize);
			teleported = true;
		}

		if (teleported) {
			this.rb.setWorldTransform(this.rootTransform);
		}
	}

	public void updateVisuals() {
		for (CInstance instance : this.instances.values()) {
			instance.instance.transform.set(this.rootTransform).mul(instance.localTransform);
		}
	}

	public void grab() {
		this.state = eTrashState.GRABBED;
		this.rb.setActivationState(4);
	}

	public void release() {
		this.state = eTrashState.IDLE;
		this.rb.activate();
	}

	public void collect() {
		this.state = eTrashState.COLLECTED;
	}

	public eTrashGroup getTrashGroup() {
		return this.trashGroup;
	}

	public eTrashType getTrashType() {
		return this.trashType;
	}

	public eTrashState getState() {
		return this.state;
	}

	public Vector3 getPosition() {
		Vector3 pos = new Vector3();
		this.rootTransform.getTranslation(pos);
		return pos;
	}

	public boolean isCollectable() {
		return this.state == eTrashState.IDLE;
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}
