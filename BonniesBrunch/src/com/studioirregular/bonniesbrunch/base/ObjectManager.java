package com.studioirregular.bonniesbrunch.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;

public class ObjectManager extends ObjectBase {

	private static final String TAG = "object-manager";
	
	protected List<ObjectBase> objects = new ArrayList<ObjectBase>();
	// override if you want to maintain certain order for objects.
	protected Comparator<ObjectBase> getComparator() {
		return null;
	}
	
	private List<ObjectBase> pendingAdd = new ArrayList<ObjectBase>();
	private List<ObjectBase> pendingRemove = new ArrayList<ObjectBase>();
	
	public ObjectManager() {
		super();
	}
	
	@Override
	public void update(long timeDelta, ObjectBase parent) {
		commitUpdate();
		
		for (int i = 0; i < objects.size(); i++) {
			objects.get(i).update(timeDelta, this);
		}
	}
	
	@Override
	public void reset() {
		commitUpdate();
		for (int i = 0; i < objects.size(); i++) {
			ObjectBase obj = objects.get(i);
			obj.reset();
		}
	}
	
	public void add(ObjectBase obj) {
		if (Config.DEBUG_LOG) Log.d(TAG, "add obj:" + obj);
		pendingAdd.add(obj);
	}
	
	public void remove(ObjectBase obj) {
		if (Config.DEBUG_LOG) Log.d(TAG, "remove obj:" + obj);
		pendingRemove.add(obj);
	}
	
	public int getCount() {
		return objects.size();
	}
	
	public ObjectBase getObject(int index) {
		return objects.get(index);
	}
	
	protected void commitUpdate() {
		if (pendingRemove.isEmpty() && pendingAdd.isEmpty()) {
			return;
		}
		
		// add first, for pendingRemove might remove it later.
		if (pendingAdd.isEmpty() == false) {
			for (ObjectBase obj : pendingAdd) {
				// add to head so later added object with same sorting order gets to draw first,
				// and let previous object draws later.
				objects.add(0, obj);
			}
			pendingAdd.clear();
		}
		
		if (pendingRemove.isEmpty() == false) {
			for (ObjectBase obj : pendingRemove) {
				objects.remove(obj);
			}
			pendingRemove.clear();
		}
		
		if (Config.DEBUG_LOG) Log.d(TAG, getClass().getSimpleName() + ":commitUpdate Collections.sort!");
		Comparator<ObjectBase> comparator = getComparator();
		if (comparator != null) {
			Collections.sort(objects, comparator);
		}
	}

}
