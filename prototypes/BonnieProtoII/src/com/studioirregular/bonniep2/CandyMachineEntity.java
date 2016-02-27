package com.studioirregular.bonniep2;

import com.studioirregular.bonniep2.FoodComponent.Type;

import android.graphics.PointF;
import android.util.Log;

public class CandyMachineEntity extends BasicEntity implements FoodProductHolder {

	private static final boolean DO_LOG = false;
	private static final String TAG = "candy-machine-entity";
	
	public CandyMachineEntity(SceneBase scene, String id, int level) {
		super(scene, id);
		if (DO_LOG) Log.d(TAG, "CandyMachineEntity id:" + id + ",level:" + level);
		
		this.level = level;
		
		renderOff = new RenderComponent("render-off", TABLE_ITEM_LEFT,
				TABLE_ITEM_TOP, TABLE_ITEM_WIDTH, TABLE_ITEM_HEIGHT,
				getStaticTexture());
		add(renderOff);
		
		renderCooking = new FrameAnimationComponent("render-cooking",
				TABLE_ITEM_LEFT, TABLE_ITEM_TOP, TABLE_ITEM_WIDTH,
				TABLE_ITEM_HEIGHT, true, this);
		renderCooking.addFrame(getCookingTexture(0), 500);
		renderCooking.addFrame(getCookingTexture(1), 500);
		
		button = new ButtonComponent("button", this, TABLE_ITEM_LEFT, TABLE_ITEM_TOP, TABLE_ITEM_WIDTH, TABLE_ITEM_HEIGHT);
		add(button);
	}
	
	@Override
	public void onEvent(Event event) {
		super.onEvent(event);
		
		if (event.what == ComponentEvent.BUTTON_UP) {
			requestCook();
		} else if (event.what == EntityEvent.FOOD_PRODUCT_CONSUMED) {
			if (((EntityEvent)event).obj instanceof FoodCandyEntity) {
				candyCount--;
			}
		}
	}
	
	@Override
	public void update(long timeDiff) {
		super.update(timeDiff);
		
		if (cooking) {
			final long elapsed = System.currentTimeMillis() - startCookTime;
			if (elapsed >= COOK_DURATION) {
				onFoodReady();
			}
		}
	}
	
	@Override
	public Size getHolderSizeType() {
		return Size.Candy;
	}

	@Override
	public void getFoodLocation(PointF topLeft) {
	}
	
	@Override
	public void getToastTopLocation(PointF loc) {
	}
	
	@Override
	public void getBeverageLocation(PointF topLeft) {
	}
	
	@Override
	public void getCandyLocation(FoodComponent candy, PointF topLeft) {
		if (level == 1) {
			topLeft.set(CANDY_LV1_X0, CANDY_LV1_Y0);
		} else if (level == 2) {
			if (candy.getType() == Type.Candy1) {
				topLeft.set(CANDY_LV2_X0, CANDY_LV2_Y0);
			} else if (candy.getType() == Type.Candy2) {
				topLeft.set(CANDY_LV2_X1, CANDY_LV2_Y1);
			}
		} else if (level == 3) {
			if (candy.getType() == Type.Candy1) {
				topLeft.set(CANDY_LV3_X0, CANDY_LV3_Y0);
			} else if (candy.getType() == Type.Candy2) {
				topLeft.set(CANDY_LV3_X1, CANDY_LV3_Y1);
			} else if (candy.getType() == Type.Candy3) {
				topLeft.set(CANDY_LV3_X2, CANDY_LV3_Y2);
			}
		}
	}
	
	@Override
	public boolean isDraggable() {
		return true;
	}
	
	private GLTexture getStaticTexture() {
		if (level == 1) {
			return TextureSystem.getInstance().getPart("game_table_candy_lv1_normal");
		} else if (level == 2) {
			return TextureSystem.getInstance().getPart("game_table_candy_lv2_normal");
		} else if (level == 3) {
			return TextureSystem.getInstance().getPart("game_table_candy_lv3_normal");
		}
		return null;
	}
	
	private GLTexture getCookingTexture(int frame) {
		if (level == 1) {
			if (frame == 0) {
				return TextureSystem.getInstance().getPart("game_table_candy_lv1_work_1");
			} else if (frame == 1) {
				return TextureSystem.getInstance().getPart("game_table_candy_lv1_work_2");
			}
		} else if (level == 2) {
			if (frame == 0) {
				return TextureSystem.getInstance().getPart("game_table_candy_lv2_work_1");
			} else if (frame == 1) {
				return TextureSystem.getInstance().getPart("game_table_candy_lv2_work_2");
			}
		} else if (level == 3) {
			if (frame == 0) {
				return TextureSystem.getInstance().getPart("game_table_candy_lv3_work_1");
			} else if (frame == 1) {
				return TextureSystem.getInstance().getPart("game_table_candy_lv3_work_2");
			}
		}
		return null;
	}
	
	private boolean requestCook() {
		if (DO_LOG) Log.d(TAG, "requestCook");
		
		if (candyCount > 0) {
			return false;
		}
		
		remove(renderOff.getId());
		add(renderCooking);
		renderCooking.start();
		
		startCookTime = System.currentTimeMillis();
		cooking = true;
		
		return true;
	}
	
	private void onFoodReady() {
		if (DO_LOG) Log.d(TAG, "onFoodReady");
		
		add(renderOff);
		renderCooking.stop();
		remove(renderCooking.getId());
		
		candyCount = level;
		if (candyCount > 0) {
			genCandy(Type.Candy1);
		}
		if (candyCount > 1) {
			genCandy(Type.Candy2);
		}
		if (candyCount > 2) {
			genCandy(Type.Candy3);
		}
		
		cooking = false;
	}
	
	private void genCandy(Type type) {
		if (DO_LOG) Log.d(TAG, "genCandy type:" + type);
		FoodCandyEntity candy = new FoodCandyEntity(scene, "candy-" + type.toString(), this, getBoundingBox());
		candy.add(FoodComponent.newInstance(type));
		scene.addEntity(candy);
	}
	
	private static final float TABLE_ITEM_LEFT		= 0;
	private static final float TABLE_ITEM_TOP		= 230;
	private static final float TABLE_ITEM_WIDTH		= 95;
	private static final float TABLE_ITEM_HEIGHT	= 168;
	
	private static final float CANDY_LV1_X0 = 37;
	private static final float CANDY_LV1_Y0 = 363;
	
	private static final float CANDY_LV2_X0 = 25;
	private static final float CANDY_LV2_Y0 = 361;
	private static final float CANDY_LV2_X1 = 50;
	private static final float CANDY_LV2_Y1 = 361;
	
	private static final float CANDY_LV3_X0 = 15;
	private static final float CANDY_LV3_Y0 = 362;
	private static final float CANDY_LV3_X1 = 36;
	private static final float CANDY_LV3_Y1 = 356;
	private static final float CANDY_LV3_X2 = 58;
	private static final float CANDY_LV3_Y2 = 362;
	
	private static long COOK_DURATION = 5000L;
	
	private int level;
	private boolean cooking = false;
	private long startCookTime = Long.MAX_VALUE;
	private int candyCount = 0;
	
	private RenderComponent renderOff;
	private FrameAnimationComponent renderCooking;
	private ButtonComponent button;

}
