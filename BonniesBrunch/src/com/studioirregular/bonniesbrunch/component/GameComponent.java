package com.studioirregular.bonniesbrunch.component;

import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.entity.GameEntity;

/* GameComponent should be able to communicate with hosting entity by
 * sending direct game event. 
 */
public abstract class GameComponent extends ObjectBase {

	// larger value mean closer to user
	public final int zOrder;
	
	public GameComponent(int zOrder) {
		this.zOrder = zOrder;
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		update(timeDelta, (GameEntity)parent);
	}
	
	public abstract void update(long timeDelta, GameEntity parent);
	
}
