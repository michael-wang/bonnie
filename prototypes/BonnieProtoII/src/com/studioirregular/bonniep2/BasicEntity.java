package com.studioirregular.bonniep2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

public class BasicEntity implements Entity, EventHost, OnTouchListener, EventListener {

	private static final boolean DO_LOG = false;
	private static final String TAG = "basic-entity";
	
	protected SceneBase scene;
	protected String id;
	protected RectF box = new RectF();
	protected List<Component> components = new ArrayList<Component>();
	protected List<GLRenderable> renderableList = new ArrayList<GLRenderable>();
	protected Map<Event, Command> event2Command = new HashMap<Event, Command>();
	
	protected boolean started = false;
	protected boolean enabled = true;
	protected boolean visible = true;
	
	public BasicEntity(SceneBase scene, String id) {
		this.scene = scene;
		this.id = id;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " id:" + id + ", #components:" + components.size();
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public void add(Component component) {
		if (DO_LOG) Log.d(TAG, "entity:" + id + " add component:" + component.getId());
		scene.scheduleCommand(new AddComponentCommand(this, component));
	}
	
	@Override
	public void add(Component component, String afterComponentId) {
		if (DO_LOG) Log.d(TAG, "entity:" + id + " add component:" + component.getId() + ", afterComponentId:" + afterComponentId);
		scene.scheduleCommand(new AddComponentCommand(this, component, afterComponentId));
	}
	
	@Override
	public void remove(String componentId) {
		if (DO_LOG) Log.d(TAG, "entity:" + id + " remove component:" + componentId);
		scene.scheduleCommand(new RemoveComponentCommand(this, componentId));
	}
	
	void addComponentInternal(Component component) {
		if (DO_LOG) Log.d(TAG, "addComponentInternal entity:" + id + ", component:" + component);
		
		synchronized (components) {
			components.add(component);
		}
		
		if (component instanceof GLRenderable) {
			GLRenderable render = (GLRenderable)component;
			
			synchronized (renderableList) {
				renderableList.add(render);
			}
		}
	}
	
	void addComponentInternal(Component component, String afterComponentId) {
		if (DO_LOG) Log.d(TAG, "addComponentInternal entity:" + id + ", component:" + component + ",afterComponentId:" + afterComponentId);
		
		boolean findAfterRender = component instanceof GLRenderable;
		GLRenderable afterRender = null;
		
		synchronized (components) {
			for (int i = 0; i < components.size(); i++) {
				final Component c = components.get(i);
				if (findAfterRender && c instanceof GLRenderable) {
					afterRender = (GLRenderable)c;
				}
				
				if (c.getId().equals(afterComponentId)) {
					components.add(i + 1, component);
					break;
				}
			}
		}
		
		if (component instanceof GLRenderable) {
			GLRenderable render = (GLRenderable)component;
			
			synchronized (renderableList) {
				if (afterRender != null) {
					for (int i = 0; i < renderableList.size(); i++) {
						if (afterRender == renderableList.get(i)) {
							renderableList.add(i + 1, render);
						}
					}
				} else {
					renderableList.add(render);
				}
			}
		}
	}
	
	Component removeComponentInternal(String componentId) {
		Component victom = getComponent(componentId);
		synchronized (components) {
			if (components.remove(victom) == false) {
				Log.w(TAG, "removeComponentInternal components cannot find component:" + componentId);
			}
		}
		
		if (victom instanceof GLRenderable) {
			synchronized (renderableList) {
				if (renderableList.remove(victom) == false) {
					Log.w(TAG, "removeComponentInternal renderableList cannot find component:" + componentId);
				}
			}
		}
		
		return victom;
	}
	
	void removeComponentInternal(Component component) {
		synchronized (components) {
			if (components.remove(component) == false) {
				Log.w(TAG, id + ":removeComponentInternal components cannot find component:" + component.getId());
				if (DO_LOG) {
					StringBuilder msg = new StringBuilder(getId() + " component ids:");
					for (Component c : components) {
						msg.append(c.getId());
						msg.append(",");
					}
					Log.w(TAG, msg.toString());
				}
			}
		}
		
		if (component instanceof GLRenderable) {
			synchronized (renderableList) {
				if (renderableList.remove(component) == false) {
					Log.w(TAG, id + ":removeComponentInternal renderableList cannot find component:" + component.getId());
					if (DO_LOG) {
						StringBuilder msg = new StringBuilder(getId() + " renderableList ids:");
						for (Component c : components) {
							msg.append(c.getId());
							msg.append(",");
						}
						Log.w(TAG, msg.toString());
					}
				}
			}
		}
	}
	
	@Override
	public Component getComponent(String componentId) {
		synchronized (components) {
			for (Component component : components) {
				if (component.getId().equals(componentId)) {
					return component;
				}
			}
		}
		return null;
	}
	
	public void addEventCommandPair(Event event, Command command) {
		if (DO_LOG) Log.d(TAG, "addEventCommandPair event:" + event + ", command:" + command);
		event2Command.put(event, command);
	}
	
	protected boolean isReadyToStart() {
		return true;
	}
	
	public Scene getScene() {
		return scene;
	}
	
	@Override
	public void setEnable(boolean enabled) {
		this.enabled = enabled;
	}
	
	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	@Override
	public void update(long timeDiff) {
		if (!started) {
			if (isReadyToStart()) {
				this.send(this, new ComponentEvent(ComponentEvent.ENTITY_ALIVE, id));
				started = true;
			} else {
				return;
			}
		}
		
		synchronized (components) {
			for (Component component : components) {
				if (component instanceof Animation) {
					Animation anim = (Animation)component;
					
					if (anim.isStarted()) {
						anim.update(timeDiff);
					}
				}
			}
		}
	}
	
	@Override
	public int getRenderableCount() {
		if (!visible) {
			return 0;
		}
		return renderableList.size();
	}
	
	@Override
	public GLRenderable getRenderable(int index) {
		if (!visible) {
			return null;
		}
		return renderableList.get(index);
	}
	
	// EventHost interface
	@Override
	public void send(Object sender, Event event) {
		if (DO_LOG) Log.d(TAG, "send sender:" + sender + ",event:" + event);
		scene.scheduleCommand(new SendEventCommand(this, event));
	}
	
	static class SendEventCommand implements Command {

		private BasicEntity entity;
		private Event event;
		
		public SendEventCommand(BasicEntity entity, Event event) {
			this.entity = entity;
			this.event = event;
		}
		
		@Override
		public void execute() {
			entity.dispatchEvent(event);
			entity.onEvent(event);
		}
		
	}
	
	protected void dispatchEvent(Event event) {
		synchronized (components) {
			for (Component component : components) {
				if (component instanceof EventListener) {
					((EventListener)component).onEvent(event);
				}
			}
		}
	}
	
	@Override
	public void onEvent(Event event) {
		// map event to command and execute if match found
		Command command = event2Command.get(event);
		if (command != null) {
			if (DO_LOG) Log.d(TAG, "send execute command:" + command);
			// some command may cause deadlock on scene entity list, such as
			// command to remove entity while scene trying to loop over entity
			// list and draw them, so execute them on handler.
			scene.scheduleCommand(command);
		}
	}
	
	@Override
	public boolean onTouch(MotionEvent event) {
		if (enabled == false) {
			return false;
		}
		
		synchronized (components) {
			for (Component component : components) {
				if (component instanceof OnTouchListener) {
					OnTouchListener listener = (OnTouchListener)component;
					if (listener.onTouch(event)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public RectF getBoundingBox() {
		box.setEmpty();
		for (GLRenderable render : renderableList) {
			box.union(render.getX(), render.getY(), render.getX() + render.getWidth(), render.getY() + render.getHeight());
		}
		return box;
	}

}
