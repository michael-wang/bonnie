package com.studioirregular.bonniesbrunch.entity;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.SoundSystem;
import com.studioirregular.bonniesbrunch.SoundSystem.Sound;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.component.ButtonComponent;
import com.studioirregular.bonniesbrunch.component.RenderComponent;

// when pressed: send gameEventWhenPressed to parent directly.
public class ButtonEntity extends GameEntity {
	
	private static final String TAG = "button-entity";
	
	public static class Builder {
		// required parameters
		private float x, y, width, height;
		private String upTexture, downTexture;
		
		public Builder(float x, float y, float width, float height, String upTexture, String downTexture) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.upTexture = upTexture;
			this.downTexture = downTexture;
		}
		
		// optional parameters
		private int emitEventWhat;
		private int emitEventArg;
		public void emitEventWhenPressed(int eventWhat) {
			emitEventWhenPressed(eventWhat, 0);
		}
		
		public void emitEventWhenPressed(int eventWhat, int eventArg) {
			emitEventWhat = eventWhat;
			emitEventArg = eventArg;
		}
		
		private String clickSound;
		public void playSoundWhenClicked(String sound) {
			clickSound = sound;
		}
		
		private float touchOffsetLeft, touchOffsetTop, touchOffsetRight, touchOffsetBottom;
		public void offsetTouchArea(float offsetLeft, float offsetTop, float offsetRight, float offsetBottom) {
			touchOffsetLeft = offsetLeft;
			touchOffsetTop = offsetTop;
			touchOffsetRight = offsetRight;
			touchOffsetBottom = offsetBottom;
		}
	}

	public ButtonEntity(int zOrder, Builder builder) {
		super(zOrder);
		
		setup(builder.x, builder.y, builder.width, builder.height, builder.upTexture, builder.downTexture, builder.emitEventWhat, builder.emitEventArg);
		
		ButtonComponent button = new ButtonComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
		final float touchAreaWidth = builder.width - builder.touchOffsetLeft + builder.touchOffsetRight;
		final float touchAreaHeight = builder.height - builder.touchOffsetTop + builder.touchOffsetBottom;
		button.setup(builder.x + builder.touchOffsetLeft, builder.y + builder.touchOffsetTop, 
				touchAreaWidth, touchAreaHeight, builder.x, builder.y);
		add(button);
		
		if (builder.clickSound != null) {
			clickSound = SoundSystem.getInstance().load(builder.clickSound);
		}
	}
	
	protected void setup(float x, float y, float width, float height, String normalTexture, String downTexture, int gameEventWhenPressed, int eventArg1) {
		if (Config.DEBUG_LOG) Log.d(TAG, "setup x:" + x + ",y:" + y + ",w:" + width + ",h:" + height + ",gameEventWhenPressed:" + gameEventWhenPressed);
		super.setup(x, y, width, height);
		
		renderUp = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		renderUp.setup(width, height);
		renderUp.setup(normalTexture);
		add(renderUp);
		
		renderDown = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		renderDown.setup(width, height);
		renderDown.setup(downTexture);
		
		if (gameEventWhenPressed == 0) {
			sendEventWhenPressed = false;
		} else {
			sendEventWhenPressed = true;
			this.gameEventWhenPressed = gameEventWhenPressed;
			this.gameEventArg1 = eventArg1;
		}
	}
	
	// there are situations which emit event cannot be decided when builder is constucted.
	public void setEmitEventWhenPressed(int eventWhat, int eventArg1) {
		sendEventWhenPressed = true;
		this.gameEventWhenPressed = eventWhat;
		this.gameEventArg1 = eventArg1;
	}
	
	public void setDisableTexture(String texture) {
		renderDisabled = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		renderDisabled.setup(box.width(), box.height());
		renderDisabled.setup(texture);
	}
	
	@Override
	public void setDisable(boolean disable) {
		if (this.disable == disable) {
			return;
		}
		super.setDisable(disable);
		
		if (disable) {
			if (renderDisabled != null) {
				add(renderDisabled);
				
				if (down) {
					remove(renderDown);
				} else {
					remove(renderUp);
				}
			}
		} else {
			if (renderDisabled != null) {
				remove(renderDisabled);
				
				if (down) {
					add(renderDown);
				} else {
					add(renderUp);
				}
			}
		}
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		switch (event.what) {
		case GameEvent.BUTTON_UP:
			if (clickSound != null) {
				SoundSystem.getInstance().playSound(clickSound, false);
			}
			if (sendEventWhenPressed) {
				scheduleSendEvent = true;
			}
			// fall through
		case GameEvent.BUTTON_CANCEL:
			down = false;
			remove(renderDown);
			add(renderUp);
			break;
		case GameEvent.BUTTON_DOWN:
			down = true;
			remove(renderUp);
			add(renderDown);
			break;
		}
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		super.update(timeDelta, parent);
		
		if (scheduleSendEvent) {
			scheduleSendEvent = false;
			GameEvent event = GameEventSystem.getInstance().obtain(gameEventWhenPressed, gameEventArg1);
			((GameEntity)parent).send(event);
			if (Config.DEBUG_LOG) Log.d(TAG, "scheduleSendEvent event:" + event + ",parent:" + parent);
		}
	}
	
	protected RenderComponent renderUp;
	protected RenderComponent renderDown;
	protected RenderComponent renderDisabled;
	private boolean sendEventWhenPressed = false;
	private int gameEventWhenPressed;
	private int gameEventArg1;
	private boolean scheduleSendEvent = false;
	private boolean down = false;
	private Sound clickSound;
}
