package com.studioirregular.bonniep1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.graphics.PointF;

// use model coordinate system
public class SpatialComponent {

	@SuppressWarnings("unused")	// unused for now
	private PointF dimension;
	private FloatBuffer vertex;
	private PointF position;
	
	public SpatialComponent(float width, float height, float positionX, float positionY) {
		dimension = new PointF(width, height);
		position = new PointF(positionX, positionY);
		genVertex(width, height);
	}
	
	public FloatBuffer getGLVertex() {
		return vertex;
	}
	
	public PointF getPosition() {
		return position;
	}
	
	public void setPosition(float x, float y) {
		position.set(x, y);
	}
	
	private void genVertex(float width, float height) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 3 * 4);	// 4 (x,y,z) points, 3 components, each in float (4 bytes)
		vbb.order(ByteOrder.nativeOrder());
		vertex = vbb.asFloatBuffer();
		
		float[] coord = {
			0.0f, height, 0.0f,
			width, height, 0.0f,
			0.0f, 0.0f, 0.0f,
			width, 0.0f, 0.0f
		};
		
		vertex.put(coord);
		vertex.position(0);
	}
}
