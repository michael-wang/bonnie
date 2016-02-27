package com.studioirregular.bonniesbrunch.entity;

import android.graphics.PointF;

import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.component.RenderComponent;

public class Plate extends GameEntity implements BrunchHolder {

	public Plate(int zOrder) {
		super(zOrder);
	}
	
	@Override
	public void setup(float x, float y, float width, float height) {
		super.setup(x, y, width, height);
		
		RenderComponent bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		bg.setup(width, height);
		bg.setup("game_table_plate_b");
		add(bg);
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
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
	public void getBeverageLocation(PointF loc) {
		loc.set(432, 351);
	}
	
	@Override
	public void getOpenToastLocation(PointF loc) {
		loc.set(465, 393);
	}
	
	@Override
	public boolean isDraggable() {
		return true;
	}
	
	// dirty trick to let brunch's beverage draw behind plate.
	// NOTICE: make sure to notify me about brunch removed!
	private BrunchEntity brunch = null;
	public void brunchAdded(BrunchEntity brunch) {
		this.brunch = brunch;
	}
	public void brunchRemoved() {
		brunch = null;
	}

	@Override
	public void update(long timeDelta, ObjectBase parent) {
		if (brunch != null) {
			brunch.updateYourBeverageFirst(timeDelta);
		}
		
		super.update(timeDelta, parent);
	}
	

}
