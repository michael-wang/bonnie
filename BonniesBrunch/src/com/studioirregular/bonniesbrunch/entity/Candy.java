package com.studioirregular.bonniesbrunch.entity;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.component.Draggable;
import com.studioirregular.bonniesbrunch.component.RenderComponent;

public class Candy extends GameEntity {

	private static final String TAG = "candy";
	
	public static final int TYPE_I		= 1;
	public static final int TYPE_II		= 2;
	public static final int TYPE_III	= 3;
	
	public Candy(int zOrder, CandyHolder holder, int type) {
		super(zOrder);
		this.holder = holder;
		this.type = type;
		
		internalSetup();
	}
	
	public void internalSetup() {
		PointF loc = new PointF();
		holder.getCandyLocation(this, loc);
		
		setup(loc.x, loc.y, WIDTH, HEIGHT);
		
		RenderComponent bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		bg.setup(WIDTH, HEIGHT);
		if (type == TYPE_I) {
			bg.setup("game_table_candy_candy_1");
		} else if (type == TYPE_II) {
			bg.setup("game_table_candy_candy_2");
		} else if (type == TYPE_III) {
			bg.setup("game_table_candy_candy_3");
		} else {
			Log.w(TAG, "unknown candy type:" + type);
		}
		add(bg);
		
		RectF holderBox = holder.getHolderBox();
		Draggable dragger = new Draggable(MIN_GAME_COMPONENT_Z_ORDER + 1);
		dragger.setTouchArea(box, 
				holderBox.left - loc.x, 
				holderBox.top - loc.y, 
				holderBox.right - (loc.x + WIDTH), 
				holderBox.bottom - (loc.y + HEIGHT));
		add(dragger);
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		if (event.what == GameEvent.DROP_REJECTED && event.obj == this) {
			return true;
		}
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		if (event.what == GameEvent.DRAG_END) {
			GameEventSystem.scheduleEvent(GameEvent.DROP_GAME_ENTITY, 0, this);
		} else if (event.what == GameEvent.DROP_REJECTED) {
			bounceBack();
		}
	}
	
	private void bounceBack() {
		PointF loc = new PointF();
		holder.getCandyLocation(this, loc);
		move(loc.x, loc.y);
	}
	
	private static final float WIDTH = 23;
	private static final float HEIGHT = 23;
	
	private CandyHolder holder;
	public int type = 0;

}
