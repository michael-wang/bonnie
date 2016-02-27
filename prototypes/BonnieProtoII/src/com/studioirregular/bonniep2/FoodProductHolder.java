package com.studioirregular.bonniep2;

import android.graphics.PointF;

public interface FoodProductHolder {

	public static enum Size {
		Normal,
		Medium,
		Candy
	}
	
	public Size getHolderSizeType();
	public void getFoodLocation(PointF topLeft);
	public void getBeverageLocation(PointF topLeft);
	public void getToastTopLocation(PointF loc);
	public void getCandyLocation(FoodComponent candy, PointF topLeft);
	public boolean isDraggable();
}
