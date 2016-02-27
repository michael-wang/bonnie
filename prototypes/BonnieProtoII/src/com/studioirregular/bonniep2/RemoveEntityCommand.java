package com.studioirregular.bonniep2;

import java.util.ArrayList;
import java.util.List;

public class RemoveEntityCommand implements Command {

//	private static final String TAG = "remove-entity-command";
	
	private SceneBase scene;
	private List<String> entityIds = new ArrayList<String>();
	private Entity entity;
	
	public RemoveEntityCommand(SceneBase scene, String entityId) {
		this(scene, new String[] {entityId});
	}
	
	public RemoveEntityCommand(SceneBase scene, String[] entityIds) {
		this.scene = scene;
		for (String id : entityIds) {
			this.entityIds.add(id);
		}
	}
	
	public RemoveEntityCommand(SceneBase scene, Entity entity) {
		this.scene = scene;
		this.entity = entity;
	}
	
	@Override
	public void execute() {
//		Log.d(TAG, "execute #entityIds:" + entityIds.size());
		if (entityIds != null)  {
			for (String id : entityIds) {
				scene.removeEntityInternal(id);
			}
		}
		if (entity != null) {
			scene.removeEntityInternal(entity);
		}
 	}
	
	@Override
	public String toString() {
		return "RemoveComponentCommand #entityIds:" + entityIds.size();
	}
}
