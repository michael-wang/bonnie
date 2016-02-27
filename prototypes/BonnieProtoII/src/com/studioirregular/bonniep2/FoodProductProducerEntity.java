package com.studioirregular.bonniep2;

import android.util.Log;

import com.studioirregular.bonniep2.FoodComponent.Type;

public class FoodProductProducerEntity extends BasicEntity {

	private static final boolean DO_LOG = false;
	private static final String TAG = "food-product-producer-entity";
	
	protected Type type;
	private Animation scaleAnimation;
	
	public FoodProductProducerEntity(SceneBase scene, String id, Type type) {
		super(scene, id);
		
		this.type = type;
		
		// add scaling animation
		ScalingAnimationComponent animation = new ScalingAnimationComponent(this, "scale-animation", 1.0f, 1.0f, 1.3f, 1.3f, 250, false);
		add(animation);
		scaleAnimation = animation;
	}
	
	@Override
	public void onEvent(Event event) {
		super.onEvent(event);
		if (DO_LOG) Log.d(TAG, "onEvent event:" + event);
		if (event.what == ComponentEvent.BUTTON_UP) {
			if (scaleAnimation != null) {
				scaleAnimation.start();
			}
			
			FoodComponent food = FoodComponent.newInstance(type);
			scene.send(this, new EntityEvent(EntityEvent.ADD_FOOD_REQUEST, id, food));
		}
	}

}
