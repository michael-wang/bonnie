package com.studioirregular.bonniep2;

import android.graphics.RectF;
import android.util.Log;

import com.studioirregular.bonniep2.FoodProductHolder.Size;

public class FoodMachineSlotEntity extends BasicEntity {

	private static final boolean DO_LOG = false;
	private static final String TAG = "food-machine-slot-entity";
	
	public FoodMachineSlotEntity(SceneBase scene, String id, FoodMachine host, RectF box, int slotIndex) {
		super(scene, id);
		
		this.slotIndex = slotIndex;
		this.host = host;
		this.box.set(box);
		
		GLTexture texture = TextureSystem.getInstance().getPart("popup_empty_bg");
		renderEmptyBg = new RenderComponent("render-bg-empty", box.left, box.top, box.width(), box.height(), texture);
		add(renderEmptyBg);
		
		texture = TextureSystem.getInstance().getPart("popup_full_bg");
		renderFullBg = new RenderComponent("render-bg-full", box.left, box.top, box.width(), box.height(), texture);
		
		add(new ButtonComponent("button", this, box.left, box.top, box.width(), box.height()));
		
		prepareCookAnimation();
	}
	
	public boolean isAvailable() {
		if (food == null) {
			return true;
		}
		return false;
	}
	
	public CookableFoodComponent getFood() {
		return food;
	}
	
	public void requestAdd(CookableFoodComponent food) {
		if (DO_LOG) Log.d(TAG, "requestAdd food:" + food);
		
		if (this.food != null) {
			return;
		}
		
		this.food = food;
		add(this.food);
		
		remove(renderEmptyBg.getId());
		add(renderFullBg);
		
		GLTexture texture = TextureSystem.getInstance().getPart(food.getTextureId(Size.Medium));
		renderFood = new RenderComponent("render-" + food.getType(), box.left, box.top, box.width(), box.height(), texture);
		add(renderFood);
		
		add(cookAnimation);
	}
	
	public void requestCook() {
		if (DO_LOG) Log.d(TAG, "requestCook food:" + food);
		
		if (food != null && food.doneCooking() == false) {
			food.startCook();
			cookAnimation.start();
			notifyFoodReady = false;
		}
	}
	
	private void onFoodConsumed() {
		if (DO_LOG) Log.d(TAG, "onFoodConsumed");
		
		remove(food.getId());
		food = null;
		
		add(renderEmptyBg);
		remove(renderFood.getId());
		remove(renderFullBg.getId());
		
		host.onSlotEmpty(this);
	}
	
	@Override
	public void update(long timeDiff) {
		super.update(timeDiff);
		
		if (food != null) {
			food.update(timeDiff, this);
			
			if (food.doneCooking() && !notifyFoodReady) {
				remove(cookAnimation.getId());
				
				host.onFoodReady(this);
				notifyFoodReady = true;
			}
		}
	}
	
	@Override
	public void onEvent(Event event) {
		super.onEvent(event);
		if (DO_LOG) Log.d(TAG, "onEvent event:" + event);
		
		if (event.what == ComponentEvent.BUTTON_UP) {
			if (food != null && food.isCooking() == false) {
				scene.send(this, new EntityEvent(EntityEvent.ADD_FOOD_REQUEST, id, food));
			}
		} else if (event.what == EntityEvent.ADD_FOOD_ACCEPTED) {
			EntityEvent ev = (EntityEvent)event;
			if (ev.obj instanceof CookableFoodComponent &&
					ev.obj == food) {
				onFoodConsumed();
				host.requestHideMachine(this);
			}
		}
	}
	
	private void prepareCookAnimation() {
		cookAnimation = new FrameAnimationComponent("cook-animation", box.left, box.top, box.width(), box.height(), false, this);
		
		// TODO: calculate curation according to food cook time
		final long FRAME_DURATION = 5000 / 12;
		cookAnimation.addFrame(TextureSystem.getInstance().getPart("store_mask_00"), FRAME_DURATION);
		cookAnimation.addFrame(TextureSystem.getInstance().getPart("store_mask_01"), FRAME_DURATION);
		cookAnimation.addFrame(TextureSystem.getInstance().getPart("store_mask_02"), FRAME_DURATION);
		cookAnimation.addFrame(TextureSystem.getInstance().getPart("store_mask_03"), FRAME_DURATION);
		cookAnimation.addFrame(TextureSystem.getInstance().getPart("store_mask_04"), FRAME_DURATION);
		cookAnimation.addFrame(TextureSystem.getInstance().getPart("store_mask_05"), FRAME_DURATION);
		cookAnimation.addFrame(TextureSystem.getInstance().getPart("store_mask_06"), FRAME_DURATION);
		cookAnimation.addFrame(TextureSystem.getInstance().getPart("store_mask_07"), FRAME_DURATION);
		cookAnimation.addFrame(TextureSystem.getInstance().getPart("store_mask_08"), FRAME_DURATION);
		cookAnimation.addFrame(TextureSystem.getInstance().getPart("store_mask_09"), FRAME_DURATION);
		cookAnimation.addFrame(TextureSystem.getInstance().getPart("store_mask_10"), FRAME_DURATION);
		cookAnimation.addFrame(TextureSystem.getInstance().getPart("store_mask_11"), FRAME_DURATION);
	}
	
	int slotIndex;
	private FoodMachine host;
	private CookableFoodComponent food = null;
	private boolean notifyFoodReady = false;
	
	private RectF box = new RectF();
	private RenderComponent renderEmptyBg;
	private RenderComponent renderFullBg;
	private RenderComponent renderFood;
	private FrameAnimationComponent cookAnimation;

}
