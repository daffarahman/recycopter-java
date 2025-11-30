package com.dapasril.finalprojectpbo.entity;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

 
public class CEntity implements Disposable {	
	public String name;
	
	 
	protected HashMap<String, CInstance> instances;
	
	public CEntity(String name) {
		this.name = name;
		this.instances = new HashMap<String, CInstance>();
	}
	
	public void addInstance(String name, ModelInstance instance) {
		this.instances.put(name, new CInstance(instance));
	}
	
	
	 
	public CInstance getCInstance(String name) {
		return this.instances.get(name);
	}
	public ModelInstance getInstance(String name) {
		return this.instances.get(name).instance;
	}
	
	public Array<ModelInstance> getInstances() {
		Array<ModelInstance> i = new Array<ModelInstance>();
		for(CInstance ci : this.instances.values()) {
			i.add(ci.instance);
		}
		return i;
	}
	
	public boolean isSafe() {
		boolean safe = true;
		for(CInstance i : this.instances.values()) {
			safe = safe && i.instance != null;
		}
		return safe;
	}

	@Override
	public void dispose() {
		this.instances.clear();
	}
	
}
