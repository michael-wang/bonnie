package com.studioirregular.bonniep2;

public class ScalingAnimationComponent implements Component, Animation {

	private float fromScaleX, toScaleX;
	private float fromScaleY, toScaleY;
	private long duration;
	private boolean loop;
	
	private boolean isStarted = false;
	private long elapsedTime;
	private Entity entity;
	private String id;
	
	public ScalingAnimationComponent(Entity entity, String id, float fromSX,
			float fromSY, float toSX, float toSY, long duration, boolean loop) {
		fromScaleX = fromSX;
		fromScaleY = fromSY;
		toScaleX = toSX;
		toScaleY = toSY;
		this.duration = duration;
		this.loop = loop;
		this.entity = entity;
	}
	
	@Override
	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	@Override
	public boolean isStarted() {
		return isStarted;
	}

	@Override
	public void start() {
		elapsedTime = 0;
		isStarted = true;
	}
	
	@Override
	public void stop() {
		onStop();
	}
	
	@Override
	public void update(long timeDiff) {
		if (isStarted == false) {
			return;
		}
		
		float sx, sy;
		elapsedTime += timeDiff;
		
		if (elapsedTime > duration) {
			if (!loop) {
				sx = 1.0f;
				sy = 1.0f;
				onStop();
			} else {
				elapsedTime = (elapsedTime % duration);
				sx = fromScaleX + (toScaleX - fromScaleX) * elapsedTime / duration;
				sy = fromScaleY + (toScaleY - fromScaleY) * elapsedTime / duration;
			}
		} else {
			sx = fromScaleX + (toScaleX - fromScaleX) * elapsedTime / duration;
			sy = fromScaleY + (toScaleY - fromScaleY) * elapsedTime / duration;
		}
		
		if (entity.getRenderableCount() > 0) {
			for (int i = 0; i < entity.getRenderableCount(); i++) {
				GLRenderable render = entity.getRenderable(0);
				render.setScale(sx, sy);
			}
		}
	}

	@Override
	public String getId() {
		return id;
	}
	
	private void onStop() {
		isStarted = false;
//		if (eventHost != null) {
//			eventHost.send(this, new ComponentEvent(ComponentEvent.ANIMATION_ENDED, id));
//		}
	}
	

}
