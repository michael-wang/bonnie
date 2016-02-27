package com.studioirregular.bonniep2;


public class AddTranslateAnimationCommand implements Command {

//	private static final String TAG = "add-translate-animation-command";
	
	private Scene scene;
	private String entityId;
	
	// Method I
	private String componentId;
	private float fromX, toX, fromY, toY;
	private long duration;
	private boolean loop;
	
	// Method II
	private TranslateAnimationComponent animation;
	
	private boolean startAfterAdded;
	
	public AddTranslateAnimationCommand(Scene scene, String entityId,
			String componentId, float fromX, float toX, float fromY, float toY,
			long duration, boolean loop, boolean startAfterAdded) {
		
		this(scene, entityId, null, startAfterAdded);
		
		this.componentId = componentId;
		this.fromX = fromX;
		this.toX = toX;
		this.fromY = fromY;
		this.toY = toY;
		this.duration = duration;
		this.loop = loop;
	}
	
	public AddTranslateAnimationCommand(Scene scene, String entityId,
			TranslateAnimationComponent component, boolean startAfterAdded) {
		this.scene = scene;
		this.entityId = entityId;
		this.animation = component;
		this.startAfterAdded = startAfterAdded;
	}
	
	@Override
	public void execute() {
		Entity entity = scene.getEntity(entityId);
		
		if (animation == null) {
			animation = new TranslateAnimationComponent(
					entity, (EventHost) entity, componentId, fromX, toX, fromY,
					toY, duration, loop);
		}
		entity.add(animation);
		
		if (startAfterAdded) {
			animation.start();
		}
	}

}
