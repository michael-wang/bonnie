package com.studioirregular.bonniesbrunch.entity;

import android.graphics.PointF;
import android.graphics.RectF;

public interface CandyHolder {

	public void getCandyLocation(Candy candy, PointF loc);
	public RectF getHolderBox();
}
