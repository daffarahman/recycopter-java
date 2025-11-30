package com.dapasril.finalprojectpbo.entity;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

 
 
public class CEntityPhysical extends CEntity {
	public final btRigidBody rb;
	public final btCollisionShape collisionShape;
	public final btRigidBody.btRigidBodyConstructionInfo constructionInfo;
	private static Vector3 localIntertia = new Vector3();

	public
	CEntityPhysical(
			String name,
			btCollisionShape collisionShape,
			float mass) {
		super(name);
		
		this.collisionShape = collisionShape;
		
		if(mass > 0) {
			this.collisionShape.calculateLocalInertia(mass, localIntertia);
		} else {
			localIntertia.set(0, 0, 0);
		}
		
		this.constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, this.collisionShape, localIntertia);
		this.rb = new btRigidBody(this.constructionInfo);
	}
	
	@Override
	public void dispose() {
		super.dispose();

		if(this.rb != null) this.rb.dispose();
		if(this.collisionShape != null) this.collisionShape.dispose();
		if(this.constructionInfo != null) this.constructionInfo.dispose();

		this.collisionShape.dispose();
		this.constructionInfo.dispose();
	}
}
