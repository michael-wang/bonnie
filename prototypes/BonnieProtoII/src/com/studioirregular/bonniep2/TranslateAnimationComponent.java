package com.studioirregular.bonniep2;

public class TranslateAnimationComponent implements Component, Animation {

	private Entity entity;
	private EventHost eventHost;
	private String id;
	
	private float fromX, toX;
	private float fromY, toY;
	private long duration;
	private boolean loop;
	
	private boolean isStarted = false;
	private long elapsedTime;
	private float currentX, currentY;
	
	public TranslateAnimationComponent(Entity entity, EventHost host,
			String id, float fromX, float toX, float fromY, float toY,
			long duration, boolean loop) {
		this.entity = entity;
		this.eventHost = host;
		this.id = id;
		
		this.fromX = fromX;
		this.toX = toX;
		this.fromY= fromY;
		this.toY = toY;
		this.duration = duration;
		this.loop = loop;
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
		currentX = fromX;
		currentY = fromY;
		
		isStarted = true;
		eventHost.send(this, new ComponentEvent(ComponentEvent.ANIMATION_STARTED, id));
	}
	
	@Override
	public void stop() {
		isStarted = false;
	}
	
	@Override
	public void update(long timeDiff) {
		if (isStarted == false) {
			return;
		}
		
		elapsedTime += timeDiff;
		if (elapsedTime > duration) {
			if (!loop) {
				currentX = toX;
				currentY = toY;
				
				isStarted = false;
				eventHost.send(this, new ComponentEvent(ComponentEvent.ANIMATION_ENDED, id));
			} else {
				elapsedTime = (elapsedTime % duration);
				currentX = fromX + (toX - fromX) * elapsedTime / duration;
				currentY = fromY + (toY - fromY) * elapsedTime / duration;
			}
		} else {
			currentX = fromX + (toX - fromX) * elapsedTime / duration;
			currentY = fromY + (toY - fromY) * elapsedTime / duration;
		}
		
		if (entity.getRenderableCount() > 0) {
			final int count = entity.getRenderableCount();
			for (int i = 0; i < count; i++) {
				final GLRenderable render = entity.getRenderable(i);
				render.setX(currentX);
				render.setY(currentY);
			}
		}
	}

	@Override
	public String getId() {
		return id;
	}

}
