package com.studioirregular.bonniesbrunch;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.RectF;
import android.util.Log;

import com.studioirregular.bonniesbrunch.TextureSystem.Texture;
import com.studioirregular.bonniesbrunch.TextureSystem.TexturePartition;
import com.studioirregular.bonniesbrunch.base.ObjectArray;
import com.studioirregular.bonniesbrunch.base.ObjectArray.OutOfCapacityException;
import com.studioirregular.bonniesbrunch.base.ObjectBase;

public class RenderSystem extends ObjectBase {

	private static final String TAG = "render-system";
	
	// singleton
	public static RenderSystem getInstance() {
		if (sInstance == null) {
			sInstance = new RenderSystem();
		}
		return sInstance;
	}
	
	public static void releaseInstance() {
		if (sInstance != null) {
			sInstance = null;
		}
	}
	
	private static RenderSystem sInstance = null;
	private RenderSystem() {
		drawQueue = new ObjectArray[QUEUE_COUNT];
		for (int i = 0; i < QUEUE_COUNT; i++) {
			drawQueue[i] = new ObjectArray<RenderObject>(256);	// TODO: find best value.
		}
		index = 0;
	}
	
	public static final int QUEUE_COUNT = 2;
	public ObjectArray<RenderObject>[] drawQueue;
	public int index;	// into drawQueue
	Context context;
	
	public void setup(Context context) {
		this.context = context;
	}
	
	@Override
	public void reset() {
		for (int i = 0; i < QUEUE_COUNT; i++) {
			drawQueue[i].clear();
		}
		index = 0;
	}
	
	public void scheduleRenderObject(RenderObject obj) {
		try {
			drawQueue[index].add(obj);
		} catch (OutOfCapacityException e) {
			Log.e(TAG, "scheduleRenderObject: " + e);
		}
	}
	
	public void swap(GameRenderer renderer) {
		renderer.setDrawQueue(drawQueue[index]);
		
		int lastIndex = (index == 0) ? (QUEUE_COUNT - 1) : index - 1;
		drawQueue[lastIndex].clear();
		
		index = (index + 1) % QUEUE_COUNT;
	}
	
	public static class RenderObject extends ObjectBase {
		public RenderObject(float w, float h) {
			this.w = w;
			this.h = h;
			
			ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 3 * 4);
			vbb.order(ByteOrder.nativeOrder());
			vertex = vbb.asFloatBuffer();
			setupVertex();
		}
		
		public RenderObject(RenderObject other) {
			this.w = other.w;
			this.h = other.h;
			this.x = other.x;
			this.y = other.y;
			
			vertex.put(other.vertex);
		}
		
		private void setupVertex() {
			float[] vertices = {
				-w/2, h/2, 0,
				w/2,  h/2, 0,
				-w/2, -h/2, 0,
				w/2,  -h/2, 0
			};
			vertex.put(vertices);
			vertex.position(0);
		}
		
		public void setPosition(float x, float y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public void reset() {
			x = y = 0;
			w = h = 0;
			vertex.clear();
			
			drawColor = false;
			colorR = colorG = colorB = alpha = 0.0f;
		}
		
		public void setColor(float r, float g, float b, float a) {
			if (Config.DEBUG_LOG) Log.d(TAG, "setColor r:" + r + ",g:" + g + ",b:" + b + ",a:" + a);
			
			colorR = r;
			colorG = g;
			colorB = b;
			alpha = a;
			drawColor = true;
		}
		
		public void setTexture(Texture texture, TexturePartition partition) {
			if (Config.DEBUG_LOG) Log.d(TAG, "setTexture texture id:" + texture.id + ",partition:" + partition.id);
			
			this.texture = texture;
			this.texturePartition = partition;
			
			if (partition.load) {
				setupTextureCoordinate(texture, partition);
			}
			
			drawTexture = true;
		}
		
		public void changeWidth(float newWidth) {
			w = newWidth;
			
			final float halfWidth = newWidth/2;
			vertex.put(0, -halfWidth);
			vertex.put(3, halfWidth);
			vertex.put(6, -halfWidth);
			vertex.put(9, halfWidth);
		}
		
		public void offsetTextureX(boolean fromStart, float offset) {
			if (!drawTexture || textureCoord == null) {
				return;
			}
			
			final RectF box = texturePartition.getBox();
			if (fromStart) {
				final float newX0 = (box.left + offset) / texture.width;
				textureCoord.put(0, newX0);
				textureCoord.put(4, newX0);
			} else {
				final float newX1 = (box.right + offset) / texture.width;
				textureCoord.put(2, newX1);
				textureCoord.put(6, newX1);
			}
		}
		
		public void changeHeight(float newHeight) {
			h = newHeight;
			
			final float halfHeight = newHeight/2;
			vertex.put(1, halfHeight);
			vertex.put(4, halfHeight);
			vertex.put(7, -halfHeight);
			vertex.put(10, -halfHeight);
		}
		
		public void offsetTextureY(boolean fromStart, float offset) {
			if (!drawTexture || textureCoord == null) {
				return;
			}
			
			final RectF box = texturePartition.getBox();
			if (fromStart) {
				final float newY0 = (box.top + offset) / texture.height;
				textureCoord.put(5, newY0);
				textureCoord.put(7, newY0);
			} else {
				final float newY1 = (box.bottom + offset) / texture.height;
				textureCoord.put(1, newY1);
				textureCoord.put(3, newY1);
			}
		}
		
		public void changeScale(float scale) {
			this.scale = scale;
		}
		
		private void setupTextureCoordinate(Texture texture, TexturePartition partition) {
			ByteBuffer tbb = ByteBuffer.allocateDirect(2 * 4 * 4);
			tbb.order(ByteOrder.nativeOrder());
			textureCoord = tbb.asFloatBuffer();
			
			final RectF box = partition.getBox();
			final float left = (box.left + 0.5f) / texture.width;
			final float top = (box.top + 0.5f) / texture.height;
			final float right = (box.right - 1.0f) / texture.width;
			final float bottom = (box.bottom - 1.0f) / texture.height;
			
			float[] texCoord = { 
				left, bottom, 
				right, bottom, 
				left, top, 
				right, top
			};
			textureCoord.put(texCoord);
			textureCoord.position(0);
		}
		
		public void draw(GL10 gl) {
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex);
			
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			
			gl.glTranslatef(x + w/2, y + h/2, 0);
			gl.glScalef(scale, scale, 1.0f);
			
			if (drawColor) {
				gl.glColor4f(colorR, colorG, colorB, alpha);
			}
			
			if (drawTexture) {
				if (texturePartition.load) {
					if (textureCoord == null) {
						setupTextureCoordinate(TextureSystem.getInstance().getTexture(texturePartition.textureId), texturePartition);
					}
					gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
					gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureCoord);
					gl.glBindTexture(GL10.GL_TEXTURE_2D, texturePartition.glName);
				} else {
					if (Config.DEBUG_LOG) Log.w(TAG, "texture partition not loaded:" + texturePartition);
					if (!drawColor) {
						gl.glColor4f(0, 0, 0, 0.0f);
					}
				}
			} else {
				gl.glDisable(GL10.GL_TEXTURE_2D);
			}
			
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			
			if (drawTexture) {
				gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			} else {
				gl.glEnable(GL10.GL_TEXTURE_2D);
			}
			
			if (drawColor) {
				gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			}
			
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glPopMatrix();
		}
		
		FloatBuffer vertex;
		float x, y;
		float w, h;
		float scale = 1.0f;
		
		boolean drawColor = false;
		float colorR, colorG, colorB, alpha;
		
		boolean drawTexture = false;
		FloatBuffer textureCoord;
		Texture texture;
		TexturePartition texturePartition;
	}

}
