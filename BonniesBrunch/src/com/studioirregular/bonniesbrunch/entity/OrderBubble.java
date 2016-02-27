package com.studioirregular.bonniesbrunch.entity;

import java.util.Comparator;

import android.graphics.PointF;

import com.studioirregular.bonniesbrunch.FoodSystem.Brunch;
import com.studioirregular.bonniesbrunch.FoodSystem.Food;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.component.FoodComponent;
import com.studioirregular.bonniesbrunch.component.FrameAnimationComponent;
import com.studioirregular.bonniesbrunch.component.RenderComponent;
import com.studioirregular.bonniesbrunch.component.Toast;
import com.studioirregular.bonniesbrunch.entity.BrunchEntity.BrunchComparator;
import com.studioirregular.bonniesbrunch.entity.BrunchHolder.Size;

public class OrderBubble extends GameEntity {

	public OrderBubble(int zOrder) {
		super(zOrder);
		
		foodComponentOffset.set(8, 29);
		beverageComponentOffset.set(58, 46);
	}
	
	@Override
	public void setup(float x, float y, float width, float height) {
		super.setup(x, y, width, height);
		
		bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		bg.setup(width, height);
		bg.setup("game_order_bg");
		
		thinkAnimation = new FrameAnimationComponent(MIN_GAME_COMPONENT_Z_ORDER);
		thinkAnimation.setLoop(true);
		thinkAnimation.addFrame("game_order_think_1", width, height, 333);
		thinkAnimation.addFrame("game_order_think_2", width, height, 333);
		thinkAnimation.addFrame("game_order_think_3", width, height, 334);
		
		thinkEquationsAnimation = new FrameAnimationComponent(MIN_GAME_COMPONENT_Z_ORDER);
		thinkEquationsAnimation.setLoop(true);
		thinkEquationsAnimation.addFrame("game_guest_08_order_think_1", width, height, 1000);
		thinkEquationsAnimation.addFrame("game_guest_08_order_think_2", width, height, 1000);
		thinkEquationsAnimation.addFrame("game_guest_08_order_think_3", width, height, 1000);
	}
	
	public void startThinking() {
		clear();	// clear previous order.
		
		add(thinkAnimation);
		thinkAnimation.start();
	}
	
	public void makeDecision(Brunch customerOrder) {
		thinkAnimation.stop();
		remove(thinkAnimation);
		
		add(bg);
		
		brunch.set(customerOrder);
		setupBrunch(brunch);
	}
	
	public void thinkEquations(boolean on) {
		if (thinkEquationsAnimation == null) {
			return;
		}
		
		if (on) {
			clear();
			add(thinkEquationsAnimation);
			thinkEquationsAnimation.start();
		} else {
			remove(thinkEquationsAnimation);
			thinkEquationsAnimation.stop();
			add(bg);
			setupBrunch(brunch);
		}
	}
	
	public void clear() {
		// remove children.
		for (int i = 0; i < getCount(); i++) {
			remove(getObject(i));
		}
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
	}
	
	private void setupBrunch(Brunch brunch) {
		FoodComponent food = null;
		float xOffset = foodComponentOffset.x;
		float yOffset = foodComponentOffset.y;	// changes with each food component added.
		
		int mainFood = brunch.getMainIngredient();
		if (Food.isToast(mainFood)) {
			food = new Toast(MIN_GAME_COMPONENT_Z_ORDER + 1);
			((Toast)food).setup(mainFood, Size.Medium, Toast.Type.Bottom);
		} else {
			food = new FoodComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
			food.setup(mainFood, Size.Medium);
		}
		food.setupOffset(xOffset, yOffset);
		yOffset += FOOD_COMPONENT_OFFSET_STEP;
		add(food);
		
		int addOnA = brunch.getAddOnA();
		if (addOnA != 0) {
			food = new FoodComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
			food.setup(addOnA, Size.Medium);
			food.setupOffset(xOffset, yOffset);
			yOffset += FOOD_COMPONENT_OFFSET_STEP;
			add(food);
		}
		
		int addOnB = brunch.getAddOnB();
		if (addOnB != 0) {
			food = new FoodComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
			food.setup(addOnB, Size.Medium);
			food.setupOffset(xOffset, yOffset);
			yOffset += FOOD_COMPONENT_OFFSET_STEP;
			add(food);
		}
		
		int sauce = brunch.getSauce();
		if (sauce != 0) {
			food = new FoodComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
			food.setup(sauce, Size.Medium);
			food.setupOffset(xOffset, yOffset);
			yOffset += FOOD_COMPONENT_OFFSET_STEP;
			add(food);
		}
		
		if (Food.isToast(mainFood)) {
			food = new Toast(MIN_GAME_COMPONENT_Z_ORDER + 2);
			((Toast)food).setup(mainFood, Size.Medium, Toast.Type.Top);
			food.setupOffset(xOffset, yOffset);
			yOffset += FOOD_COMPONENT_OFFSET_STEP;
			add(food);
		}
		
		int beverage = brunch.getBeverage();
		if (beverage != 0) {
			food = new FoodComponent(MIN_GAME_COMPONENT_Z_ORDER + 2);
			food.setup(beverage, Size.Medium);
			food.setupOffset(beverageComponentOffset.x, beverageComponentOffset.y);
			add(food);
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
	
	private Brunch brunch = new Brunch();
	
	private RenderComponent bg;
	private FrameAnimationComponent thinkAnimation;
	private FrameAnimationComponent thinkEquationsAnimation;
	
	private PointF foodComponentOffset = new PointF();
	private PointF beverageComponentOffset = new PointF();
	private static final float FOOD_COMPONENT_OFFSET_STEP = -8;

}
