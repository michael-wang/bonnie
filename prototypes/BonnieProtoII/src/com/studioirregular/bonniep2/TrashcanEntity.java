package com.studioirregular.bonniep2;


public class TrashcanEntity extends BasicEntity implements FoodProductConsumer {

	public TrashcanEntity(SceneBase scene, String id) {
		super(scene, id);
		
		setupRenderComponent();
	}
	
	@Override
	public boolean doYouWantToConsumeIt(FoodProductEntity food) {
		return true;	// I eat all kinds of foods!
	}

	@Override
	public void consumeIt(FoodProductEntity food) {
		state = OPENED;
		startOpenTime = System.currentTimeMillis();
		
		updateRenderState();
		
		getScene().removeEntity(food.getId());
	}
	
	@Override
	public void update(long timeDiff) {
		super.update(timeDiff);
		
		if (state == OPENED) {
			final long elapsed = System.currentTimeMillis() - startOpenTime;
			if (elapsed >= OPEN_DURATION) {
				state = CLOSED;
				updateRenderState();
			}
		}
	}
	
	
	private static final int CLOSED = 1;
	private static final int OPENED = 2;
	
	private int state;
	
	private void setupRenderComponent() {
		GLTexture texture = TextureSystem.getInstance().getPart("game_table_trashcan");
		StatfulRenderComponent render = new StatfulRenderComponent("render", 0, 366, 81, 114, CLOSED, texture);
		
		texture = TextureSystem.getInstance().getPart("game_table_trashcan_open");
		render.addState(OPENED, texture);
		
		addComponentInternal(render);
	}
	
	private static final long OPEN_DURATION = 1000;	// ms
	private long startOpenTime;
	
	private void updateRenderState() {
		if (getRenderableCount() == 0) {
			return;
		}
		
		GLRenderable render = getRenderable(0);
		if (render instanceof StatfulRenderComponent) {
			((StatfulRenderComponent)render).setState(state);
		}
	}

}
