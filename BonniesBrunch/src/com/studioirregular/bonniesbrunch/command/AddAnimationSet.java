package com.studioirregular.bonniesbrunch.command;

import java.util.ArrayList;
import java.util.List;

import com.studioirregular.bonniesbrunch.component.Animation;
import com.studioirregular.bonniesbrunch.component.AnimationSet;
import com.studioirregular.bonniesbrunch.component.HoldAnimation;
import com.studioirregular.bonniesbrunch.component.ScaleAnimation;
import com.studioirregular.bonniesbrunch.component.TranslateAnimation;
import com.studioirregular.bonniesbrunch.main.SceneManager;

public class AddAnimationSet implements Command {

	public AddAnimationSet(SceneManager manager, String entityName, int zOrder, boolean loop) {
		this.manager = manager;
		this.entityName = entityName;
		this.zOrder = zOrder;
		this.loop = loop;
	}
	
	public void addTranslateAnimation(float fromX, float fromY, float toX,
			float toY, int duration) {
		addTranslateAnimation(fromX, fromY, toX, toY, duration, false, 0);
	}
	
	public void addTranslateAnimation(float fromX, float fromY, float toX,
			float toY, int duration, boolean notifyWhenFinished,
			int notifyId) {
		addTranslateAnimation(fromX, fromY, toX, toY, duration,
				notifyWhenFinished, notifyId, false, false);
	}
	
	public void addTranslateAnimation(float fromX, float fromY, float toX,
			float toY, int duration, boolean notifyWhenFinished,
			int notifyId, boolean fillBefore, boolean fillAfter) {
		TranslateAnimationAttributes attrs = new TranslateAnimationAttributes();
		attrs.duration = duration;
		attrs.notifyWhenFinished = notifyWhenFinished;
		attrs.notifyId = notifyId;
		attrs.fillBefore = fillBefore;
		attrs.fillAfter = fillAfter;
		attrs.fromX = fromX;
		attrs.fromY = fromY;
		attrs.toX = toX;
		attrs.toY = toY;
		attrsList.add(attrs);
	}
	
	public void addHoldAnimation(int duration) {
		this.addHoldAnimation(duration, false, 0);
	}
	
	public void addHoldAnimation(int duration, 
			boolean notifyWhenFinished, int notifyId) {
		HoldAnimationAttributes attrs = new HoldAnimationAttributes();
		attrs.duration = duration;
		attrs.notifyWhenFinished = notifyWhenFinished;
		attrs.notifyId = notifyId;
		attrsList.add(attrs);
	}
	
	public void addScaleAnimation(float fromScale, float toScale, int duration,
			boolean notifyWhenDone, int notifyId) {
		addScaleAnimation(fromScale, toScale, duration, notifyWhenDone,
				notifyId, false, false);
	}
	
	public void addScaleAnimation(float fromScale, float toScale, int duration,
			boolean notifyWhenDone, int notifyId, boolean fillBefore,
			boolean fillAfter) {
		ScaleAnimationAttributes attrs = new ScaleAnimationAttributes();
		attrs.duration = duration;
		attrs.notifyWhenFinished = notifyWhenDone;
		attrs.notifyId = notifyId;
		attrs.fillBefore = fillBefore;
		attrs.fillAfter = fillAfter;
		attrs.fromScale = fromScale;
		attrs.toScale = toScale;
		attrsList.add(attrs);
	}
	
	@Override
	public void execute() {
		AnimationSet set = new AnimationSet(zOrder);
		set.setLoop(loop);
		
		for (AnimationAttributes attrs : attrsList) {
			if (attrs instanceof TranslateAnimationAttributes) {
				addTranslateAnimation((TranslateAnimationAttributes)attrs, set);
			} else if (attrs instanceof HoldAnimationAttributes) {
				addHolderAnimation((HoldAnimationAttributes)attrs, set);
			} else if (attrs instanceof ScaleAnimationAttributes) {
				addScaleAnimation((ScaleAnimationAttributes)attrs, set);
			}
		}
		
		manager.addComponent(entityName, set);
		set.start();
	}
	
	private void addTranslateAnimation(TranslateAnimationAttributes attrs,
			AnimationSet set) {
		TranslateAnimation translate = new TranslateAnimation(zOrder,
				attrs.fromX, attrs.fromY, attrs.toX, attrs.toY, attrs.duration);
		translate.setLoop(false);
		translate.notifyWhenFinished(attrs.notifyWhenFinished, attrs.notifyId);
		translate.setFillBefore(attrs.fillBefore);
		translate.setFillAfter(attrs.fillAfter);

		set.addAnimation(translate);
	}
	
	private void addHolderAnimation(HoldAnimationAttributes attrs,
			AnimationSet set) {
		HoldAnimation anim = new HoldAnimation(zOrder);
		anim.setDuration(attrs.duration);
		anim.setLoop(false);
		anim.notifyWhenFinished(attrs.notifyWhenFinished, attrs.notifyId);

		set.addAnimation(anim);
	}

	private void addScaleAnimation(ScaleAnimationAttributes attrs,
			AnimationSet set) {
		Animation scale = new ScaleAnimation(zOrder, attrs.fromScale,
				attrs.toScale, attrs.duration);
		scale.setLoop(false);
		scale.notifyWhenFinished(attrs.notifyWhenFinished, attrs.notifyId);
		scale.setFillBefore(attrs.fillBefore);
		scale.setFillAfter(attrs.fillAfter);

		set.addAnimation(scale);
	}
	
	class AnimationAttributes {
		int duration;
		boolean notifyWhenFinished = false;
		int notifyId;
		boolean fillBefore, fillAfter;
	}
	
	class TranslateAnimationAttributes extends AnimationAttributes {
		float fromX, fromY;
		float toX, toY;
	}
	
	class HoldAnimationAttributes extends AnimationAttributes {
	}
	
	class ScaleAnimationAttributes extends AnimationAttributes {
		float fromScale, toScale;
	}
	
	private SceneManager manager;
	private String entityName;
	private int zOrder;
	private boolean loop;
	private List<AnimationAttributes> attrsList = new ArrayList<AnimationAttributes>();

}
