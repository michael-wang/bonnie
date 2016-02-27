package com.studioirregular.bonniesbrunch.entity;

import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.component.ButtonComponent;
import com.studioirregular.bonniesbrunch.component.RenderComponent;
import com.studioirregular.bonniesbrunch.entity.CustomerManager.CustomerSpec;

public class CustomerButton extends GameEntity {

	public CustomerButton(int zOrder) {
		super(zOrder);
	}
	
	@Override
	public void setup(float x, float y, float width, float height) {
		super.setup(x, y, width, height);
		
		ButtonComponent button = new ButtonComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
		button.setup(x, y, width, height);
		add(button);
	}
	
	public void setup(CustomerManager host, CustomerSpec trigger) {
		this.host= host;
		this.trigger = trigger;
		
		RenderComponent bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		bg.setup(box.width(), box.height());
		bg.setup(new float[] {1.0f, 1.0f, 1.0f, 1.0f});
		add(bg);
		
		RenderComponent render = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		render.setup(box.width(), box.height());
		render.setup(getCustomerTexture(trigger.type));
		add(render);
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		if (event.what == GameEvent.BUTTON_UP) {
			host.requestAddCustomer(trigger);
		}
	}
	
	private String getCustomerTexture(Customer.Type type) {
		if (Customer.Type.JOGGING_GIRL == type) {
			return "game_guest_01_w1_normal_1";
		} else if (Customer.Type.WORKING_MAN == type) {
			return "game_guest_02_w1_normal_1";
		} else if (Customer.Type.BALLOON_BOY == type) {
			return "game_guest_04_w1_normal_1";
		} else if (Customer.Type.GIRL_WITH_DOG == type) {
			return "game_guest_05_w1_normal_1";
		} else if (Customer.Type.GRANNY == type) {
			return "game_guest_11_w1_normal_1";
		} else if (Customer.Type.SUPERSTAR_MAN == type) {
			return "game_guest_12_w1_normal_1";
		} else if (Customer.Type.PHYSICIST == type) {
			return "game_guest_08_w1_normal_1";
		} else if (Customer.Type.SUPERSTAR_LADY == type) {
			return "game_guest_07_w1_normal_1";
		} else if (Customer.Type.FOOD_CRITIC == type) {
			return "game_guest_06_w1_normal_2";
		} else if (Customer.Type.TRAMP == type) {
			return "game_guest_09_w1_normal_1";
		}
		return "";
	}
	
	private CustomerManager host;
	private CustomerSpec trigger;

}
