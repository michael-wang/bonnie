package com.studioirregular.bonniep2;


public class EntityEvent extends Event {

	public static final int USER_CLICK				= 0x1001;
	public static final int CUSTOMER_MAKE_ORDER		= 0x1002;
	public static final int ADD_FOOD_REQUEST		= 0x1003;	// obj <-- FoodComponent
	public static final int ADD_FOOD_ACCEPTED		= 0x1004;	// obj <-- FoodComponent
	
	public static final int FOOD_PRODUCT_DROPT		= 0x1005;	// obj <-- FoodProductEntity
	public static final int FOOD_PRODUCT_CONSUMED	= 0x1006;	// obj <-- FoodProductEntity
	
	public static final int TUTORIAL_FOOD_ADDED		= 0x1201;	// obj <-- FoodComponent.Type
	public static final int TUTORIAL_CUSTOMER_LEAVED= 0x1202;
	
	public static final int SUPER_STAR_LADY_APPEARS = 0x1300;
	public static final int FOOD_CRITIC_EXCITED		= 0x1301;
	public static final int FOOD_CRITIC_WALKOUT_ANGRILY	= 0x1302;
	public static final int TRAMP_APPEARS			= 0x1303;
	
	private static final String TAG = "entity-event";
	
	public String entityId;
	public Object obj;
	
	public EntityEvent(int what, String entityId) {
		this(what, entityId, null);
	}
	
	public EntityEvent(int what, String entityId, Object obj) {
		super(what);
		this.entityId = entityId;
		this.obj = obj;
	}
	
	@Override
	public boolean equals(Object o) {
//		Log.d(TAG, "equals me:" + this.toString() + ", o:" + o.toString());
		
		if (super.equals(o) == false) {
			return false;
		}
		
		EntityEvent lhs = (EntityEvent)o;
		return (entityId == lhs.entityId);	// don't compare obj, event system will be re-designed so equals won't be used!
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + what;
		result = 31 * result + (entityId == null ? 0 : entityId.hashCode());
		result = 31 * result + (obj == null ? 0 : obj.hashCode());
		
//		Log.d(TAG, "hashCode result:" + result);
		return result;
	}
	
	@Override
	public String toString() {
		return getClass().getName() + " what:" + what + ", entityId:" + entityId + ", obj:" + obj;
	}
	
}
