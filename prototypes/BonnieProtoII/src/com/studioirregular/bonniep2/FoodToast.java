package com.studioirregular.bonniep2;

import com.studioirregular.bonniep2.FoodProductHolder.Size;

public interface FoodToast {

	public boolean isClosed();
	public void open();
	public void close();
	public String getBottomTextureId(Size size);
	public String getTopTextureId(Size size);
}
