package com.studioirregular.bonniep2;

import android.graphics.PointF;
import android.util.Log;

import com.studioirregular.bonniep2.FoodComponent.Category;

public class FoodPlateEntity extends BasicEntity implements FoodProductHolder {

	private static final boolean DO_LOG = false;
	private static final String TAG = "food-plate-entity";
	
	private FoodProductEntity foodProduct;
	
	public FoodPlateEntity(SceneBase scene, String id) {
		super(scene, id);
		
		GLTexture texture = TextureSystem.getInstance().getPart("game_table_plate_b");
		RenderComponent render = new RenderComponent("render", 345, 383, 120, 104, texture);
		add(render);
	}
	
	@Override
	public void onEvent(Event event) {
		super.onEvent(event);
		if (DO_LOG) Log.d(TAG, "onEvent event:" + event);
		
		if (event.what == EntityEvent.ADD_FOOD_REQUEST) {
			onFoodComponentArrival((FoodComponent)((EntityEvent)event).obj);
		} else if (event.what == EntityEvent.FOOD_PRODUCT_CONSUMED) {
			onFoodConsumed();
		}
	}

	@Override
	public Size getHolderSizeType() {
		return Size.Normal;
	}

	@Override
	public void getFoodLocation(PointF loc) {
		loc.set(360, 383);
	}
	
	@Override
	public void getBeverageLocation(PointF topLeft) {
		topLeft.set(432, 351);
	}
	
	@Override
	public void getToastTopLocation(PointF loc) {
		loc.set(465, 393);
	}
	
	@Override
	public void getCandyLocation(FoodComponent candy, PointF topLeft) {
	}
	
	@Override
	public boolean isDraggable() {
		return true;
	}
	
	protected void onFoodComponentArrival(FoodComponent food) {
		if (DO_LOG) Log.d(TAG, "onFoodArrival food:" + food);
		
		if (food.getCategory() == Category.Toast) {
			((FoodToast)food).open();
		}
		
		if (foodProduct == null) {
			FoodProductEntity product = new FoodProductEntity(scene, "food", this, scene);
			scene.addEntity(product);
			product.add(food);
			
			foodProduct = product;
			
			scene.send(this, new EntityEvent(EntityEvent.ADD_FOOD_ACCEPTED, id, food));
			scene.send(this, new EntityEvent(EntityEvent.TUTORIAL_FOOD_ADDED, id, food.getType()));
		} else {
			if (foodProduct.acceptFood(food)) {
				foodProduct.add(food);
				
				scene.send(this, new EntityEvent(EntityEvent.ADD_FOOD_ACCEPTED, id, food));
			} else {
				// play food rejected sound
			}
		}
	}
	
	protected void onFoodConsumed() {
		foodProduct = null;
	}
	
}
