package com.studioirregular.bonniep2;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.util.Pair;

public class AddEntityCommand implements Command {

	private static final boolean DO_LOG = false;
	private static final String TAG = "add-basic-entity-command";
	
	protected SceneBase scene;
	
	protected Entity entity;
	
	protected String entityId;
	protected String afterEntityId;
	protected List< Pair<Event, Command> > eventCommandPairs = new ArrayList< Pair<Event, Command> >();
	
	// Method I: client created entity
	public AddEntityCommand(SceneBase scene, Entity entity) {
		this(scene, entity, null);
	}
	
	public AddEntityCommand(SceneBase scene, Entity entity, String afterEntityId) {
		this.scene = scene;
		this.entity = entity;
		this.afterEntityId = afterEntityId;
	}
	
	// Method II: delay (basic)entity creation until time to add it to scene (to save memory usage)
	public AddEntityCommand(SceneBase scene, String id) {
		this(scene, id, null);
	}
	
	public AddEntityCommand(SceneBase scene, String id, String afterEntityId) {
		this.scene = scene;
		this.entityId = id;
		this.afterEntityId = afterEntityId;
	}
	
	public void addCommandTrigger(Event event, Command command) {
		eventCommandPairs.add(Pair.create(event, command));
	}
	
	@Override
	public void execute() {
		if (DO_LOG) Log.d(TAG, "execute entityId:" + entityId);
		
		if (entity == null) {
			entity = createEntity();
			for (Pair<Event, Command> pair : eventCommandPairs) {
				((BasicEntity)entity).addEventCommandPair(pair.first, pair.second);
			}
		}
		
		if (afterEntityId != null) {
			scene.addEntityInternal(entity, afterEntityId);
		} else {
			scene.addEntityInternal(entity);
		}
	}
	
	protected Entity createEntity() {
		return new BasicEntity(scene, entityId);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " entity:" + entity + ", entityId:"
				+ entityId + ", afterEntityId:" + afterEntityId
				+ ", #eventCommandPairs:" + eventCommandPairs.size();
	}
	
}
