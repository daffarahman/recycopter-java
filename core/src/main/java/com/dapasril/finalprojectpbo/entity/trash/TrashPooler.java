package com.dapasril.finalprojectpbo.entity.trash;

import java.util.Random;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.utils.Array;
import com.dapasril.finalprojectpbo.Global;
import com.dapasril.finalprojectpbo.entity.trash.Trash.eTrashType;

public class TrashPooler {
    public static final float TRASH_SPAWN_INTERVAL = 5f;

    public int targetTrashCount = 10;
    public int currentLevel = 1;

    private Array<Trash> trashPool;
    private btDynamicsWorld dynamicsWorld;

    private float spawnRadius = Global.WORLD_SIZE;
    private Vector3 spawnCenter;
    private int collectedTrashCount = 0;

    private Array<String> trashPaths;

    private Array<ModelInstance> instancesReference;

    public TrashPooler(btDynamicsWorld dynamicsWorld, Vector3 spawnCenter, Array<ModelInstance> instancesReference) {
        this.dynamicsWorld = dynamicsWorld;
        this.spawnCenter = spawnCenter;
        this.instancesReference = instancesReference;
        this.trashPool = new Array<Trash>();

        trashPaths = new Array<String>();
        trashPaths.add("model/barrel1/barrel1.g3db");
        trashPaths.add("model/trash1/trash1.g3db");

        while (this.trashPool.size < targetTrashCount) {
            this.spawnRandomTrash(instancesReference);
        }
    }

    public void update(float delta) {
        if (this.trashPool.size == 0) {
            this.currentLevel++;
            this.targetTrashCount = this.currentLevel * 10;
            // Spawn new batch
            while (this.trashPool.size < this.targetTrashCount) {
                this.spawnRandomTrash(this.instancesReference);
            }
        }

        for (Trash trash : this.trashPool) {
            trash.update(delta);
        }
    }

    public void spawnRandomTrash(Array<ModelInstance> instancesReference) {
        if (this.trashPool.size >= this.targetTrashCount)
            return;

        Vector3 position = this.getRandomSpawnPosition();
        eTrashType trashType = Trash.eTrashType.PLASTIC_BAG;
        Trash trash = new Trash("Trash", Trash.eTrashGroup.PACKED, trashType, position,
                Global.assets.get(this.trashPaths.get(new Random().nextInt(0, this.trashPaths.size)), Model.class));
        this.dynamicsWorld.addRigidBody(trash.rb);
        this.trashPool.add(trash);
        for (ModelInstance mi : trash.getInstances()) {
            instancesReference.add(mi);
        }
    }

    public void removeTrash(Trash trash, Array<ModelInstance> instancesReference) {
        if (trash == null)
            return;

        this.dynamicsWorld.removeRigidBody(trash.rb);
        this.trashPool.removeValue(trash, true);

        for (ModelInstance mi : trash.getInstances()) {
            instancesReference.removeValue(mi, true);
        }

        trash.dispose();
        this.collectedTrashCount++;
    }

    private Vector3 getRandomSpawnPosition() {
        float angle = MathUtils.random(0f, 360f);
        float distance = MathUtils.random(0f, spawnRadius);

        float x = spawnCenter.x + distance * MathUtils.cosDeg(angle);
        float z = spawnCenter.z + distance * MathUtils.sinDeg(angle);

        float y = 20f;

        return new Vector3(x, y, z);
    }

    public Trash getNearestTrash(Vector3 position) {
        Trash nearestTrash = null;
        float minDistance = Float.MAX_VALUE;

        for (Trash trash : this.trashPool) {
            if (trash.isCollectable()) {
                float distance = trash.getPosition().dst(position);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestTrash = trash;
                }
            }
        }

        return nearestTrash;
    }

    public void clear() {
        for (Trash trash : this.trashPool) {
            this.dynamicsWorld.removeRigidBody(trash.rb);
        }
        this.trashPool.clear();
    }

    public float getDistanceToNearestTrash(Vector3 position) {
        Trash nearestTrash = this.getNearestTrash(position);
        if (nearestTrash != null) {
            return nearestTrash.getPosition().dst(position);
        }
        return -1f;
    }

    public Array<Trash> getTrashPool() {
        return this.trashPool;
    }

    public int getTrashCount() {
        return this.trashPool.size;
    }

    public void setSpawnRadius(float radius) {
        this.spawnRadius = radius;
    }

    public void setSpawnCenter(Vector3 center) {
        this.spawnCenter = center;
    }

    public int getCollectedTrashCount() {
        return this.collectedTrashCount;
    }

    public void dispose() {
        this.clear();
    }
}
