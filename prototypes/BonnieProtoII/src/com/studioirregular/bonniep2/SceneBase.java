package com.studioirregular.bonniep2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import javax.microedition.khronos.opengles.GL10;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import com.studioirregular.bonniep2.TextureSystem.XmlFormatException;

public abstract class SceneBase implements Scene, OnTouchListener, EventListener, EventHost {
	
	private static final boolean DO_LOG = false;
	private static final String TAG = "scene-base";
	
	protected Game game;
	protected GameHandler handler;
	protected boolean isLoaded = false;
	protected List<Entity> entityList = new ArrayList<Entity>();
	protected float width, height;
	protected SceneParser parser;
	protected int textureResourceId;
	
	protected LinkedList<SceneNode> nodeList = new LinkedList<SceneNode>();
	protected ListIterator<SceneNode> nodeIterator;
	protected SceneNode node = null;
	protected PointF scale = new PointF();
	
	protected boolean isStop = true;
	protected boolean isPause = false;
	protected long prevFrameTime;
	protected long timeDiff;
//	protected long startTime;
//	protected long accumulatedDiff;
	protected PauseButtonEntity pauseButton;
	
	public SceneBase(Game game) {
		this.game = game;
		this.handler = game.getHandler();
	}
	
	// Invoker: setCommand
	public void addSceneNode(SceneNode node) {
		if (DO_LOG) Log.d(TAG, "addSceneNode node:" + node);
		nodeList.addLast(node);
	}
	
	// command receiver
	@Override
	public void addEntity(Entity entity) {
		if (DO_LOG) Log.d(TAG, "addEntity entity:" + entity);
		handler.sendMessage(handler.obtainMessage(
				GameHandler.MSG_SCHEDULE_COMMAND, new AddEntityCommand(this, entity)));
	}
	
	@Override
	public void addEntity(Entity entity, String afterEntityId) {
		if (DO_LOG) Log.d(TAG, "addEntity afterEntityId:" + afterEntityId + ", entity:" + entity);
		handler.sendMessage(handler.obtainMessage(
				GameHandler.MSG_SCHEDULE_COMMAND, new AddEntityCommand(this, entity, afterEntityId)));
	}
	
	@Override
	public void removeEntity(String entityId) {
		if (DO_LOG) Log.d(TAG, "removeEntity entityId:" + entityId);
		handler.sendMessage(handler.obtainMessage(GameHandler.MSG_SCHEDULE_COMMAND, new RemoveEntityCommand(this, entityId)));
	}
	
	@Override
	public void removeEntity(Entity entity) {
		if (DO_LOG) Log.d(TAG, "removeEntity entity:" + entity);
		handler.sendMessage(handler.obtainMessage(GameHandler.MSG_SCHEDULE_COMMAND, new RemoveEntityCommand(this, entity)));
	}
	
	@Override
	public Entity getEntity(String entityId) {
		synchronized (entityList) {
			for (Entity entity : entityList) {
				if (entity.getId().equals(entityId)) {
					return entity;
				}
			}
		}
		return null;
	}
	
	void addEntityInternal(Entity entity) {
		synchronized (entityList) {
			entityList.add(entity);
		}
	}
	
	void addEntityInternal(Entity entity, String afterEntityId) {
		synchronized (entityList) {
			for (int i = 0; i < entityList.size(); i++) {
				if (entityList.get(i).getId().equals(afterEntityId)) {
					entityList.add(i + 1, entity);
					return;
				}
			}
		}
	}
	
	void removeEntityInternal(String entityId) {
		synchronized (entityList) {
			for (int i = 0; i < entityList.size(); i++) {
				if (entityList.get(i).getId().equals(entityId)) {
					entityList.remove(i);
					return;
				}
			}
		}
	}
	
	void removeEntityInternal(Entity entity) {
		synchronized (entityList) {
			entityList.remove(entity);
		}
	}
	
	// Scene interface
	@Override
	public void setSceneParser(SceneParser parser) {
		this.parser = parser;
	}
	
	@Override
	public void setTextureResource(int id) {
		this.textureResourceId = id;
	}
	
	@Override
	public void load(Context context, GL10 gl) {
		if (loadTexture(context, gl) == false) {
			Log.e(TAG, "load texture failed, texture resource id:" + textureResourceId);
			return;
		}
		
		if (parser.parse(context, this) == false) {
			Log.e(TAG, "parser.parser failed");
			return;
		}
		
		start();
		
		isLoaded = true;
	}
	
	@Override
	public boolean isLoaded() {
		return isLoaded;
	}
	
	protected boolean loadTexture(Context context, GL10 gl) {
		if (this.textureResourceId == 0) {
			Log.e(TAG, "loadTexture textureResourceId not set!");
			return false;
		}
		
		try {
			TextureSystem.getInstance().release(gl);	// TODO: move this to stop scene
			TextureSystem.getInstance().load(context, gl, textureResourceId);
		} catch (XmlFormatException e) {
			Log.e(TAG, "load failed:" + e);
			return false;
		} catch (XmlPullParserException e) {
			Log.e(TAG, "load failed:" + e);
			return false;
		} catch (IOException e) {
			Log.e(TAG, "load failed:" + e);
			return false;
		}
		return true;
	}
	
	@Override
	public void start() {
		if (DO_LOG) Log.d(TAG, "start");
		nodeIterator = nodeList.listIterator();
		try {
			node = nodeIterator.next();
			node.execute(this);
		} catch (NoSuchElementException e) {
			Log.e(TAG, "start exception:" + e);
			game.gameOver();
		}
		
		prevFrameTime = System.currentTimeMillis();
		timeDiff = 0;
//		startTime = prevFrameTime;
//		accumulatedDiff = 0;
		isStop = false;
	}
	
	@Override
	public synchronized void stop(GL10 gl) {
		if (DO_LOG) Log.d(TAG, "stop");
		isStop = true;
		
		// release texture
		TextureSystem.getInstance().release(gl);
	}
	
	@Override
	public synchronized boolean isStop() {
		return isStop;
	}
	
	@Override
	public void nextNode() {
		if (DO_LOG) Log.d(TAG, "nextNode");
		if (nodeIterator.hasNext()) {
			node = nodeIterator.next();
			node.execute(this);
		} else {
			game.gameOver();
		}
	}
	
	public void onAddPauseButton() {
		pauseButton = new PauseButtonEntity(this, "pause_button");
		addEntity(pauseButton);
		if (!isStop) {
			pauseButton.setEnable(true);
		} else {
			pauseButton.setEnable(false);
		}
	}
	
	@Override
	public void update() {
		if (isStop) {
			return;
		} else if (isPause) {
			long now = System.currentTimeMillis();
			timeDiff = now - prevFrameTime;
			prevFrameTime = now;
			pauseButton.update(timeDiff);
			return;
		}
		
		long now = System.currentTimeMillis();
		timeDiff = now - prevFrameTime;
		prevFrameTime = now;
		
//		accumulatedDiff += timeDiff;
//		Log.d(TAG, "accumulatedDiff:" + accumulatedDiff + ",elapsed:" + (now - startTime) + ", delta:" + (accumulatedDiff - now + startTime));
		
		synchronized (entityList) {
			for (Entity entity : entityList) {
				entity.update(timeDiff);
			}
		}
	}
	
	@Override
	public synchronized void draw(GL10 gl) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		
		synchronized (entityList) {
			for (Entity entity : entityList) {
				
				if (entity.getRenderableCount() == 0) {
					continue;
				}
				
				for (int i = 0; i < entity.getRenderableCount(); i++) {
					GLRenderable render = entity.getRenderable(i);
					if (render.getVisible() == false) {
						continue;
					}
					
					GLTexture texture = render.getGLTexture();
					if (texture == null) {
						continue;
					}
					
					gl.glVertexPointer(3, GL10.GL_FLOAT, 0, render.getVertex());
					gl.glBindTexture(GL10.GL_TEXTURE_2D, render.getGLTexture().getGLTextureId());
					gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, render.getGLTexture().getCoordinates());
					
					gl.glMatrixMode(GL10.GL_MODELVIEW);
					gl.glPushMatrix();
					gl.glLoadIdentity();
					
					gl.glTranslatef(render.getTranslateX(), render.getTranslateY(), 0);
					gl.glScalef(render.getScaleX(), render.getScaleY(), 1.0f);
					
					gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
					
					gl.glMatrixMode(GL10.GL_MODELVIEW);
					gl.glPopMatrix();
				} // for each renderable
			} // for each entity
		} // synchronized (entityList)
	}
	
	@Override
	public void onEvent(Event event) {
		if (DO_LOG) Log.d(TAG, "onEvent event:" + event);
		
		if (event.what == EntityEvent.FOOD_PRODUCT_DROPT) {
			handleFoodDropEvent((EntityEvent)event);
			return;
		} else if (event.what == SceneEvent.SCENE_PAUSE) {
			isPause = true;
		} else if (event.what == SceneEvent.SCENE_RESUME) {
			isPause = false;
		} else if (event.what == SceneEvent.LEVEL_START) {
			if (pauseButton != null) {
				pauseButton.setEnable(true);
			}
		}
		
		Command command = node.getMappedCommand(event);
		if (DO_LOG) Log.d(TAG, "onEvent event:" + event + ", mapped command:" + command);
		if (command != null) {
			command.execute();
		}
	}
	
	@Override
	public boolean onTouch(MotionEvent event) {
		synchronized (entityList) {
			final int count = entityList.size();
			for (int i = count-1; i >= 0; i--) {
				BasicEntity entity = (BasicEntity)entityList.get(i);
				if (entity.onTouch(event)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void scheduleCommand(Command command) {
		GameHandler handler = game.getHandler();
		handler.sendMessage(handler.obtainMessage(GameHandler.MSG_SCHEDULE_COMMAND, command));
	}
	
	protected void dispatchEvent(Event event) {
		synchronized (entityList) {
			for (Entity entity : entityList) {
				if (entity instanceof EventListener) {
					((EventListener)entity).onEvent(event);
				}
			}
		}
	}
	
	@Override
	public void send(Object sender, Event event) {
		if (DO_LOG) Log.d(TAG, "send sender:" + sender + ", event:" + event);
		dispatchEvent(event);
		onEvent(event);
	}
	
	protected void handleFoodDropEvent(EntityEvent event) {
		if (DO_LOG) Log.d(TAG, "handleFoodDropEvent event:" + event);
		
		FoodProductEntity food = (FoodProductEntity)event.obj;
		
		for (Entity entity : entityList) {
			if (entity instanceof FoodProductConsumer) {
				if (isCollided(food, entity)) {
					FoodProductConsumer consumer = (FoodProductConsumer)entity;
					if (consumer.doYouWantToConsumeIt(food)) {
						consumer.consumeIt(food);
						send(this, new EntityEvent(EntityEvent.FOOD_PRODUCT_CONSUMED, entity.getId(), food));
						return;
					}
				}
			}
		}
		
		food.droptRejected();
	}
	
	protected boolean isCollided(Entity entityA, Entity entityB) {
		final RectF boxA = entityA.getBoundingBox();
		final RectF boxB = entityB.getBoundingBox();
		if (DO_LOG) Log.d(TAG, "entity(" + entityA.getId() + "):" + boxA + " , entity(" + entityB.getId() + "):" + boxB);
		return RectF.intersects(boxA, boxB);
	}
	
}
