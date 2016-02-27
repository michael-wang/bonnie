package com.studioirregular.bonniep1;

import java.util.LinkedList;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.opengl.GLUtils;
import android.util.Log;

public class SceneSelectLevel implements Scene, UserInput {

	private static final String TAG = "scene-select_level";
	private static final boolean DO_LOG = false;
	
	private Context context;
	private Game game;
	private boolean loaded = false;
	private LinkedList<GameEntity> gameEntities = new LinkedList<GameEntity>();
	
	private LinkedList<GameEntity> scrollableEntities = new LinkedList<GameEntity>();
	private PointF scroll = new PointF();	// accumulated scroll
	
	public SceneSelectLevel(Context context, Game game) {
		this.context = context;
		this.game = game;
	}
	
	@Override
	public boolean isLoaded() {
		return loaded;
	}
	
	@Override
	public boolean load(GL10 gl) {
		loadEntities(gl);
		loaded = true;
		return true;
	}

	@Override
	public void update() {
		synchronized(scroll) {
			if (scroll.x != 0.0f || scroll.y != 0.0f) {
				if (DO_LOG) Log.d(TAG, "update scroll:" + scroll.x + "," + scroll.y);
				for (GameEntity entity : scrollableEntities) {
					SpatialComponent spatial = (SpatialComponent)entity.getComponent(SpatialComponent.class);
					if (spatial != null) {
						PointF pos = spatial.getPosition();
						// eat up y scroll
						pos.x -= scroll.x;
					}
					
					TouchComponent touch = (TouchComponent)entity.getComponent(TouchComponent.class);
					if (touch != null) {
						touch.offset(-scroll.x, 0.0f);
					}
				}
				
				scroll.set(0.0f, 0.0f);
			}
		}
		
		for (GameEntity entity : gameEntities) {
			AnimationComponent anim = (AnimationComponent)entity.getComponent(AnimationComponent.class);
			if (anim != null) {
				anim.update();
			}
		}
	}

	@Override
	public void draw(GL10 gl) {
		if (DO_LOG) Log.d(TAG, "draw");
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		
		RenderComponents renderComps = new RenderComponents();
		for (GameEntity entity : gameEntities) {
			if (getRenderComponents(entity, renderComps) == false) {
				continue;
			}
			
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, renderComps.spatial.getGLVertex());
			
			gl.glBindTexture(GL10.GL_TEXTURE_2D, renderComps.texture.getTextureId());
			
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, renderComps.texture.getTextureCoordinates());
			
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			
			gl.glScalef(1.0f, -1.0f, 1.0f);
			gl.glTranslatef(-WIDTH/2, -HEIGHT/2, 0);
			PointF pos = renderComps.spatial.getPosition();
			gl.glTranslatef(pos.x, pos.y, 0);
			
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glPopMatrix();
		}
		
	}
	
	private class RenderComponents {
		public SpatialComponent spatial;
		public RenderComponent texture;
	}
	private boolean getRenderComponents(GameEntity entity, RenderComponents comps) {
		comps.spatial = null;
		comps.texture = null;
		
		Object obj = entity.getComponent(SpatialComponent.class);
		if (obj == null) {
			return false;
		}
		comps.spatial = (SpatialComponent)obj;
		
		obj = entity.getComponent(RenderComponent.class);
		if (obj == null) {
			return false;
		}
		comps.texture = (RenderComponent)obj;
		return true;
	}
	
	private static final int LEVEL_COUNT = 5;
	private static final int LEVEL_SPRITE_WIDTH = 342;
	private static final int LEVEL_SPRITE_HEIGHT = 272;
	private static final int LEVEL_SPRITE_INIT_X = 195;
	private static final int LEVEL_SPRITE_Y = 61;
	private static final int LEVEL_SPRITE_STEP_X = 85;
	
	private void loadEntities(GL10 gl) {
		// load bg texture
		TextureLoader.Texture tex = TextureLoader.load(context, R.drawable.level_menu_bg);
		if (DO_LOG) Log.d(TAG, "setupSprites tex:" + tex);
		
		int[] texId = new int[1];
		gl.glGenTextures(1, texId, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texId[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, tex.bitmap, 0);
	    
	    RenderComponent.Texture renderTex = new RenderComponent.Texture(texId[0], tex.bitmap.getWidth(), tex.bitmap.getHeight(), tex.left, tex.top, tex.right, tex.bottom);
		RenderComponent texComp = new RenderComponent(renderTex);
		SpatialComponent spatial = new SpatialComponent(WIDTH, HEIGHT, 0.0f, 0.0f);
		GameEntity bg = new GameEntity();
		bg.addComponent(spatial);
		bg.addComponent(texComp);
		gameEntities.add(bg);
		
		// load map bg
		tex = TextureLoader.load(context, R.drawable.level_menu_map_bg);
		gl.glGenTextures(1, texId, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texId[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, tex.bitmap, 0);
		
		renderTex = new RenderComponent.Texture(texId[0], tex.bitmap.getWidth(), tex.bitmap.getHeight(), tex.left, tex.top, tex.right, tex.bottom);
		texComp = new RenderComponent(renderTex);
		spatial = new SpatialComponent(720, 177, 0, 303);
		GameEntity map = new GameEntity();
		map.addComponent(spatial);
		map.addComponent(texComp);
		gameEntities.add(map);
		
		// load level tex
		TextureLoader loader = new TextureLoader();
		loader.newBitmap(LEVEL_SPRITE_WIDTH * LEVEL_COUNT, LEVEL_SPRITE_HEIGHT);
		TextureLoader.Texture[] levelTexs = new TextureLoader.Texture[LEVEL_COUNT];
		levelTexs[0] = loader.loadIntoBitmap(context, R.drawable.level_menu_ep1, 0, 0);
		levelTexs[1] = loader.loadIntoBitmap(context, R.drawable.level_menu_ep2, 342, 0);
		levelTexs[2] = loader.loadIntoBitmap(context, R.drawable.level_menu_ep3, 342*2, 0);
		levelTexs[3] = loader.loadIntoBitmap(context, R.drawable.level_menu_ep4, 342*3, 0);
		levelTexs[4] = loader.loadIntoBitmap(context, R.drawable.level_menu_ep5, 342*4, 0);
		TextureLoader.Texture texBack = new TextureLoader.Texture();
		texBack = loader.loadIntoBitmap(context, R.drawable.level_menu_left_normal, 342*5, 0);
		
		gl.glGenTextures(1, texId, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texId[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
	    
	    Bitmap levelBitmap = loader.getBitmap();
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, levelBitmap, 0);
		
		for (int i = 0; i < LEVEL_COUNT; i++) {
			tex = levelTexs[i];
			renderTex = new RenderComponent.Texture(texId[0], tex.bitmap.getWidth(), tex.bitmap.getHeight(), tex.left, tex.top, tex.right, tex.bottom);
			texComp = new RenderComponent(renderTex);
			float left = LEVEL_SPRITE_INIT_X + (LEVEL_SPRITE_WIDTH + LEVEL_SPRITE_STEP_X) * i;
			float top = LEVEL_SPRITE_Y;
			float right = left + LEVEL_SPRITE_WIDTH;
			float bottom = top + LEVEL_SPRITE_HEIGHT;
			spatial = new SpatialComponent(LEVEL_SPRITE_WIDTH, LEVEL_SPRITE_HEIGHT, 
					left, top);
			GameEntity level = new GameEntity();
			level.addComponent(spatial);
			level.addComponent(texComp);
			level.addComponent(new TouchComponent(level, left, top, right, bottom));
			level.addComponent(new ButtonComponent(level));
			gameEntities.add(level);
			scrollableEntities.add(level);
		}
		
		GameEntity back = new GameEntity();
		renderTex = new RenderComponent.Texture(texId[0], tex.bitmap.getWidth(), tex.bitmap.getHeight(), texBack.left, texBack.top, texBack.right, texBack.bottom);
		back.addComponent(new RenderComponent(renderTex));
		back.addComponent(new SpatialComponent(99, 98, 9, 380));
		back.addComponent(new TouchComponent(back, 9,380, 9 + 99, 380 + 98));
		back.addComponent(new ButtonComponent(back));
		gameEntities.add(back);
		
		back.registerStateChangedListener(backListener);
		
		// car and level marks
		loader.newBitmap(1024, 1024);
		TextureLoader.Texture glTexCar1 = loader.loadIntoBitmap(context, R.drawable.level_menu_map_car_01, 0, 0);		// 102 X 69
		TextureLoader.Texture glTexCar2 = loader.loadIntoBitmap(context, R.drawable.level_menu_map_car_02, 102, 0);		// 102 X 69
		TextureLoader.Texture glLevelMark = loader.loadIntoBitmap(context, R.drawable.level_menu_map_mark, 102*2, 0);	// 38 X 38
		
		gl.glGenTextures(1, texId, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texId[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
	    
	    Bitmap bitmap = loader.getBitmap();
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, bitmap, 0);
		
		RenderComponent.Texture texCar1 = new RenderComponent.Texture(texId[0], glTexCar1.bitmap.getWidth(), glTexCar1.bitmap.getHeight(), glTexCar1.left, glTexCar1.top, glTexCar1.right, glTexCar1.bottom);
		RenderComponent.Texture texCar2 = new RenderComponent.Texture(texId[0], glTexCar2.bitmap.getWidth(), glTexCar2.bitmap.getHeight(), glTexCar2.left, glTexCar2.top, glTexCar2.right, glTexCar2.bottom);
		AnimationComponent animCar = new AnimationComponent(texCar1, true);
		animCar.addKeyFrame(texCar1, 250L);
		animCar.addKeyFrame(texCar2, 250L);
		animCar.startAnimation();	// start now!
		
		GameEntity car = new GameEntity();
		car.addComponent(animCar);
		car.addComponent(new SpatialComponent(102, 69, 214, 353));
		gameEntities.add(car);
		
		RenderComponent.Texture texMark = new RenderComponent.Texture(texId[0], glLevelMark.bitmap.getWidth(), glLevelMark.bitmap.getHeight(), glLevelMark.left, glLevelMark.top, glLevelMark.right, glLevelMark.bottom);
		RenderComponent rendMark = new RenderComponent(texMark);
		
		// mark1
		GameEntity mark = new GameEntity();
		mark.addComponent(rendMark);
		mark.addComponent(new SpatialComponent(38, 38, 124, 400));
		gameEntities.add(mark);
		
		// mark2
		mark = new GameEntity();
		mark.addComponent(rendMark);
		mark.addComponent(new SpatialComponent(38, 38, 261, 420));
		gameEntities.add(mark);
		
		// mark3
		mark = new GameEntity();
		mark.addComponent(rendMark);
		mark.addComponent(new SpatialComponent(38, 38, 396, 410));
		gameEntities.add(mark);
		
		// mark4
		mark = new GameEntity();
		mark.addComponent(rendMark);
		mark.addComponent(new SpatialComponent(38, 38, 528, 421));
		gameEntities.add(mark);
		
		// mark5
		mark = new GameEntity();
		mark.addComponent(rendMark);
		mark.addComponent(new SpatialComponent(38, 38, 645, 414));
		gameEntities.add(mark);
	}
	
	@Override
	public boolean onDown(float x, float y) {
		tempPt.set(x, y);
		if (projectScreenToWorld(tempPt) == false) {
			return true;
		}
		
		for (GameEntity entity : gameEntities) {
			Object comp = entity.getComponent(TouchComponent.class);
			if (comp != null) {
				TouchComponent touch = (TouchComponent)comp;
				if (touch.onDown(tempPt.x, tempPt.y)) {
					return true;
				}
			}
		}
		return true;
	}

	@Override
	public boolean onScroll(float x, float y, float dx, float dy) {
		tempPt.set(x, y);
		if (projectScreenToWorld(tempPt) == false) {
			return true;
		}
		
		synchronized(scroll) {
			scroll.set(dx, dy);
		}
		
		for (GameEntity entity : gameEntities) {
			Object comp = entity.getComponent(TouchComponent.class);
			if (comp != null) {
				TouchComponent touch = (TouchComponent)comp;
				if (touch.onScroll(tempPt.x, tempPt.y, dx, dy)) {
					return true;
				}
			}
		}
		return true;
	}

	@Override
	public boolean onTap(float x, float y) {
		tempPt.set(x, y);
		if (projectScreenToWorld(tempPt) == false) {
			return true;
		}
		
		for (GameEntity entity : gameEntities) {
			Object comp = entity.getComponent(TouchComponent.class);
			if (comp != null) {
				TouchComponent touch = (TouchComponent)comp;
				if (touch.onTap(tempPt.x, tempPt.y)) {
					return true;
				}
			}
		}
		return true;
	}
	
	@Override
	public boolean onUp(float x, float y) {
		tempPt.set(x, y);
		if (projectScreenToWorld(tempPt) == false) {
			return true;
		}
		
		for (GameEntity entity : gameEntities) {
			Object comp = entity.getComponent(TouchComponent.class);
			if (comp != null) {
				TouchComponent touch = (TouchComponent)comp;
				if (touch.onUp(tempPt.x, tempPt.y)) {
					return true;
				}
			}
		}
		return true;
	}
	
	private PointF tempPt = new PointF();
	private float surfaceWidth, surfaceHeight;
	private boolean projectScreenToWorld(PointF pt) {
		float margin = (surfaceWidth - surfaceHeight * WIDTH / HEIGHT) / 2.0f;
		if (pt.x < margin || (surfaceWidth - margin) < pt.x) {
			return false;
		}
		
		float y = pt.y * HEIGHT / surfaceHeight;
		float x = (pt.x - margin) * WIDTH / (surfaceWidth - 2*margin);
		pt.set(x, y);
		
		return true;
	}
	
	private GameEntity.StateChangedListener backListener = new GameEntity.StateChangedListener() {
		
		@Override
		public void onStateChanged(Integer newState) {
			if (newState == GameEntity.STATE_BUTTON_UP) {
				game.backScene();
			}
		}
	};
	
	@Override
	public void setSurfaceDimension(float width, float height) {
		surfaceWidth = width;
		surfaceHeight = height;
	}

}
