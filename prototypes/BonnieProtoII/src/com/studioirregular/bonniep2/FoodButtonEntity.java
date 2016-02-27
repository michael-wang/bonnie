package com.studioirregular.bonniep2;

import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import com.studioirregular.bonniep2.FoodComponent.Type;
import com.studioirregular.bonniep2.FoodProductHolder.Size;

public class FoodButtonEntity extends BasicEntity {

	private static final boolean DO_LOG = false;
	private static final String TAG = "food-button-entity";
	
	public FoodButtonEntity(SceneBase scene, String id, Type type, FoodMachine host, RectF box) {
		super(scene, id);
		foodType = type;
		this.host = host;
		
		FoodComponent food = FoodComponent.newInstance(foodType);
		GLTexture texture = TextureSystem.getInstance().getPart(food.getTextureId(Size.Normal));
		RenderComponent render = new RenderComponent("render", box.left, box.top, box.width(), box.height(), texture);
		add(render);
		
		button = new ButtonComponent("button", this, box.left, box.top, box.width(), box.height());
		add(button);
	}
	
	@Override
	public void onEvent(Event event) {
		super.onEvent(event);
		
		if (event.what == ComponentEvent.BUTTON_UP) {
			host.requestCook(this);
		}
	}
	
	@Override
	public boolean onTouch(MotionEvent event) {
		boolean result = super.onTouch(event);
		if (DO_LOG) Log.d(TAG, "onTouch box:" + button.box + ",x:" + event.getX() + ",y:" + event.getY() + ", result:" + result);
		return result;
	}
	
	public Type getFoodType() {
		return foodType;
	}
	
	private Type foodType;
	private FoodMachine host;
	private ButtonComponent button;

}
