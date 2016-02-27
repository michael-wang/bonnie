package com.studioirregular.bonniep2;


public class ButtonEntity extends BasicEntity {

	private StatfulRenderComponent renderComponent;
	
	public ButtonEntity(SceneBase scene, String id) {
		super(scene, id);
	}
	
	@Override
	public void add(Component component) {
		super.add(component);
		
		if (component instanceof StatfulRenderComponent) {
			renderComponent = (StatfulRenderComponent)component;
		}
	}
	
	@Override
	public void send(Object sender, Event event) {
		super.send(sender, event);
		
		if (sender instanceof ButtonComponent) {
			if (renderComponent != null) {
				renderComponent.setState(event.what);
			}
		}
	}

}
