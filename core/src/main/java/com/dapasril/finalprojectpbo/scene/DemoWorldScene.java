package com.dapasril.finalprojectpbo.scene;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.utils.Array;
import com.dapasril.finalprojectpbo.Global;
import com.dapasril.finalprojectpbo.Main;
import com.dapasril.finalprojectpbo.camera.CThirdPersonCam;
import com.dapasril.finalprojectpbo.camera.CTowerCam;
import com.dapasril.finalprojectpbo.entity.trash.Trash;
import com.dapasril.finalprojectpbo.entity.trash.TrashPooler;
import com.dapasril.finalprojectpbo.entity.vehicle.Boat;
import com.dapasril.finalprojectpbo.entity.vehicle.Helicopter;
import com.dapasril.finalprojectpbo.physics.CContactListener;
import com.dapasril.finalprojectpbo.physics.CPhysics;
import com.dapasril.finalprojectpbo.recycling.Inventory;
import com.dapasril.finalprojectpbo.recycling.RecyclableItem;
import com.dapasril.finalprojectpbo.entity.vehicle.CVehicle;

import com.dapasril.finalprojectpbo.ui.HUDManager;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;

public class DemoWorldScene extends CScene3D implements EventListener {

	CThirdPersonCam tpsCam;
	CTowerCam towerCam;
	public boolean isGroundCrewMode = false;
	private String currentMode = "Heli";

	public Helicopter playerHeli;
	public Boat playerBoat;

	ModelInstance islandInstance, waterInstance, skyInstance,
			hq1Instance, hq2Instance, towerUNSInstance;

	HUDManager hudManager;

	btCollisionObject collisionObject_Water, collisionObject_Terrain,
			collisionObject_HQ1, collisionObject_HQ2, collisionObject_UNSTower;

	Array<MeshPart> terrainMeshParts = new Array<>();
	btBvhTriangleMeshShape terrainShape, hq1Shape, hq2Shape, unsTowerShape;
	private btBoxShape waterGroundShape;

	btDynamicsWorld dynamicsWorld;
	btConstraintSolver constraintSolver;
	btCollisionConfiguration collisionConfig;
	btDispatcher collisionDispatcher;
	btBroadphaseInterface broadphaseInterface;
	btCollisionWorld collisionWorld;

	private TrashPooler trashPooler;

	CContactListener contactListener;
	DebugDrawer debugDrawer;

	DirectionalShadowLight shadowLight;
	ModelBatch shadowBatch;

	// Drop Zone
	private BoundingBox dropZone;
	private ModelInstance dropZoneVisual;

	// Shop Zone
	private BoundingBox helipadZone;
	private ModelInstance helipadZoneVisual;

	private Music collectSound;
	private boolean trashInRangeNotificationShown = false;
	private boolean trashHeldNotificationShown = false;
	private float hintTimer = 0;
	private float hintInterval = 15f; // Initial interval
	private int currentLevelCollectedTrashCount = 0;

	public DemoWorldScene(Main game) {
		super(game);

		com.dapasril.finalprojectpbo.recycling.Inventory.getInstance().reset();
		com.dapasril.finalprojectpbo.managers.EconomyManager.getInstance().reset();

		this.playerHeli = new Helicopter();
		this.playerHeli.vehicleMode = CVehicle.eVehicleStatus.PLAYABLE;
		this.playerHeli.isRotorMoving = true;
		this.playerHeli.rootTransform.setToTranslation(new Vector3(100, -30, 137.5f));
		this.playerHeli.rb.setWorldTransform(this.playerHeli.rootTransform);
		this.instances.addAll(this.playerHeli.getInstances());

		this.playerBoat = new Boat();
		this.playerBoat.vehicleMode = CVehicle.eVehicleStatus.AI;
		this.playerBoat.rootTransform.setToTranslation(new Vector3(0, -44.5f, 150));
		this.playerBoat.rb.setWorldTransform(this.playerBoat.rootTransform);
		this.instances.addAll(this.playerBoat.getInstances());

		this.tpsCam = new CThirdPersonCam(this.cam, this.playerHeli.rootTransform);
		this.tpsCam.distanceFromTarget = 15f;
		this.cam.fieldOfView = 90f;
		this.cam.far = 3000f;
		this.tpsCam.isMouseActive = false;

		environment.clear();
		shadowLight = new DirectionalShadowLight(2048, 2048, Global.WORLD_SIZE * 2, Global.WORLD_SIZE * 2, 1f,
				Global.WORLD_SIZE * 2);
		shadowLight.set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f);
		environment.add(shadowLight);
		environment.shadowMap = shadowLight;
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1f));
		shadowBatch = new ModelBatch(new DepthShaderProvider());

		Vector3 towerPos = new Vector3(87, -25, 138);
		this.towerCam = new CTowerCam(this.cam, towerPos);

		this.backgroundColor = new Color((float) (138 / 255), (float) (214 / 255), (float) (255 / 255), 1f);

		this.islandInstance = new ModelInstance(Global.assets.get("model/island2/island2.g3db", Model.class));
		this.waterInstance = new ModelInstance(Global.assets.get("model/water2/water2.g3db", Model.class));
		this.skyInstance = new ModelInstance(Global.assets.get("model/sky1/sky1.g3db", Model.class));
		this.hq1Instance = new ModelInstance(Global.assets.get("model/hq/hq1.g3db", Model.class));
		this.hq2Instance = new ModelInstance(Global.assets.get("model/hq/hq2.g3db", Model.class));
		this.towerUNSInstance = new ModelInstance(Global.assets.get("model/tower1/tower1.g3db", Model.class));

		this.islandInstance.transform.setToTranslation(new Vector3(0, -38, 0));
		this.waterInstance.transform.setToTranslation(new Vector3(0, -38, 0));
		this.hq1Instance.transform.setToTranslation(new Vector3(100, -38, 140));
		this.hq2Instance.transform.setToTranslation(new Vector3(160, -38, 150));
		this.towerUNSInstance.transform.setToTranslation(new Vector3(-150, -38, 130));

		this.collisionObject_Water = new btCollisionObject();
		this.waterGroundShape = new btBoxShape(new Vector3(1000, 1, 1000));
		this.collisionObject_Water.setCollisionShape(this.waterGroundShape);
		this.collisionObject_Water.setWorldTransform(this.waterInstance.transform);
		this.collisionObject_Water.setFriction(0.8f);
		this.collisionObject_Water.setRestitution(0.2f);
		Matrix4 waterPlace = new Matrix4();
		waterPlace.setToTranslation(new Vector3(0, -46.5f, 0));
		this.collisionObject_Water.setWorldTransform(waterPlace);

		// Terrain Setup
		this.terrainShape = (btBvhTriangleMeshShape) Bullet.obtainStaticNodeShape(this.islandInstance.nodes);
		btRigidBody.btRigidBodyConstructionInfo terrainInfo = new btRigidBody.btRigidBodyConstructionInfo(0f, null,
				this.terrainShape, Vector3.Zero);

		this.collisionObject_Terrain = new btRigidBody(terrainInfo);
		this.collisionObject_Terrain.setWorldTransform(this.islandInstance.transform);
		this.collisionObject_Terrain.setFriction(0.8f);
		this.collisionObject_Terrain.setRestitution(0.2f);
		terrainInfo.dispose();

		// Sky Setup
		this.skyInstance.transform.setToScaling(new Vector3(2000, 2000, 2000));
		this.skyInstance.transform.rotate(new Vector3(1f, 0, 0), 180f);
		Material skyMaterial = this.skyInstance.materials.get(0);
		TextureAttribute diffuseTexture = (TextureAttribute) skyMaterial.get(TextureAttribute.Diffuse);
		if (diffuseTexture != null) {
			skyMaterial.set(new TextureAttribute(
					TextureAttribute.Emissive,
					diffuseTexture.textureDescription.texture));
		}
		skyMaterial.set(new ColorAttribute(ColorAttribute.Emissive, Color.WHITE));
		skyMaterial.set(new ColorAttribute(ColorAttribute.Diffuse, Color.BLACK));
		skyMaterial.remove(ColorAttribute.Specular);

		// HQ1 Setup
		this.hq1Shape = (btBvhTriangleMeshShape) Bullet.obtainStaticNodeShape(this.hq1Instance.nodes);
		btRigidBody.btRigidBodyConstructionInfo hq1RbInfo = new btRigidBody.btRigidBodyConstructionInfo(0f, null,
				this.hq1Shape, Vector3.Zero);

		this.collisionObject_HQ1 = new btRigidBody(hq1RbInfo);
		this.collisionObject_HQ1.setWorldTransform(this.hq1Instance.transform);
		this.collisionObject_HQ1.setFriction(0.8f);
		this.collisionObject_HQ1.setRestitution(0.2f);
		hq1RbInfo.dispose();

		// HQ2 Setup
		this.hq2Shape = (btBvhTriangleMeshShape) Bullet.obtainStaticNodeShape(this.hq2Instance.nodes);
		btRigidBody.btRigidBodyConstructionInfo hq2RbInfo = new btRigidBody.btRigidBodyConstructionInfo(0f, null,
				this.hq2Shape, Vector3.Zero);

		this.collisionObject_HQ2 = new btRigidBody(hq2RbInfo);
		this.collisionObject_HQ2.setWorldTransform(this.hq2Instance.transform);
		this.collisionObject_HQ2.setFriction(0.8f);
		this.collisionObject_HQ2.setRestitution(0.2f);
		hq2RbInfo.dispose();

		// UNS Tower Setup
		this.unsTowerShape = (btBvhTriangleMeshShape) Bullet.obtainStaticNodeShape(this.towerUNSInstance.nodes);
		btRigidBody.btRigidBodyConstructionInfo towerUnsInfo = new btRigidBody.btRigidBodyConstructionInfo(0f, null,
				this.unsTowerShape, Vector3.Zero);
		this.collisionObject_UNSTower = new btRigidBody(towerUnsInfo);
		this.collisionObject_UNSTower.setWorldTransform(this.towerUNSInstance.transform);
		this.collisionObject_UNSTower.setFriction(0.8f);
		this.collisionObject_UNSTower.setRestitution(0.2f);
		towerUnsInfo.dispose();

		this.instances.add(islandInstance, waterInstance, skyInstance);
		this.instances.add(hq1Instance, hq2Instance, towerUNSInstance);

		// Rigidbody Setup
		this.collisionConfig = new btDefaultCollisionConfiguration();
		this.collisionDispatcher = new btCollisionDispatcher(this.collisionConfig);
		this.broadphaseInterface = new btDbvtBroadphase();
		this.constraintSolver = new btSequentialImpulseConstraintSolver();
		this.dynamicsWorld = new btDiscreteDynamicsWorld(this.collisionDispatcher, this.broadphaseInterface,
				this.constraintSolver, this.collisionConfig);
		this.dynamicsWorld.setGravity(new Vector3(0, -9.8f, 0));
		this.contactListener = new CContactListener();

		// Adding physics objects into dynamicsWorld
		this.dynamicsWorld.addRigidBody(this.playerHeli.rb, CPhysics.OBJECT_FLAG, CPhysics.ALL_FLAG);
		this.dynamicsWorld.addRigidBody(this.playerBoat.rb, CPhysics.OBJECT_FLAG, CPhysics.ALL_FLAG);
		this.dynamicsWorld.addCollisionObject(this.collisionObject_Terrain);
		this.dynamicsWorld.addCollisionObject(this.collisionObject_Water);
		this.dynamicsWorld.addCollisionObject(this.collisionObject_HQ1);
		this.dynamicsWorld.addCollisionObject(this.collisionObject_HQ2);
		this.dynamicsWorld.addCollisionObject(this.collisionObject_UNSTower);

		this.trashPooler = new TrashPooler(this.dynamicsWorld, new Vector3(0, -30, 0), this.instances);

		this.debugDrawer = new DebugDrawer();
		this.debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_DrawWireframe);
		this.dynamicsWorld.setDebugDrawer(this.debugDrawer);

		this.hudManager = new HUDManager(this, this.stage);

		// Initialize Drop Zone
		Vector3 zoneMin = new Vector3(123, -43, 140);
		Vector3 zoneMax = new Vector3(143, -33, 160);
		this.dropZone = new BoundingBox(zoneMin, zoneMax);

		// Create visual for drop zone
		ModelBuilder modelBuilder = new ModelBuilder();
		Model zoneModel = modelBuilder.createCylinder(10, 20, 10, 32,
				new Material(ColorAttribute.createDiffuse(Color.YELLOW),
						new com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute(0.3f)),
				VertexAttributes.Usage.Position
						| VertexAttributes.Usage.Normal);
		this.dropZoneVisual = new ModelInstance(zoneModel);
		this.dropZoneVisual.transform.setToTranslation(133, -38, 150);
		this.instances.add(this.dropZoneVisual);

		// Shop Zone
		Vector3 shopZoneMin = new Vector3(95, -32, 134);
		Vector3 shopZoneMax = new Vector3(105, -22, 141);
		this.helipadZone = new BoundingBox(shopZoneMin, shopZoneMax);

		Model shopZoneModel = modelBuilder.createCylinder(3, 3, 3, 16,
				new Material(ColorAttribute.createDiffuse(Color.WHITE),
						new com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute(0.3f)),
				VertexAttributes.Usage.Position
						| VertexAttributes.Usage.Normal);
		this.helipadZoneVisual = new ModelInstance(shopZoneModel);
		this.helipadZoneVisual.transform.setToTranslation(100, -30.5f, 137.5f);
		this.instances.add(this.helipadZoneVisual);

		this.collectSound = Global.assets.get("audio/collect1.mp3", Music.class);
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(this.backgroundColor.r, this.backgroundColor.g, this.backgroundColor.b,
				this.backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		if (!isPaused) {
			this.update3D(delta);
		}

		shadowLight.begin(Vector3.Zero, cam.direction);
		shadowBatch.begin(shadowLight.getCamera());
		shadowBatch.render(instances);
		shadowBatch.end();
		shadowLight.end();

		game.modelBatch.begin(cam);
		game.modelBatch.render(instances, environment);
		game.modelBatch.end();

		if (Global.debugMode && this.dynamicsWorld != null) {
			this.debugDrawer.begin(this.cam);
			this.dynamicsWorld.debugDrawWorld();
			this.debugDrawer.end();
		}

		this.cam.update();
		this.cam.up.set(Vector3.Y);

		this.game.spriteBatch.begin();
		this.update2D(delta);
		this.game.spriteBatch.end();

		this.stage.act(delta);
		this.stage.draw();
	}

	@Override
	protected void update3D(float delta) {
		// DEBUG!!!
		if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
			Global.debugMode = !Global.debugMode;
		}

		// Engine toggle on/off
		if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
			if (this.playerHeli.vehicleMode == CVehicle.eVehicleStatus.PLAYABLE) {
				this.playerHeli.toggleEngine();
				this.hudManager.showNotification(this.playerHeli.isEngineOn ? "Engine ON" : "Engine OFF");
			} else if (this.playerBoat.vehicleMode == CVehicle.eVehicleStatus.PLAYABLE) {
				this.playerBoat.toggleEngine();
				this.hudManager.showNotification(this.playerBoat.isEngineOn ? "Engine ON" : "Engine OFF");
			}
		}

		// Step the physics world
		this.dynamicsWorld.stepSimulation(delta, 5, 1f / 60f);

		// Character Switching UI
		if (this.hudManager.altTable.isVisible()) {
			String selected = this.hudManager.getSelectedCharacter();
			if (!selected.equals(currentMode)) {
				// Trigger Transitions
				if (selected.equals("Base")) {
					this.towerCam.startTransition(this.cam.position);
				} else if (currentMode.equals("Base")) {
					// Switching FROM Base TO Vehicle
					this.tpsCam.startTransition(this.cam.position);
				}
				currentMode = selected;
			}
		} else {
			// Sync UI with current mode to prevent bug/reset
			this.hudManager.setSelectedCharacter(currentMode);
		}

		// Camera Switching
		if (currentMode.equals("Heli")) {
			this.tpsCam.targetTransform = this.playerHeli.rootTransform;

			this.playerHeli.vehicleMode = CVehicle.eVehicleStatus.PLAYABLE;
			this.playerBoat.vehicleMode = CVehicle.eVehicleStatus.NPC;
			this.isGroundCrewMode = false;
		} else if (currentMode.equals("Boat")) {
			this.tpsCam.targetTransform = this.playerBoat.rootTransform;

			this.playerHeli.vehicleMode = CVehicle.eVehicleStatus.NPC;
			this.playerBoat.vehicleMode = CVehicle.eVehicleStatus.PLAYABLE;
			this.isGroundCrewMode = false;
		} else if (currentMode.equals("Base")) {
			this.playerHeli.vehicleMode = CVehicle.eVehicleStatus.NPC;
			this.playerBoat.vehicleMode = CVehicle.eVehicleStatus.NPC;
			this.isGroundCrewMode = true;
		}

		// Camera Update
		if (isGroundCrewMode) {
			this.towerCam.update(delta);
		} else {
			// Cam FoV reset
			this.cam.fieldOfView = 90f;
			this.tpsCam.updateLerp();
			this.tpsCam.updatePivot();
		}

		// Update the Trash Pooler
		this.trashPooler.update(delta);

		// Update the helicopter
		this.playerHeli.update();
		this.playerHeli.updateSound(this.cam.position, this.cam.direction, this.cam.up);
		this.playerBoat.update();

		// Notifications
		this.hintTimer += delta;
		// Every 8 seconds
		if (this.hintTimer >= 8f) {
			this.hintTimer = 0;

			// Adding list of tips to show in notifications
			Array<String> genericTips = new Array<>();
			genericTips.add("Press Alt to switch characters.");
			if (isPlayerInShopZone()) {
				genericTips.add("Press B to open Shop.");
			} else {
				genericTips.add("Go to the Helipad to access Shop.");
			}
			genericTips.add("Recycle trashes you collect to earn money!");
			genericTips.add("Keep your eye on the fuel. You don't want the game to be over right?");
			genericTips.add("Visit https://recycopter.madebydap.my.id for more info about updates and events!");
			genericTips.add(String.format("You're on level %d.", this.trashPooler.currentLevel));
			genericTips.add("Press M to turn engine on/off.");
			genericTips.add("Press Esc to pause the game.");
			genericTips.add(String.format("So far you've collected %d/%d trashes!",
					this.currentLevelCollectedTrashCount,
					this.trashPooler.targetTrashCount));
			genericTips.add(
					"Press W or S for the thrust control.\nA or D for roll control\nQ or E for yaw control.\nLEFT or RIGHT for pitch control.");

			// Contextual Tips
			Array<String> contextualTips = new Array<>();
			if (this.playerHeli.vehicleMode == CVehicle.eVehicleStatus.PLAYABLE) {
				if (this.playerHeli.getGrabbedTrash() != null) {
					contextualTips.add("Press F to throw the trash");
					contextualTips.add("Bring the trash into the drop zone");
				}
			} else if (this.playerBoat.vehicleMode == CVehicle.eVehicleStatus.PLAYABLE) {
				if (this.playerBoat.getGrabbedTrash() != null) {
					contextualTips.add("Press F to throw the trash");
				}
			} else if (this.isGroundCrewMode) {
				contextualTips.add("Press I to open Inventory");
			}

			if (contextualTips.size > 0) {
				String tip = contextualTips.get(com.badlogic.gdx.math.MathUtils.random(0, contextualTips.size - 1));
				this.hudManager.showNotification(tip);
			} else if (genericTips.size > 0) {
				String tip = genericTips.get(com.badlogic.gdx.math.MathUtils.random(0, genericTips.size - 1));
				this.hudManager.showNotification(tip);
			}
		}

		// Check if there is any trash in the drop zone
		this.checkTrashInDropZone();

		// Game over logic
		if ((this.playerHeli.currentFuel <= 0 || this.playerBoat.currentFuel <= 0) && !this.isPaused()) {
			this.playerHeli.isEngineOn = false;
			this.playerBoat.isEngineOn = false;

			String reason = "";
			if (this.playerHeli.currentFuel <= 0 && this.playerBoat.currentFuel <= 0) {
				reason = "Both vehicles ran out of fuel.";
			} else if (this.playerHeli.currentFuel <= 0) {
				reason = "Helicopter ran out of fuel.";
			} else if (this.playerBoat.currentFuel <= 0) {
				reason = "Boat ran out of fuel.";
			}

			this.hudManager.showGameOver(reason);
		}

		// Notification to pick up trash when get near (HELI)
		if (this.playerHeli.vehicleMode == CVehicle.eVehicleStatus.PLAYABLE) {
			// Immediate Proximity Notification
			Trash nearestTrash = this.trashPooler.getNearestTrash(this.playerHeli.getPosition());
			if (nearestTrash != null) {
				float distance = nearestTrash.getPosition().dst(this.playerHeli.getPosition());
				if (distance <= this.playerHeli.getPickupDistance() && this.playerHeli.getGrabbedTrash() == null) {
					if (!this.trashInRangeNotificationShown) {
						this.hudManager.showNotification("Press F to collect trash");
						this.trashInRangeNotificationShown = true;
					}
				} else {
					this.trashInRangeNotificationShown = false;
				}
			} else {
				this.trashInRangeNotificationShown = false;
			}

			// Trash picking (F to pick/drop)
			if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
				if (this.playerHeli.getGrabbedTrash() == null) {
					if (nearestTrash != null) {
						float distance = nearestTrash.getPosition().dst(this.playerHeli.getPosition());
						if (distance <= this.playerHeli.getPickupDistance()) {
							this.playerHeli.pickupTrash(nearestTrash);
						}
					}
				} else {
					this.playerHeli.dropTrash();
				}
			}
		}

		// Notification to pick up trash when get near (BOAT)
		if (this.playerBoat.vehicleMode == CVehicle.eVehicleStatus.PLAYABLE) {
			// Immediate Proximity Notification
			Trash nearestTrash = this.trashPooler.getNearestTrash(this.playerBoat.getPosition());
			if (nearestTrash != null) {
				float distance = nearestTrash.getPosition().dst(this.playerBoat.getPosition());
				if (distance <= this.playerBoat.getPickupDistance() && this.playerBoat.getGrabbedTrash() == null) {
					if (!this.trashInRangeNotificationShown) {
						this.hudManager.showNotification("Press F to collect trash");
						this.trashInRangeNotificationShown = true;
					}
				} else {
					this.trashInRangeNotificationShown = false;
				}
			} else {
				this.trashInRangeNotificationShown = false;
			}

			// Trash picking (F to pick/drop)
			if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
				if (this.playerBoat.getGrabbedTrash() == null) {
					if (nearestTrash != null) {
						float distance = nearestTrash.getPosition().dst(this.playerBoat.getPosition());
						if (distance <= this.playerBoat.getPickupDistance()) {
							this.playerBoat.pickupTrash(nearestTrash);
						}
					}
				} else {
					this.playerBoat.dropTrash();
				}
			}
		}

		// Don't show notification about trash if no trash is held
		if (this.playerHeli.getGrabbedTrash() == null && this.playerBoat.getGrabbedTrash() == null) {
			this.trashHeldNotificationShown = false;
		}

		// Logic for the world bounds
		float WORLD_SIZE = Global.WORLD_SIZE;

		Vector3 heliPos = new Vector3();
		this.playerHeli.rootTransform.getTranslation(heliPos);
		boolean heliTeleported = false;

		if (heliPos.x > WORLD_SIZE) {
			this.playerHeli.rootTransform.setTranslation(-WORLD_SIZE, heliPos.y, heliPos.z);
			heliTeleported = true;
		} else if (heliPos.x < -WORLD_SIZE) {
			this.playerHeli.rootTransform.setTranslation(WORLD_SIZE, heliPos.y, heliPos.z);
			heliTeleported = true;
		}

		this.playerHeli.rootTransform.getTranslation(heliPos);

		if (heliPos.z > WORLD_SIZE) {
			this.playerHeli.rootTransform.setTranslation(heliPos.x, heliPos.y, -WORLD_SIZE);
			heliTeleported = true;
		} else if (heliPos.z < -WORLD_SIZE) {
			this.playerHeli.rootTransform.setTranslation(heliPos.x, heliPos.y, WORLD_SIZE);
			heliTeleported = true;
		}

		if (heliTeleported) {
			this.playerHeli.rb.setWorldTransform(this.playerHeli.rootTransform);
		}

		Vector3 boatPos = new Vector3();
		this.playerBoat.rootTransform.getTranslation(boatPos);
		boolean boatTeleported = false;

		if (boatPos.x > WORLD_SIZE) {
			this.playerBoat.rootTransform.setTranslation(-WORLD_SIZE, boatPos.y, boatPos.z);
			boatTeleported = true;
		} else if (boatPos.x < -WORLD_SIZE) {
			this.playerBoat.rootTransform.setTranslation(WORLD_SIZE, boatPos.y, boatPos.z);
			boatTeleported = true;
		}

		this.playerBoat.rootTransform.getTranslation(boatPos);

		if (boatPos.z > WORLD_SIZE) {
			this.playerBoat.rootTransform.setTranslation(boatPos.x, boatPos.y, -WORLD_SIZE);
			boatTeleported = true;
		} else if (boatPos.z < -WORLD_SIZE) {
			this.playerBoat.rootTransform.setTranslation(boatPos.x, boatPos.y, WORLD_SIZE);
			boatTeleported = true;
		}

		if (boatTeleported) {
			this.playerBoat.rb.setWorldTransform(this.playerBoat.rootTransform);
		}

		// Update the heli rigidbody
		this.playerHeli.rb.getWorldTransform(this.playerHeli.rootTransform);

		// Update the boat rigidbody
		this.playerBoat.rb.getWorldTransform(this.playerBoat.rootTransform);

		// Update the camera
		this.tpsCam.updateLerp();
		this.tpsCam.updatePivot();
	}

	@Override
	public void update2D(float delta) {
		this.hudManager.update(delta);
	}

	@Override
	public boolean handle(Event event) {
		return false;
	}

	@Override
	public void dispose() {
		super.dispose();

		if (this.playerHeli != null)
			this.playerHeli.dispose();
		if (this.playerBoat != null)
			this.playerBoat.dispose();

		if (this.trashPooler != null) {
			this.trashPooler.dispose();
		}

		if (this.collisionObject_Water != null)
			this.collisionObject_Water.dispose();
		if (this.waterGroundShape != null)
			this.waterGroundShape.dispose();

		if (this.collisionObject_Terrain != null)
			this.collisionObject_Terrain.dispose();
		if (this.terrainShape != null)
			this.terrainShape.dispose();

		if (this.dynamicsWorld != null)
			this.dynamicsWorld.dispose();
		if (this.constraintSolver != null)
			this.constraintSolver.dispose();
		if (this.broadphaseInterface != null)
			this.broadphaseInterface.dispose();
		if (this.collisionDispatcher != null)
			this.collisionDispatcher.dispose();
		if (this.collisionConfig != null)
			this.collisionConfig.dispose();

		if (this.contactListener != null)
			this.contactListener.dispose();

		if (this.debugDrawer != null)
			this.debugDrawer.dispose();

		if (this.hudManager != null)
			this.hudManager.dispose();

		if (this.shadowLight != null)
			this.shadowLight.dispose();
		if (this.shadowBatch != null)
			this.shadowBatch.dispose();

	}

	// Check trash drop zone
	private void checkTrashInDropZone() {
		for (Trash trash : this.trashPooler.getTrashPool()) {
			if (trash.getState() == Trash.eTrashState.IDLE) {
				Vector3 pos = trash.getPosition();
				if (this.dropZone.contains(pos)) {
					// Collect trash
					for (Map.Entry<RecyclableItem, Integer> entry : trash.getContents().entrySet()) {
						Inventory.getInstance().addItem(entry.getKey(),
								entry.getValue());
					}
					this.trashPooler.removeTrash(trash, this.instances);
					this.collectSound.play();
					this.currentLevelCollectedTrashCount++;
					if (this.currentLevelCollectedTrashCount >= this.trashPooler.targetTrashCount) {
						this.hudManager
								.showNotification(String.format(
										"Congratulations you've collected level %d trashes!\nLevel is now upgraded!",
										this.trashPooler.currentLevel));
					} else {
						this.hudManager.showNotification("Trash collected and Items added to inventory!\n"
								+ String.format("So far you've collected %d/%d trashes!",
										this.currentLevelCollectedTrashCount,
										this.trashPooler.targetTrashCount));
					}
					if (this.trashPooler.getTrashPool().size <= 0) {
						this.currentLevelCollectedTrashCount = 0;
					}
					break;
				}
			}
		}
	}

	public boolean isPlayerInShopZone() {
		if (this.playerHeli != null && this.helipadZone != null) {
			return this.helipadZone.contains(this.playerHeli.getPosition());
		}
		return false;
	}

	public String getCurrentMode() {
		return this.currentMode;
	}

	public HUDManager getHudManager() {
		return this.hudManager;
	}

	public TrashPooler getTrashPooler() {
		return this.trashPooler;
	}

	@Override
	public void setPaused(boolean paused) {
		super.setPaused(paused);
		if (this.playerHeli != null) {
			if (paused) {
				this.playerHeli.stopSound();
			} else {
				this.playerHeli.resumeSound();
			}
		}
	}

	public int getCurrentLevelCollectedTrashCount() {
		return this.currentLevelCollectedTrashCount;
	}
}
