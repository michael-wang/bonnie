package com.studioirregular.bonniep2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.PointF;
import android.util.Log;

import com.studioirregular.bonnie.foodsystem.FoodSystem.Brunch;
import com.studioirregular.bonnie.foodsystem.FoodSystem.Food;
import com.studioirregular.bonniep2.FoodComponent.Category;

public class FoodProductEntity extends BasicEntity {

	private static final boolean DO_LOG = false;
	private static final String TAG = "food-product-entity";
	
	private static final String FOOD_RENDER_COMPONENT_ID_PREFIX = "render-";
	private static final String TOAST_TOP_RENDER_COMPONENT_ID_POSTFIX = "-top";
	private static final int FOOD_COMPONENT_Y_OFFSET = 8;	// new food add on top of old food with y offset.
	
	protected List<FoodComponent> foodComponents = new ArrayList<FoodComponent>();
	protected Map<String, RenderComponent> food2Render = new HashMap<String, RenderComponent>();
	protected FoodProductHolder holder;
	protected EventHost eventHost;
	protected DraggableComponent dragger;
	protected ButtonComponent toastTop;
	
	private PointF foodLocation = new PointF();
	private PointF foodSize = new PointF();
	
	public FoodProductEntity(SceneBase scene, String id, FoodProductHolder holder, EventHost eventHost) {
		super(scene, id);
		
		if (DO_LOG) Log.d(TAG, "FoodProductEntity id:" + id);
		
		this.holder = holder;
		this.eventHost = eventHost;
		
		if (holder.isDraggable()) {
			dragger = new DraggableComponent(this, "dragger", this);
			addComponentInternal(dragger);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("FoodProductEntity: ");
		for (FoodComponent food : foodComponents) {
			result.append(food.getId());
			result.append(",");
		}
		return result.toString();
	}
	
	public void setBrunch(Brunch brunch) {
		if (DO_LOG) Log.d(TAG,"setBrunch brunch:" + brunch);
		
		Food[] foods = brunch.getComposition();
		for (Food food : foods) {
			FoodComponent component = FoodComponent.newInstance(food);
			super.addComponentInternal(component);
			onAddingFoodComponent(component);
		}
	}
	
	public boolean acceptFood(FoodComponent food) {
		for (FoodComponent f : foodComponents) {
			if (f.isCombinable(food) == false) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	void addComponentInternal(Component component) {
		if (!(component instanceof FoodComponent)) {
			super.addComponentInternal(component);
		} else {
			FoodComponent food = (FoodComponent)component;
			if (!acceptFood(food)) {
				Log.w(TAG, "addComponentInternal do not accept food:" + food);
				return;
			}
			
			super.addComponentInternal(component);
			
			onAddingFoodComponent(food);
		}
	}
	
	protected void onAddingFoodComponent(FoodComponent food) {
		if (DO_LOG) Log.d(TAG, "onAddingFoodComponent food:" + food);
		
		if (food.getCategory() == Category.Toast && ((FoodToast)food).isClosed() == false) {
			if (toastTop == null) {
				holder.getToastTopLocation(foodLocation);
				food.getTextureSize(holder.getHolderSizeType(), foodSize);
				toastTop = new ButtonComponent("toast-button", this, foodLocation.x, foodLocation.y, foodSize.x, foodSize.y);
				if (DO_LOG) Log.d(TAG, "onAddingFoodComponent toastTop:" + toastTop);
				add(toastTop);
			}
		}
		
		foodComponents.add(food);
		Collections.sort(foodComponents);
		
		if (food.getCategory() == Category.Toast) {
			RenderComponent renderBottom = genToastRenderComponent(food, true);
			food2Render.put(food.getId(), renderBottom);
			
			RenderComponent renderTop = genToastRenderComponent(food, false);
			food2Render.put(food.getId() + TOAST_TOP_RENDER_COMPONENT_ID_POSTFIX, renderTop);
		} else {
			RenderComponent render = genRenderComponent(food);
			food2Render.put(food.getId(), render);
		}
		
		adjustAndInsertRender();
	}
	
	// adjust renders component sequence and location according to foodComponents,
	// and insert them to render list.
	private void adjustAndInsertRender() {
		if (DO_LOG) Log.d(TAG, "adjustRender");
		
		// remove all render components
		renderableList.clear();
		
		if (dragger != null) {
			dragger.cleanTouchableArea();
		}
		
		// adjust render components' order and location
		PointF baseLocation = new PointF();
		holder.getFoodLocation(baseLocation);
		
		final int count = foodComponents.size();
		FoodComponent toastComponent = null;
		if (DO_LOG) Log.d(TAG, "count:" + count);
		for (int i = 0; i < count; i++) {
			FoodComponent food = foodComponents.get(i);
			RenderComponent render = null;
			
			if (food.getCategory() == Category.Toast) {
				toastComponent = food;
			}
			
			render = food2Render.get(food.getId());
			// adjust location
			if (food.getCategory() != FoodComponent.Category.Beverage) {
				render.setY(baseLocation.y - FOOD_COMPONENT_Y_OFFSET * i);
			}
			
			// insert back, with correct order.
			super.addComponentInternal(render);
			
			if (DO_LOG) Log.d(TAG, "food:" + food + ",render:" + render + ",dragger:" + dragger);
			if (dragger != null) {
				dragger.addTouchableArea(render);
			}
		}
		
		if (toastComponent != null) {
			RenderComponent render = food2Render.get(toastComponent.getId() + TOAST_TOP_RENDER_COMPONENT_ID_POSTFIX);
			FoodToast toast = (FoodToast)toastComponent;
			if (toast.isClosed()) {
				render.setX(baseLocation.x);
				render.setY(baseLocation.y - FOOD_COMPONENT_Y_OFFSET * (count-1));
			} else {
				holder.getToastTopLocation(foodLocation);
				render.setX(foodLocation.x);
				render.setY(foodLocation.y);
			}
			super.addComponentInternal(render);
		}
	}
	
	public int getProductPrice() {
		int price = 0;
		for (FoodComponent c : foodComponents) {
			price += c.getPrice();
		}
		if (DO_LOG) Log.d(TAG, "getProductPrice price:" + price);
		return price;
	}
	
	protected RenderComponent genRenderComponent(FoodComponent food) {
		if (holder == null) {
			Log.e(TAG, "addRenderComponent no holder...");
			return null;
		}
		
		String textureId = food.getTextureId(holder.getHolderSizeType());
		if (DO_LOG) Log.d(TAG, "genRenderComponent textureId:" + textureId);
		
		food.getTextureSize(holder.getHolderSizeType(), foodSize);
		if (food.getCategory() == Category.Candy) {
			holder.getCandyLocation(food, foodLocation);
		} else if (food.getCategory() == Category.Beverage) {
			holder.getBeverageLocation(foodLocation);
		} else {
			holder.getFoodLocation(foodLocation);
		}
		
		GLTexture texture = TextureSystem.getInstance().getPart(textureId);
		
		RenderComponent render = new RenderComponent(
				getFoodRenderComponentId(food), foodLocation.x, foodLocation.y,
				foodSize.x, foodSize.y, texture);
		
		return render;
	}
	
	protected RenderComponent genToastRenderComponent(FoodComponent food, boolean bottom) {
		if (!(food instanceof FoodToast)) {
			return null;
		}
		FoodToast toast = ((FoodToast)food);
		
		food.getTextureSize(holder.getHolderSizeType(), foodSize);
		GLTexture texture = null;
		RenderComponent render = null;
		
		if (bottom) {
			texture = TextureSystem.getInstance().getPart(toast.getBottomTextureId(holder.getHolderSizeType()));
			if (texture == null) {
				return null;
			}
			
			holder.getFoodLocation(foodLocation);
		} else {
			texture = TextureSystem.getInstance().getPart(toast.getTopTextureId(holder.getHolderSizeType()));
			if (texture == null) {
				return null;
			}
			
			holder.getToastTopLocation(foodLocation);
		}
		
		render = new RenderComponent("render-toast-bottom", foodLocation.x, foodLocation.y, foodSize.x, foodSize.y, texture);
		return render;
	}
	
	@Override
	public void onEvent(Event event) {
		super.onEvent(event);
		
		if (DO_LOG) Log.d(TAG, "onEvent event:" + event);
		if (event.what == ComponentEvent.DRAGGABLE_DROPT) {
			eventHost.send(this, new EntityEvent(EntityEvent.FOOD_PRODUCT_DROPT, id, this));
		} else if (event.what == ComponentEvent.BUTTON_UP) {
			closeToast();
		}
	}
	
	public void droptRejected() {
		if (DO_LOG) Log.d(TAG, "droptRejected");
		dragger.dropRejected();
	}
	
	public boolean isSameFood(FoodProductEntity otherFood) {
		if (foodComponents.size() != otherFood.foodComponents.size()) {
			return false;
		}
		
		for (int i = 0; i < foodComponents.size(); i++) {
			FoodComponent myComponent = foodComponents.get(i);
			FoodComponent otherComponent = otherFood.foodComponents.get(i);
			
			if (myComponent.getType() != otherComponent.getType()) {
				return false;
			}
		}
		return true;
	}
	
	private String getFoodRenderComponentId(FoodComponent food) {
		return FOOD_RENDER_COMPONENT_ID_PREFIX + food.getId();
	}
	
	private void closeToast() {
		for (FoodComponent food : foodComponents) {
			if (food.getCategory() == Category.Toast) {
				FoodToast toast = (FoodToast)food;
				if (toast.isClosed() == false) {
					toast.close();
					adjustAndInsertRender();
				}
			}
		}
	}
	
}
