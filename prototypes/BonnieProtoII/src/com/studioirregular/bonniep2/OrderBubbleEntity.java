package com.studioirregular.bonniep2;

import android.graphics.PointF;


public class OrderBubbleEntity extends BasicEntity implements FoodProductHolder {

//	private static final String TAG = "order-bubble-entity";
	
	private static final String BACKGROUND_RENDER_ID = "render";
	private static final String THINKING_ANIMATION_ID = "thinking-animation";
	private static final String THINKING_PHYSICS_ANIMATION_ID = "thinking-physics-animation";
	
	private static final float WIDTH = 101;
	private static final float HEIGHT = 112;
	private int seat;
	private boolean isThinking = false;
	private RenderComponent renderEmpty;
	private FrameAnimationComponent thinkAnimation;
	private FrameAnimationComponent thinkPhysicsAnimation;
	
	public OrderBubbleEntity(SceneBase scene, String id, int seat) {
		super(scene, id);
		this.seat = seat;
		
		addBackgroundRenderComponent();
		addThinkingAnimation();
	}

	@Override
	public Size getHolderSizeType() {
		return Size.Medium;
	}

	@Override
	public void getFoodLocation(PointF topLeft) {
		topLeft.set(CustomerSeat.getFoodBaseX(seat),
				CustomerSeat.getFoodBaseY(seat));
	}
	
	@Override
	public void getBeverageLocation(PointF topLeft) {
		topLeft.set(CustomerSeat.getBeverageX(seat),
				CustomerSeat.getBeverageY(seat));
	}
	
	@Override
	public void getToastTopLocation(PointF loc) {
		// not support second location, for all toasts should be closed.
	}
	
	@Override
	public void getCandyLocation(FoodComponent candy, PointF topLeft) {
	}
	
	@Override
	public boolean isDraggable() {
		return false;
	}
	
	public boolean isThinking() {
		return isThinking;
	}
	
	public void startThinking() {
		isThinking = true;
		thinkAnimation.setVisible(true);
		thinkAnimation.start();
		
		renderEmpty.setVisible(false);
	}
	
	public void stopThinking() {
		thinkAnimation.stop();
		thinkAnimation.setVisible(false);
		
		renderEmpty.setVisible(true);
		
		isThinking = false;
	}
	
	public void startThinkingPhysics() {
		if (thinkPhysicsAnimation == null) {
			thinkPhysicsAnimation = new FrameAnimationComponent(
					THINKING_PHYSICS_ANIMATION_ID,
					CustomerSeat.getOrderBubbleX(seat),
					CustomerSeat.getOrderBubbleY(seat), WIDTH, HEIGHT, true,
					this);
			thinkPhysicsAnimation.addFrame(TextureSystem.getInstance().getPart("game_guest_08_order_think_1"), 1000);
			thinkPhysicsAnimation.addFrame(TextureSystem.getInstance().getPart("game_guest_08_order_think_2"), 1000);
			thinkPhysicsAnimation.addFrame(TextureSystem.getInstance().getPart("game_guest_08_order_think_3"), 1000);
			add(thinkPhysicsAnimation);
		}
		thinkPhysicsAnimation.start();
		thinkPhysicsAnimation.setVisible(true);
		
		if (isThinking) {
			thinkAnimation.stop();
			thinkAnimation.setVisible(false);
		} else {
			renderEmpty.setVisible(false);
		}
	}
	
	public void stopThinkingPhysics() {
		if (isThinking) {
			thinkAnimation.start();
			thinkAnimation.setVisible(true);
		} else {
			renderEmpty.setVisible(true);
		}
		
		if (thinkPhysicsAnimation != null) {
			thinkPhysicsAnimation.stop();
			thinkPhysicsAnimation.setVisible(false);
		}
	}
	
	private void addBackgroundRenderComponent() {
		GLTexture texture = TextureSystem.getInstance().getPart("game_order_bg");
		renderEmpty = new RenderComponent(BACKGROUND_RENDER_ID,
				CustomerSeat.getOrderBubbleX(seat),
				CustomerSeat.getOrderBubbleY(seat), WIDTH, HEIGHT, texture);
		add(renderEmpty);
	}
	
	private void addThinkingAnimation() {
		thinkAnimation = new FrameAnimationComponent(THINKING_ANIMATION_ID,
				CustomerSeat.getOrderBubbleX(seat),
				CustomerSeat.getOrderBubbleY(seat), WIDTH, HEIGHT, true, this);
		thinkAnimation.addFrame(TextureSystem.getInstance().getPart("game_order_think_1"), 300);
		thinkAnimation.addFrame(TextureSystem.getInstance().getPart("game_order_think_1"), 300);
		thinkAnimation.addFrame(TextureSystem.getInstance().getPart("game_order_think_1"), 300);
		thinkAnimation.setVisible(false);
		add(thinkAnimation);
	}

}
