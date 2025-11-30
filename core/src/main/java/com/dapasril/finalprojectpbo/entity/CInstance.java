package com.dapasril.finalprojectpbo.entity;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;

 
 
public class CInstance {
	public ModelInstance instance;
	public Matrix4 localTransform;
	
	public CInstance(ModelInstance instance) {
		this.instance = instance;
		this.localTransform = new Matrix4();
	}
}
