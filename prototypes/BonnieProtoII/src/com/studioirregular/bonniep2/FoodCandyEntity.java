package com.studioirregular.bonniep2;

import android.graphics.RectF;

public class FoodCandyEntity extends FoodProductEntity {

//	private static final String TAG = "food-candy-entity";
	
	public FoodCandyEntity(SceneBase scene, String id, FoodProductHolder holder, RectF touchableBox) {
		super(scene, id, holder, scene);
		
//		Log.d(TAG, "FoodCandyEntity id:" + id + ", dragger:" + dragger + ", touchableBox:" + touchableBox);
		if (dragger != null) {
			dragger.addTouchableArea(touchableBox);
		}
	}
	
	@Override
	protected void onAddingFoodComponent(FoodComponent food) {
		if (foodComponents.isEmpty() == false) {
			return;	// CandyEntity take only one food component.
		}
		
		foodComponents.add(food);
		addComponentInternal(genRenderComponent(food));
	}
	
	@Override
	public void onEvent(Event event) {
		super.onEvent(event);
		
		if (event.what == ComponentEvent.DRAGGABLE_DROPT) {
			scene.send(this, new EntityEvent(EntityEvent.FOOD_PRODUCT_DROPT, id, this));
		}
	}

}
