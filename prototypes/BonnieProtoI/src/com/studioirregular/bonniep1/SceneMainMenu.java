package com.studioirregular.bonniep1;

import java.util.LinkedList;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.opengl.GLUtils;
import android.util.Log;

public class SceneMainMenu implements Scene, UserInput {

	private static final String TAG = "scene-main_menu";
	private static final boolean DO_LOG = false;
	
	private Context context;
	private Game game;
	private boolean loaded = false;
	private LinkedList<GameEntity> gameEntities = new LinkedList<GameEntity>();
	
	public SceneMainMenu(Context context, Game game) {
		this.context = context;
		this.game = game;
	}
	
	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public boolean load(GL10 gl) {
		loaded = loadEntities(gl);
		if (loaded) {
			game.onSceneReady();
		}
		return loaded;
	}

	@Override
	public void update() {
		for (GameEntity entity : gameEntities) {
			AnimationComponent anim = (AnimationComponent)entity.getComponent(AnimationComponent.class);
			if (anim != null) {
				anim.update();
			}
		}
	}

	@Override
	public void draw(GL10 gl) {
		if (!loaded) {
			return;
		}
		
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
	
	private boolean loadEntities(GL10 gl) {
		// load bg texture
		TextureLoader.Texture tex = TextureLoader.load(context, R.drawable.main_menu_bg);
		
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
		RenderComponent render = new RenderComponent(renderTex);
		SpatialComponent spatial = new SpatialComponent(WIDTH, HEIGHT, 0.0f, 0.0f);
		GameEntity bg = new GameEntity();
		bg.addComponent(spatial);
		bg.addComponent(render);
		gameEntities.add(bg);
		
		// menu items + audio controls
		TextureLoader loader = new TextureLoader();
		loader.newBitmap(1024, 1024);
		TextureLoader.Texture[] menuTexs = new TextureLoader.Texture[4];
		menuTexs[0] = loader.loadIntoBitmap(context, R.drawable.main_menu_bt_play, 0, 0);		// 228 X 63
		menuTexs[1] = loader.loadIntoBitmap(context, R.drawable.main_menu_bt_help, 228, 0);
		menuTexs[2] = loader.loadIntoBitmap(context, R.drawable.main_menu_bt_credit, 228*2, 0);
		menuTexs[3] = loader.loadIntoBitmap(context, R.drawable.main_menu_bt_buy, 228*3, 0);
		TextureLoader.Texture[] menuDownTexs = new TextureLoader.Texture[4];
		menuDownTexs[0] = loader.loadIntoBitmap(context, R.drawable.main_menu_bt_play_pressed, 0, 63);
		menuDownTexs[1] = loader.loadIntoBitmap(context, R.drawable.main_menu_bt_help_pressed, 228, 63);
		menuDownTexs[2] = loader.loadIntoBitmap(context, R.drawable.main_menu_bt_credit_pressed, 228*2, 63);
		menuDownTexs[3] = null;
		TextureLoader.Texture texSoundEnable = loader.loadIntoBitmap(context, R.drawable.main_menu_bt_sound_enable, 0, 63*2);	// 71 X 72
		TextureLoader.Texture texSoundDisable = loader.loadIntoBitmap(context, R.drawable.main_menu_bt_sound_disable, 71, 63*2);
		TextureLoader.Texture texMusicEnable = loader.loadIntoBitmap(context, R.drawable.main_menu_bt_music_enable, 71*2, 63*2);	// 71 X 72
		TextureLoader.Texture texMusicDisable = loader.loadIntoBitmap(context, R.drawable.main_menu_bt_music_disable, 71*3, 63*2);
		TextureLoader.Texture texBonnie1 = loader.loadIntoBitmap(context, R.drawable.main_menu_bonnie_01, 71*4, 63*2);	// 123 X 63
		TextureLoader.Texture texBonnie2 = loader.loadIntoBitmap(context, R.drawable.main_menu_bonnie_02, 71*4 + 123, 63*2);
		
		gl.glGenTextures(1, texId, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texId[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
	    
	    Bitmap texBitmap = loader.getBitmap();
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, texBitmap, 0);
		
		for (int i = 0; i < 4; i++) {
			GameEntity menu = buildMenu(228, 63, 492, 63 + 70*i,
					texId[0], texBitmap, menuTexs[i], menuDownTexs[i]);
			gameEntities.add(menu);
			
			if (i == 0) {
				// inject touch listener to detect user tap play menu
				menu.registerStateChangedListener(playListener);
			}
		}
		
		GameEntity sound = new GameEntity();
		sound.addComponent(new SpatialComponent(71, 72, 525, 359));
		RenderComponent.Texture rendEnable = new RenderComponent.Texture(texId[0], texBitmap.getWidth(), texBitmap.getHeight(), texSoundEnable.left, texSoundEnable.top, texSoundEnable.right, texSoundEnable.bottom);
		RenderComponent.Texture rendDisable   = new RenderComponent.Texture(texId[0], texBitmap.getWidth(), texBitmap.getHeight(), texSoundDisable.left, texSoundDisable.top, texSoundDisable.right, texSoundDisable.bottom);
		StatfulRenderComponent stateRender = new StatfulRenderComponent(rendEnable, GameEntity.STATE_BUTTON_DOWN);
		stateRender.addState(GameEntity.STATE_BUTTON_UP, rendDisable);
		stateRender.addState(GameEntity.STATE_BUTTON_DOWN, rendEnable);
		sound.addComponent(stateRender);
		sound.addComponent(new TouchComponent(sound, 525, 359, 525 + 71, 359 + 72));
		sound.addComponent(new ToggleButtonComponent(sound, GameEntity.STATE_BUTTON_DOWN));
		gameEntities.add(sound);
		
		GameEntity music = new GameEntity();
		music.addComponent(new SpatialComponent(71, 72, 614, 359));
		rendEnable = new RenderComponent.Texture(texId[0], texBitmap.getWidth(), texBitmap.getHeight(), texMusicEnable.left, texMusicEnable.top, texMusicEnable.right, texMusicEnable.bottom);
		rendDisable   = new RenderComponent.Texture(texId[0], texBitmap.getWidth(), texBitmap.getHeight(), texMusicDisable.left, texMusicDisable.top, texMusicDisable.right, texMusicDisable.bottom);
		stateRender = new StatfulRenderComponent(rendEnable, GameEntity.STATE_BUTTON_DOWN);
		stateRender.addState(GameEntity.STATE_BUTTON_UP, rendDisable);
		stateRender.addState(GameEntity.STATE_BUTTON_DOWN, rendEnable);
		music.addComponent(stateRender);
		music.addComponent(new TouchComponent(music, 614, 359, 614 + 71, 359 + 72));
		music.addComponent(new ToggleButtonComponent(music, GameEntity.STATE_BUTTON_DOWN));
		gameEntities.add(music);
		
		music.registerStateChangedListener(musicListener);
		
		// Bonnie blink!
		GameEntity blink = new GameEntity();
		RenderComponent.Texture rendBonnie1 = new RenderComponent.Texture(texId[0], texBitmap.getWidth(), texBitmap.getHeight(), texBonnie1.left, texBonnie1.top, texBonnie1.right, texBonnie1.bottom);
		RenderComponent.Texture rendBonnie2 = new RenderComponent.Texture(texId[0], texBitmap.getWidth(), texBitmap.getHeight(), texBonnie2.left, texBonnie2.top, texBonnie2.right, texBonnie2.bottom);
		AnimationComponent anim = new AnimationComponent(rendBonnie1, true);
		anim.addKeyFrame(rendBonnie1, 200L);
		anim.addKeyFrame(rendBonnie2, 100L);
		anim.addKeyFrame(rendBonnie1,  50L);
		anim.addKeyFrame(rendBonnie2, 100L);
		anim.addKeyFrame(rendBonnie1, 3000L);
		anim.startAnimation();
		blink.addComponent(anim);
		blink.addComponent(new SpatialComponent(123, 63, 79, 114));
		gameEntities.add(blink);
		
		return true;
	}
	
	private GameEntity buildMenu(float width, float height, float x, float y, 
			int texId, Bitmap texBitmap, TextureLoader.Texture texNormal, TextureLoader.Texture texDown) {
		GameEntity menu = new GameEntity();
		menu.addComponent(new SpatialComponent(width, height, x, y));
		if (texDown != null) {
			RenderComponent.Texture rendUp = new RenderComponent.Texture(texId, texBitmap.getWidth(), texBitmap.getHeight(), texNormal.left, texNormal.top, texNormal.right, texNormal.bottom);
			RenderComponent.Texture rendDown = new RenderComponent.Texture(texId, texBitmap.getWidth(), texBitmap.getHeight(), texDown.left, texDown.top, texDown.right, texDown.bottom);
			StatfulRenderComponent render = new StatfulRenderComponent(rendUp, GameEntity.STATE_BUTTON_UP);
			render.addState(GameEntity.STATE_BUTTON_UP, rendUp);
			render.addState(GameEntity.STATE_BUTTON_DOWN, rendDown);
			render.addState(GameEntity.STATE_TOUCH_CANCEL, rendUp);
			menu.addComponent(render);
			
			menu.addComponent(new TouchComponent(menu, x, y, x + width, y + height));
			menu.addComponent(new ButtonComponent(menu));
		} else {
			RenderComponent.Texture rendUp = new RenderComponent.Texture(texId, texBitmap.getWidth(), texBitmap.getHeight(), texNormal.left, texNormal.top, texNormal.right, texNormal.bottom);
			RenderComponent render = new RenderComponent(rendUp);
			menu.addComponent(render);
			menu.addComponent(new TouchComponent(menu, x, y, x + width, y + height));
			menu.addComponent(new ButtonComponent(menu));
		}
		return menu;
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
					if (DO_LOG) Log.d(TAG, "onDown touch:" + touch);
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
		
		for (GameEntity entity : gameEntities) {
			Object comp = entity.getComponent(TouchComponent.class);
			if (comp != null) {
				TouchComponent touch = (TouchComponent)comp;
				touch.onScroll(tempPt.x, tempPt.y, dx, dy);
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
	
	private GameEntity.StateChangedListener playListener = new GameEntity.StateChangedListener() {
		
		@Override
		public void onStateChanged(Integer newState) {
			if (DO_LOG) Log.d(TAG, "playListener newState:" + newState);
			if (newState == GameEntity.STATE_BUTTON_UP) {
				game.nextScene();
			}
		}
	};
	
	private GameEntity.StateChangedListener musicListener = new GameEntity.StateChangedListener() {
		@Override
		public void onStateChanged(Integer newState) {
			if (DO_LOG) Log.d(TAG, "musicListener newState:" + newState);
			if (newState == GameEntity.STATE_BUTTON_UP) {
				game.music(false);
			} else if (newState == GameEntity.STATE_BUTTON_DOWN) {
				game.music(true);
			}
		}
	};

	@Override
	public void setSurfaceDimension(float width, float height) {
		Log.d(TAG, "setViewDimension width:" + width + ",height:" + height);
		surfaceWidth = width;
		surfaceHeight = height;
	}
}
