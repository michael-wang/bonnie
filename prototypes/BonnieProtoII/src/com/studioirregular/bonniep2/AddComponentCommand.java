package com.studioirregular.bonniep2;

public class AddComponentCommand implements Command {

	protected Entity entity;
	protected Component component;
	protected String afterComponentId;
	
	public AddComponentCommand(Entity entity, Component component) {
		this(entity, component, null);
	}
	
	public AddComponentCommand(Entity entity, Component component, String afterComponentId) {
		this.entity = entity;
		this.component = component;
		this.afterComponentId = afterComponentId;
	}
	
	@Override
	public void execute() {
		if (afterComponentId != null) {
			((BasicEntity)entity).addComponentInternal(component, afterComponentId);
		} else {
			((BasicEntity)entity).addComponentInternal(component);
		}
	}

}
