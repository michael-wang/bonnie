package com.studioirregular.bonniep2;



public class ComponentEvent extends Event {

	public static final int ENTITY_ALIVE		= 0x2001;	// componentId <-- entity ID in this case!
	
	public static final int BUTTON_UP			= 0x2002;
	public static final int BUTTON_DOWN			= 0x2003;
	public static final int BUTTON_CANCEL		= 0x2004;
	
	public static final int ANIMATION_STARTED	= 0x2005;
	public static final int ANIMATION_ENDED		= 0x2006;
	
	public static final int DRAGGABLE_DROPT		= 0x2007;
	
	public static final int CUSTOMER_PHYSICIST_CHANGE_THINKING = 0x2008;
	
	private static final String TAG = "component-event";
	
	private String componentId;
	
	public ComponentEvent(int what, String componentId) {
		super(what);
		this.componentId = componentId;
	}
	
	public String getComponentId() {
		return componentId;
	}
	
	@Override
	public boolean equals(Object o) {
		if (super.equals(o) == false) {
			return false;
		}
		
		ComponentEvent lhs = (ComponentEvent)o;
		return componentId == lhs.componentId;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + what;
		result = 31 * result + (componentId == null ? 0 : componentId.hashCode());
		
		return result;
	}
	
	@Override
	public String toString() {
		return getClass().getName() + " what:" + what;
	}
	
}
