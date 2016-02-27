package com.studioirregular.bonniesbrunch.entity;

import android.graphics.PointF;

public interface BrunchHolder {

	public static enum Size {
		Normal,
		Medium,
		Candy
	}
	
	public Size getHolderSizeType();
	public void getFoodLocation(PointF loc);
	public void getBeverageLocation(PointF loc);
	public void getOpenToastLocation(PointF loc);
	public boolean isDraggable();
}
