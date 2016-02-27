package com.studioirregular.bonniesbrunch.entity;

import android.graphics.RectF;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.ContextParameters;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.SoundSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.RenderSystem;
import com.studioirregular.bonniesbrunch.RenderSystem.RenderObject;
import com.studioirregular.bonniesbrunch.SoundSystem.Sound;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.component.RenderComponent;

public class Trashcan extends GameEntity {

//	private static final String TAG = "trashcan";
	
	public Trashcan(int zOrder) {
		super(zOrder);
	}
	
	@Override
	public void setup(float x, float y, float width, float height) {
		super.setup(x, y, width, height);
		
		renderClosed = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		renderClosed.setup(width, height);
		renderClosed.setup("game_table_trashcan");
		
		renderOpened = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		renderOpened.setup(width, height);
		renderOpened.setup("game_table_trashcan_open");
		
		soundDump = SoundSystem.getInstance().load("game_table_trashcan_s1");
		
		opened = false;
		updateState();
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		super.update(timeDelta, parent);
		
		if (opened) {
			elapsedOpenTime += timeDelta;
			if (OPEN_DURATION <= elapsedOpenTime) {
				closeTrashcan();
			}
		}
		
		if (ContextParameters.getInstance().debugDrawTouchArea && visible) {
			if (renderTouchRegion == null) {
				debugSetupDrawTouchArea();
			}
			renderTouchRegion.setPosition(box.left, box.top);
			RenderSystem.getInstance().scheduleRenderObject(renderTouchRegion);
		}
	}
	
	@Override
	public void reset() {
		if (opened) {
			closeTrashcan();
		}
		super.reset();
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		if (disable) {
			return false;
		}
		
		if (event.what == GameEvent.DROP_GAME_ENTITY) {
			GameEntity entity = (GameEntity)event.obj;
			if (entity instanceof Candy) {
				return false;	// you can't dump candy!!
			}
			if (RectF.intersects(box, entity.box)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		if (event.what == GameEvent.DROP_GAME_ENTITY) {
			GameEventSystem.scheduleEvent(GameEvent.DROP_ACCEPTED, 0, event.obj);
			SoundSystem.getInstance().playSound(soundDump, false);
			
			openTrashcan();
		}
	}
	
	private void openTrashcan() {
		if (!opened) {
			opened = true;
			updateState();
		}
		elapsedOpenTime = 0;
	}
	
	private void closeTrashcan() {
		if (opened) {
			opened = false;
			updateState();
		}
		elapsedOpenTime = 0;
	}
	
	private void updateState() {
		if (opened) {
			remove(renderClosed);
			add(renderOpened);
		} else {
			add(renderClosed);
			remove(renderOpened);
		}
	}
	
	private void debugSetupDrawTouchArea() {
		renderTouchRegion = new RenderObject(box.width(), box.height());
		renderTouchRegion.setColor(Config.TOUCH_REGION_COLOR_R,
				Config.TOUCH_REGION_COLOR_G, Config.TOUCH_REGION_COLOR_B,
				Config.TOUCH_REGION_COLOR_A);
	}
	
	private boolean opened = false;
	private long elapsedOpenTime = 0;
	private static final long OPEN_DURATION = 500L;
	private RenderComponent renderClosed, renderOpened;
	
	private RenderObject renderTouchRegion;
	private Sound soundDump;
}
