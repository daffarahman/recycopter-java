package com.dapasril.finalprojectpbo.physics;

import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;

public class CContactListener extends ContactListener {
	@Override
	public boolean
	onContactAdded(
			btManifoldPoint cp,
			btCollisionObjectWrapper colObj0Wrap, int partId0, int index0,
			btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {
		return true;
	}
}
