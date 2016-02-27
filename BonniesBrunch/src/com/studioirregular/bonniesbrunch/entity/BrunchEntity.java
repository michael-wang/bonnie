package com.studioirregular.bonniesbrunch.entity;

import java.util.Comparator;

import android.graphics.PointF;
import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.FoodSystem.Brunch;
import com.studioirregular.bonniesbrunch.FoodSystem.Food;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.component.Draggable;
import com.studioirregular.bonniesbrunch.component.FoodComponent;
import com.studioirregular.bonniesbrunch.component.Toast;
import com.studioirregular.bonniesbrunch.component.Toast.Type;

// TODO: allow turn on/off beverage trick (see beverage and someoneLetYourDrawFirst)
public class BrunchEntity extends GameEntity {

	private static final String TAG = "brunch";
	
	public BrunchEntity(int zOrder) {
		super(zOrder);
	}
	
	public void setup(BrunchHolder holder) {
		this.holder = holder;
		holder.getFoodLocation(loc);
		setup(loc.x, loc.y, 90, 90);
		
		Draggable dragger = new Draggable(MIN_GAME_COMPONENT_Z_ORDER + 0x10);
		dragger.setTouchArea(box, -16, -24, 8, 8);
		add(dragger);
	}
	
	public boolean addFood(int foodType) {
		if (Config.DEBUG_LOG) Log.d(TAG, "addFood foodType:" + Food.getFoodName(foodType));
		
		if (brunch == null) {
			brunch = new Brunch();
		}
		
		if (brunch.addFood(foodType) == false) {
			return false;
		}
		
		FoodComponent component = null;
		if (Food.isToast(foodType)) {
			component = new Toast(MIN_GAME_COMPONENT_Z_ORDER);
			((Toast)component).setup(foodType, holder.getHolderSizeType(), Toast.Type.Bottom);
			
			GameEventSystem.scheduleEvent(GameEvent.BRUNCH_NEED_TOAST_TOP, foodType);
		} else {
			component = new FoodComponent(MIN_GAME_COMPONENT_Z_ORDER);
			component.setup(foodType, holder.getHolderSizeType());
		}
		
		if (Food.isBeverage(foodType)) {
			beverage = component;
			// adjust beverage location here
			holder.getBeverageLocation(loc);
			beverage.setupOffset(loc.x - box.left, loc.y - box.top);
		} else {
			add(component);
		}
		
		adjustFoodOrderRequired = true;
		return true;
	}
	
	public void closeToast() {
		int toast = brunch.getMainIngredient();
		assert Food.isToast(toast);
		
		brunch.closeToast();
		
		Toast top = new Toast(MIN_GAME_COMPONENT_Z_ORDER + 1);
		top.setup(toast, holder.getHolderSizeType(), Type.Top);
		
		add(top);
		adjustFoodOrderRequired = true;
	}
	
	@Override
	protected void commitUpdate() {
		super.commitUpdate();
		
		if (!adjustFoodOrderRequired) {
			return;
		}
		adjustFoodOrderRequired = false;
		
		// to adjust food component location
		// by finding first food component
		// (other components first and then comes food components)
		final int count = objects.size();
		int firstFoodIndex = -1;
		for (int i = 0; i < count; i++) {
			if (objects.get(i) instanceof FoodComponent) {
				firstFoodIndex = i;
				break;
			}
		}
		if (firstFoodIndex == -1) {
			// no food component
			return;
		}
		
		// for each food component, find its position offset from this entity.
		holder.getFoodLocation(loc);
		int yOffset = 0;
		for (int i = firstFoodIndex; i < count; i++) {
			ObjectBase obj = objects.get(i);
			if (obj instanceof FoodComponent == false) {
				continue;
			}
			
			FoodComponent food = (FoodComponent)objects.get(i);
			if (Food.isBeverage(food.getFoodType())) {
				holder.getBeverageLocation(loc);
				food.setupOffset(loc.x - box.left, loc.y - box.top);
				holder.getFoodLocation(loc);
			} else {
				food.setupOffset(loc.x - box.left, loc.y - box.top + yOffset);
				yOffset += FOOD_COMPONENT_LOCATION_OFFSET_Y;
			}
		}
	}
	
	public Brunch getBrunch() {
		return brunch;
	}
	
	public void updateYourBeverageFirst(long timeDelta) {
		// someone should be food plate, draw beverage here!
		if (beverage != null) {
			beverage.update(timeDelta, this);
		}
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		if (event.what == GameEvent.DROP_ACCEPTED && event.obj == this) {
			return true;
		} else if (event.what == GameEvent.DROP_REJECTED && event.obj == this) {
			return true;
		}
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		if (Config.DEBUG_LOG) Log.d(TAG, "handleGameEvent event:" + event);
		if (event.what == GameEvent.DRAG_END) {
			GameEventSystem.scheduleEvent(GameEvent.DROP_GAME_ENTITY, 0, this);
		} else if (event.what == GameEvent.DROP_REJECTED) {
			bounceBackToHolder();
		} else if (event.what == GameEvent.DROP_ACCEPTED) {
			GameEventSystem.scheduleEvent(GameEvent.BRUNCH_TIME_TO_LEAVE, 0, this);
		}
	}
	
	// TODO: use translate animation to bounce back!
	private void bounceBackToHolder() {
		holder.getFoodLocation(loc);
		move(loc.x, loc.y);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " brunch:" + brunch;
	}
	
	private static final int FOOD_COMPONENT_LOCATION_OFFSET_Y = -8;
	
	private BrunchHolder holder;
	private Brunch brunch;
	private FoodComponent beverage;
	
	private PointF loc = new PointF();
	private boolean adjustFoodOrderRequired = false;
	
	public static class BrunchComparator extends GameObjectComparator {

		@Override
		public int compare(ObjectBase lhs, ObjectBase rhs) {
			int result = super.compare(lhs, rhs);
			
			if (result != 0 || !(lhs instanceof FoodComponent) || !(rhs instanceof FoodComponent)) {
				return result;
			}
			
			// both are FoodComponent, compare by food type
			final int lFood = ((FoodComponent)lhs).getFoodType();
			final int rFood = ((FoodComponent)rhs).getFoodType();
			if (lFood < rFood) {
				return -1;
			} else if (lFood > rFood) {
				return 1;
			} else {
				return 0;
			}
		}
		
	}
	private static BrunchComparator sComparator = null;
	static {
		sComparator = new BrunchComparator();
	}
	@Override
	protected Comparator<ObjectBase> getComparator() {
		return sComparator;
	}
	
}
