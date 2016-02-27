package com.studioirregular.bonniesbrunch.command;

import com.studioirregular.bonniesbrunch.entity.ButtonEntity;
import com.studioirregular.bonniesbrunch.main.SceneManager;

public class AddButtonEntity implements Command {

	public AddButtonEntity(SceneManager manager, String entityName, int zOrder,
			float x, float y, float w, float h, String normalTexture,
			String downTexture, int gameEventWhenPressed) {
		this.manager = manager;
		this.entityName = entityName;
		this.zOrder = zOrder;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.normal = normalTexture;
		this.down = downTexture;
		this.gameEventWhenPressed = gameEventWhenPressed;
	}
	
	public void offsetTouchArea(float offsetLeft, float offsetTop, float offsetRight, float offsetBottom) {
		this.offsetTouchLeft = offsetLeft;
		this.offsetTouchTop = offsetTop;
		this.offsetTouchRight = offsetRight;
		this.offsetTouchBottom = offsetBottom;
	}
	
	@Override
	public void execute() {
		ButtonEntity.Builder builder = new ButtonEntity.Builder(x, y, w, h, normal, down);
		builder.emitEventWhenPressed(gameEventWhenPressed);
		builder.playSoundWhenClicked("mainmenu_bt_s1");
		builder.offsetTouchArea(offsetTouchLeft, offsetTouchTop, offsetTouchRight, offsetTouchBottom);
		
		ButtonEntity button = new ButtonEntity(zOrder, builder);
		manager.add(entityName, button);
	}
	
	private SceneManager manager;
	private String entityName;
	private int zOrder;
	private float x, y, w, h;
	private String normal, down;
	private int gameEventWhenPressed;
	
	private float offsetTouchLeft, offsetTouchTop, offsetTouchRight, offsetTouchBottom;

}
