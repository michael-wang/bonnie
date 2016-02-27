package com.studioirregular.bonniep2;

import java.util.HashMap;
import java.util.Map;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

public class DraggableComponent implements Component, OnTouchListener {

	private static final boolean DO_LOG = false;
	private static final String TAG = "draggable-component";
	
	private BasicEntity entity;
	private String id;
	private RectF box = new RectF();
	private RectF movingBox = new RectF();
	private boolean isDownOnMe = false;
	private EventHost eventHost;
	
	private PointF downPoint;
	private Map<GLRenderable, PointF> clientOldPoints = new HashMap<GLRenderable, PointF>();
	
	public DraggableComponent(BasicEntity entity, String id, EventHost host) {
		if (DO_LOG) Log.d(TAG, "DraggableComponent entity:" + entity.id + ", id:" + id);
		this.entity = entity;
		this.id = id;
		this.box = new RectF();
		this.eventHost = host;
	}
	
	public void addTouchableArea(GLRenderable render) {
		box.union(render.getX(), render.getY(),
				render.getX() + render.getWidth(),
				render.getY() + render.getHeight());
		if (DO_LOG) Log.d(TAG, "addTouchableArea box:" + box);
	}
	
	public void addTouchableArea(RectF area) {
		box.union(area);
		if (DO_LOG) Log.d(TAG, "addTouchableArea box:" + box);
	}
	
	public void cleanTouchableArea() {
		box.setEmpty();
	}
	
	public void dropRejected() {
		bounceBack();
	}
	
	@Override
	public boolean onTouch(MotionEvent event) {
		final int action = event.getAction();
		
		boolean consumed = false;
		
		CoordinateConverter converter = CoordinateConverter.getInstance();
		final float x = converter.convertX(event.getX());
		final float y = converter.convertY(event.getY());
		
		if (DO_LOG) Log.d(TAG, "onTouch action" + action + ", x:" + x + ", y:" + y + ", box:" + box);
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (box.contains(x, y)) {
				isDownOnMe = true;
				downPoint = new PointF(x, y);
				getClientsPoint();
				movingBox.set(box);
				consumed = true;
			} else {
				if (DO_LOG) Log.w(TAG, "onTouch ACTION_DOWN x:" + x + ",y:" + y + ", out of box:" + box);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (isDownOnMe) {
				move(x - downPoint.x, y - downPoint.y);
				consumed = true;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			if (isDownOnMe) {
				isDownOnMe = false;
				bounceBack();
				clientOldPoints.clear();
				consumed = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (isDownOnMe) {
				isDownOnMe = false;
				
				eventHost.send(this, new ComponentEvent(ComponentEvent.DRAGGABLE_DROPT, id));
				consumed = true;
			}
			break;
		}
		
		return consumed;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	private void getClientsPoint() {
		if (DO_LOG) Log.d(TAG, "getClientsPoint #render:" + entity.getRenderableCount());
		for (int i = 0; i < entity.getRenderableCount(); i++) {
			GLRenderable render = entity.getRenderable(i);
			PointF point = new PointF(render.getX(), render.getY());
			clientOldPoints.put(render, point);
			if (DO_LOG) Log.d(TAG, "getClientsPoint i:" + i + ", x:" + point.x + ", y:" + point.y);
		}
	}
	
	private void move(float dx, float dy) {
		movingBox.set(box.left + dx, box.top + dy, box.right + dx, box.bottom + dy);
		if (DO_LOG) Log.d(TAG, "move dx:" + dx + ", dy:" + dy + ", box:" + box + ", movingBox:" + movingBox);
		
		for (int i = 0; i < entity.getRenderableCount(); i++) {
			GLRenderable render = entity.getRenderable(i);
			PointF oldPoint = clientOldPoints.get(render);
			if (oldPoint != null) {
				if (DO_LOG) Log.d(TAG, "render oldPoint:" + oldPoint + ", new point:" + render.getTranslateX() + "," + render.getTranslateY());
				render.setX(oldPoint.x + dx);
				render.setY(oldPoint.y + dy);
			}
		}
	}
	
	private void bounceBack() {
		GLRenderable render = entity.getRenderable(0);
		if (render == null) {
			return;
		}
		
		PointF oldPoint = clientOldPoints.get(render);
		if (oldPoint == null) {
			return;
		}
		
		float dx = oldPoint.x - render.getX();
		float dy = oldPoint.y - render.getY();
		if (DO_LOG) Log.d(TAG, "bounceBack dx:" + dx + ", dy:" + dy);
		
		float fromX = render.getX();
		float toX = fromX + dx;
		float fromY = render.getY();
		float toY = fromY + dy;
		if (DO_LOG) Log.d(TAG, "bounceBack fromX:" + fromX + ", toX:" + toX);
		if (DO_LOG) Log.d(TAG, "bounceBack fromY:" + fromY + ", toY:" + toY);
		
		TranslateAnimationComponent bounceBack = new TranslateAnimationComponent(entity, eventHost, "bounceBack", fromX, toX, fromY, toY, 500, false);
		bounceBack.start();
		entity.add(bounceBack);
	}

}
