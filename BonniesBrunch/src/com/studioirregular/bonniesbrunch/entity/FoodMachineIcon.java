package com.studioirregular.bonniesbrunch.entity;

import com.studioirregular.bonniesbrunch.FoodSystem.Food;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.SoundSystem;
import com.studioirregular.bonniesbrunch.SoundSystem.Sound;
import com.studioirregular.bonniesbrunch.component.Animation;
import com.studioirregular.bonniesbrunch.component.AnimationSet;
import com.studioirregular.bonniesbrunch.component.ButtonComponent;
import com.studioirregular.bonniesbrunch.component.FoodComponent;
import com.studioirregular.bonniesbrunch.component.FrameAnimationComponent;
import com.studioirregular.bonniesbrunch.component.RenderComponent;
import com.studioirregular.bonniesbrunch.component.ScaleAnimation;
import com.studioirregular.bonniesbrunch.entity.BrunchHolder.Size;
import com.studioirregular.bonniesbrunch.entity.FoodMachine.CookSlot;
import com.studioirregular.bonniesbrunch.entity.FoodMachine.CookSlot.State;

public class FoodMachineIcon extends GameEntity {

	public enum Type {
		Muffin,
		Toast,
		FryingPan
	};
	
	public FoodMachineIcon(int zOrder) {
		super(zOrder);
	}
	
	public void setup(Type type, int level) {
		this.type = type;
		this.level = level;
		setupComponents();
		
		if (Type.Muffin == type) {
			machineWorkingSound = SoundSystem.getInstance().load("game_table_waffle_work_s1");
		} else if (Type.Toast == type) {
			machineWorkingSound = SoundSystem.getInstance().load("game_table_toast_work_s1");
		} else if (Type.FryingPan == type) {
			machineWorkingSound = SoundSystem.getInstance().load("game_table_pan_work_s1");
		}
		machineDoneSound = SoundSystem.getInstance().load("game_table_machine_timeup_s1");
		
		clickAnimation = new AnimationSet(MIN_GAME_COMPONENT_Z_ORDER);
		Animation scaleUp = new ScaleAnimation(MIN_GAME_COMPONENT_Z_ORDER, 1.0f, 1.25f, 150);
		Animation scaleDown = new ScaleAnimation(MIN_GAME_COMPONENT_Z_ORDER, 1.25f, 1.0f, 150);
		((AnimationSet)clickAnimation).addAnimation(scaleUp);
		((AnimationSet)clickAnimation).addAnimation(scaleDown);
		clickAnimation.setLoop(false);
		clickAnimation.setFillBefore(true);
		clickAnimation.setFillAfter(true);
		add(clickAnimation);
	}
	
	public void notifyCookSlotStateChanged(FoodMachine machine, CookSlot slot) {
		final CookSlot.State newState = slot.getState();
		if (newState == State.COOKING) {
			if (!cooking) {
				turnOn();
			}
			updateFoodIconWhenStartCooking(slot.getFoodType());
		} else if (newState == State.DONE) {
			assert cooking;
			if (machine.cooking == false) {
				turnOff();
			}
			updateFoodIconWhenDoneCooking(slot.getFoodType());
		} else if (newState == State.EMPTY) {
			if (machine.cooking == false) {
				CookSlot doneSlot = machine.findNextDoneCookingSlot();
				int newFoodType = Food.INVALID_TYPE;
				if (doneSlot != null) {
					newFoodType = doneSlot.getFoodType();
				}
				updateNewFoodIcon(newFoodType);
			}
		}
	}
	
	private void updateFoodIconWhenStartCooking(int newFoodType) {
		if (type == Type.Muffin) {
			if (foodIcon != null) {
				remove(foodIcon);
				foodIcon = null;
			}
		} else if (type == Type.FryingPan) {
			if (foodIcon != null && foodIcon.getFoodType() != newFoodType) {
				remove(foodIcon);
				foodIcon = null;
			}
			
			if (foodIcon == null) {
				foodIcon = createFoodComponent(newFoodType);
				add(foodIcon);
			}
		}
	}
	
	private void updateFoodIconWhenDoneCooking(int newFoodType) {
		if (type == Type.Muffin) {
			// show foodIcon if turn off
			if (!cooking) {
				assert foodIcon == null;
				foodIcon = createFoodComponent(newFoodType);
				add(foodIcon);
			}
		} else if (type == Type.FryingPan) {
			assert foodIcon != null;
			assert foodIcon.getFoodType() == newFoodType;
		}
	}
	
	private void updateNewFoodIcon(int newFoodType) {
		if (foodIcon != null && foodIcon.getFoodType() != newFoodType) {
			remove(foodIcon);
			foodIcon = null;
		}
		
		if (newFoodType != Food.INVALID_TYPE && foodIcon == null) {
			foodIcon = createFoodComponent(newFoodType);
			add(foodIcon);
		}
	}
	
	public void turnOn() {
		cooking = true;
		updateState();
		
		if (machineWorkingSound != null) {
			workingSoundStreamId = SoundSystem.getInstance().playSound(machineWorkingSound, true);
		}
	}
	
	public void turnOff() {
		cooking = false;
		updateState();
		
		SoundSystem.getInstance().stopSound(workingSoundStreamId);
		SoundSystem.getInstance().playSound(machineDoneSound, false);
	}
	
	private FoodComponent createFoodComponent(int foodType) {
		FoodComponent result = new FoodComponent(MIN_GAME_COMPONENT_Z_ORDER + 1);
		if (type == Type.Muffin) {
			result.setup(foodType, Size.Medium);
			
			final float dx = (box.width() - result.width) / 2;
			final float dy = box.height() - result.height - 18;	// ugly, but works here.
			result.setupOffset(dx, dy);
		} else if (type == Type.FryingPan) {
			result.setup(foodType, Size.Normal);
			
			final float dx = (box.width() - result.width) / 2;
			final float dy = box.height() - result.height - 38;	// ugly, but works here.
			result.setupOffset(dx, dy);
		}
		return result;
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		if (!disable) {
			if (event.what == GameEvent.BUTTON_UP) {
				GameEventSystem.scheduleEvent(GameEvent.FOOD_MACHINE_SHOW, type.ordinal());
				clickAnimation.start();
			}
		}
	}
	
	private void setupComponents() {
		renderNormal = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		cookingAnimation = new FrameAnimationComponent(MIN_GAME_COMPONENT_Z_ORDER);
		if (type == Type.FryingPan) {
			smokeAnimation = new FrameAnimationComponent(MIN_GAME_COMPONENT_Z_ORDER + 2);
		}
		ButtonComponent button = new ButtonComponent(MIN_GAME_COMPONENT_Z_ORDER + 0x10);
		
		if (type == Type.Muffin) {
			setup(201, 228, 92, 135);
			button.setup(201, 228, 92, 135);
			
			renderNormal.setup(92, 135);
			cookingAnimation.setLoop(true);
			if (level == 1) {
				renderNormal.setup("game_table_waffle_lv1_normal");
				cookingAnimation.addFrame("game_table_waffle_lv1_work_1", 92, 135, COOK_ANIMATION_FRAME_DURATION);
				cookingAnimation.addFrame("game_table_waffle_lv1_work_2", 92, 135, COOK_ANIMATION_FRAME_DURATION);
			} else if (level == 2) {
				renderNormal.setup("game_table_waffle_lv2_normal");
				cookingAnimation.addFrame("game_table_waffle_lv2_work_1", 92, 135, COOK_ANIMATION_FRAME_DURATION);
				cookingAnimation.addFrame("game_table_waffle_lv2_work_2", 92, 135, COOK_ANIMATION_FRAME_DURATION);
			}
		} else if (type == Type.Toast){
			setup(293, 233, 116, 123);
			button.setup(293, 233, 116, 123);
			
			renderNormal.setup(116, 123);
			cookingAnimation.setLoop(true);
			if (level == 1) {
				renderNormal.setup("game_table_toast_lv1_normal");
				cookingAnimation.addFrame("game_table_toast_lv1_work_1", 116, 123, COOK_ANIMATION_FRAME_DURATION);
				cookingAnimation.addFrame("game_table_toast_lv1_work_2", 116, 123, COOK_ANIMATION_FRAME_DURATION);
			} else if (level == 2) {
				renderNormal.setup("game_table_toast_lv2_normal");
				cookingAnimation.addFrame("game_table_toast_lv2_work_1", 116, 123, COOK_ANIMATION_FRAME_DURATION);
				cookingAnimation.addFrame("game_table_toast_lv2_work_2", 116, 123, COOK_ANIMATION_FRAME_DURATION);
			} else if (level == 3) {
				renderNormal.setup("game_table_toast_lv3_normal");
				cookingAnimation.addFrame("game_table_toast_lv3_work_1", 116, 123, COOK_ANIMATION_FRAME_DURATION);
				cookingAnimation.addFrame("game_table_toast_lv3_work_2", 116, 123, COOK_ANIMATION_FRAME_DURATION);
			}
		} else if (type == Type.FryingPan) {
			setup(195, 353, 131, 128);
			button.setup(195, 353, 131, 128);
			
			renderNormal.setup(131, 128);
			cookingAnimation.setLoop(true);
			
			if (level == 1) {
				renderNormal.setup("game_table_pan_lv1_normal");
				cookingAnimation.addFrame("game_table_pan_lv1_work_1", 131, 128, COOK_ANIMATION_FRAME_DURATION);
				cookingAnimation.addFrame("game_table_pan_lv1_work_2", 131, 128, COOK_ANIMATION_FRAME_DURATION);
			} else if (level == 2) {
				renderNormal.setup("game_table_pan_lv2_normal");
				cookingAnimation.addFrame("game_table_pan_lv2_work_1", 131, 128, COOK_ANIMATION_FRAME_DURATION);
				cookingAnimation.addFrame("game_table_pan_lv2_work_2", 131, 128, COOK_ANIMATION_FRAME_DURATION);
			} else if (level == 3) {
				renderNormal.setup("game_table_pan_lv3_normal");
				cookingAnimation.addFrame("game_table_pan_lv3_work_1", 131, 128, COOK_ANIMATION_FRAME_DURATION);
				cookingAnimation.addFrame("game_table_pan_lv3_work_2", 131, 128, COOK_ANIMATION_FRAME_DURATION);
			}
			
			smokeAnimation.setLoop(true);
			smokeAnimation.setupOffset(18, -27);
			smokeAnimation.addFrame("game_table_pan_smoke_work_1", 98, 101, COOK_ANIMATION_FRAME_DURATION);
			smokeAnimation.addFrame("game_table_pan_smoke_work_2", 98, 101, COOK_ANIMATION_FRAME_DURATION);
		}
		
		updateState();
		add(button);
	}
	
	private void updateState() {
		if (!cooking) {
			add(renderNormal);
			remove(cookingAnimation);
			cookingAnimation.stop();
			
			if (type == Type.FryingPan) {
				remove(smokeAnimation);
				smokeAnimation.stop();
			}
		} else {
			remove(renderNormal);
			add(cookingAnimation);
			cookingAnimation.start();
			
			if (type == Type.FryingPan) {
				add(smokeAnimation);
				smokeAnimation.start();
			}
		}
	}
	
	@Override
	public void reset() {
		if (cooking) {
			this.turnOff();
		}
		if (foodIcon != null) {
			updateNewFoodIcon(Food.INVALID_TYPE);
		}
		super.reset();
	}
	
	public int getLevel() {
		return level;
	}
	
	public Type getType() {
		return type;
	}
	
	private Type type;
	private int level;
	public boolean cooking = false;
	
	public RenderComponent renderNormal;
	public FrameAnimationComponent cookingAnimation;
	public FrameAnimationComponent smokeAnimation;
	protected static final long COOK_ANIMATION_FRAME_DURATION = 250;
	private Sound machineWorkingSound;
	private int workingSoundStreamId;
	private Sound machineDoneSound;
	private Animation clickAnimation;
	
	private FoodComponent foodIcon;

}
