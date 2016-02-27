package com.studioirregular.bonniesbrunch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

/* Support two types of usage:
 * 1. scheduleEvent: event are queued and dispatch through entity graph on start of each frame.
 * 2. obtain event and send to host entity: reduce event dispatch and simplified event design 
 *    (e.g. entity no need to check for every button event about who sent it)
 */
public class GameEventSystem {
	
	private static final String TAG = "game-event-system";
	
	// you can schedule event during dispatch event, for next() does not keep list state (such as using iterator).
	public static void scheduleEvent(int what) {
		scheduleEvent(what, 0, null);
	}
	
	public static void scheduleEvent(int what, int arg1) {
		scheduleEvent(what, arg1, null);
	}
	
	public static void scheduleEvent(int what, int arg1, Object obj) {
		if (Config.DEBUG_LOG) Log.d(TAG, "scheduleEvent what:" + what + ", arg1:" + arg1 + ", obj:" + obj);
		GameEvent event = getInstance().obtain(what, arg1, obj);
		getInstance().events.add(event);
	}
	
	// singleton
	public static GameEventSystem getInstance() {
		if (sInstance == null) {
			sInstance = new GameEventSystem();
		}
		return sInstance;
	}
	private static GameEventSystem sInstance = null;
	
	GameEvent next() {
		GameEvent result = null;
		synchronized (events) {
			if (events.isEmpty() == false) {
				result = events.remove(0);
			}
		}
		return result;
	}
	
	public GameEvent obtain(int what) {
		return obtain(what, 0, null);
	}
	
	public GameEvent obtain(int what, int arg1) {
		return obtain(what, arg1, null);
	}
	
	public GameEvent obtain(int what, int arg1, Object obj) {
		return new GameEvent(what, arg1, obj);
	}
	
	private List<GameEvent> events = new ArrayList<GameEvent>();
	
	private GameEventSystem() {
		if (Config.DEBUG_LOG_ALLOCATION) Log.d(TAG, "InputSystem allocated");
	}
	
	public static class GameEvent {
		public int what;
		public int arg1;
		public Object obj;
		
		private GameEvent(int what) {
			this(what, 0, null);
		}
		
		private GameEvent(int what, int arg1) {
			this(what, arg1, null);
		}
		
		private GameEvent(int what, int arg1, Object obj) {
			if (Config.DEBUG_LOG_ALLOCATION) Log.d(TAG, "GameEvent allocated.");
			this.what = what;
			this.arg1 = arg1;
			this.obj = obj;
		}
		
		// menu events
		public static final int MAIN_MENU_PLAY			= 0x01;
		public static final int MAIN_MENU_HELP			= 0x02;
		public static final int MAIN_MENU_CREDIT		= 0x03;
		public static final int MAIN_MENU_BUY			= 0x04;	// arg1: 0: force, 1: hint only
		public static final int MAIN_MENU_MUSIC			= 0x05;	// arg1: 0:off, 1:on
		public static final int MAIN_MENU_SOUND			= 0x06;	// arg1: 0:off, 1:on
		public static final int LEVEL_MAJOR_SELECTED	= 0x07;	// arg1: major level
		public static final int LEVEL_MINOR_SELECTED	= 0x08;	// arg1: minor level (1 - 10)
		public static final int MENU_BACK				= 0x09;
		public static final int HELP_NEXT_CARD			= 0x10;
		public static final int HELP_PREV_CARD			= 0x11;
		public static final int PURCHASE_TEE			= 0x12;
		
		// entity root events
		public static final int RENDERER_FADE_OUT_DONE	= 0x80;
		public static final int ROOT_BACK				= 0x81;
		
		// component events
		public static final int BUTTON_DOWN		= 0x100;
		public static final int BUTTON_UP		= 0x101;
		public static final int BUTTON_CANCEL	= 0x102;
		
		public static final int DRAG_BEGIN			= 0x110;
		public static final int DRAG_END			= 0x111;
		public static final int DROP_GAME_ENTITY	= 0x112;	// arg1: N/A, obj == GameEntity (BrunchEntity or Candy)
		public static final int DROP_ACCEPTED		= 0x113;	// arg1: N/A, obj == GameEntity (BrunchEntity or Candy)
		public static final int DROP_REJECTED		= 0x114;	// arg1: N/A, obj == GameEntity (BrunchEntity or Candy)
		
		public static final int ANIMATION_END		= 0x120;	// arg1: id (int)
		
		// pause event
		public static final int GAME_PAUSE			= 0xA00;
		public static final int GAME_RESUME			= 0xA01;
		public static final int GAME_RESTART		= 0xA02;	// arg1: 1 (need confirm), 0 (no confirm)
		public static final int GAME_BACK_TO_MENU	= 0xA03;	// arg1: 1 (need confirm), 0 (no confirm)
		public static final int GAME_EXIT			= 0xA04;
		public static final int GAME_NEXT_LEVEL		= 0xA05;	// arg1: N/A, obj == LevelNumber to go to.
		public static final int GAME_CONFIRM_YES	= 0xA06;
		public static final int GAME_CONFIRM_NO		= 0xA07;
		
		// food events
		// people who send FOOD_GENERATED should catch FOOD_CONSUMED even you don't need it, so consumed event won't travel around.
		public static final int FOOD_GENERATED		= 0x200;	// arg1: int foodType, obj == GameEvent.this
		public static final int FOOD_CONSUMED		= 0x201;	// arg1: int foodType, obj == FoodMachine.CookSlot.this
		public static final int FOOD_DONE_COOKING	= 0x202;	// arg1: int foodType
		
		// brunch events
		public static final int BRUNCH_TIME_TO_LEAVE	= 0x213;	// arg1: N/A, obj == BrunchEntity
		public static final int BRUNCH_NEED_TOAST_TOP	= 0x214;	// arg1: int foodType
		public static final int BRUNCH_TOAST_CLOSED		= 0x215;	// arg1: N/A, obj == ToastTop
		
		// food machine events
		public static final int FOOD_MACHINE_SHOW			= 0x300;	// arg1: FoodMachineIcon.Type.ordinal
		public static final int FOOD_BUTTON_REQUEST_ADD		= 0x301;	// arg1: int foodType
		public static final int FOOD_MACHINE_ADD_FOOD		= 0x302;	// arg1: int foodType
		public static final int FOOD_MACHINE_START_COOKING	= 0x303;	// arg1: slot index, obj == FoodMachine.CookSlot
		public static final int FOOD_MACHINE_FINISH_COOKING	= 0x304;	// arg1: slot index, obj == FoodMachine.CookSlot
		public static final int CANDY_MACHINE_START_COOKING		= 0x305;
		public static final int CANDY_MACHINE_FINISH_COOKING	= 0x306;
		public static final int FOOD_MACHINE_SLOT_FOOD_ADDED	= 0x307;	// arg1: int foodType, obj == FoodMachine.CookSlot
		
		// customer events
		public static final int CUSTOMER_SUPER_LADY_ARRIVED		= 0x400;	// arg1: N/A, obj == Customer
		public static final int CUSTOMER_TRAMP_ARRIVED			= 0x401;	// arg1: N/A, obj == Customer
		public static final int CUSTOMER_FOOD_CRITIC_SATISFIED	= 0x402;	// arg1: N/A, obj == Customer
		public static final int CUSTOMER_FOOD_CRITIC_WAIT_OUT	= 0x403;	// arg1: N/A, obj == Customer
		
		// customer manager events
		public static final int CUSTOMER_MANAGER_ADD_CUSTOMER			= 0x503;	// arg1: N/A, obj == CustomerSpec
		public static final int CUSTOMER_MANAGER_SERVICE_RESULT			= 0x504;	// arg1: N/A, obj == ServiceResult
		
		// misc events
		public static final int GAME_SCORE_ADD				= 0x600;		// arg1: added amount.
		public static final int GAME_SCORE_CLEAR			= 0x601;		// arg1: N/A
		public static final int GAME_SCORE_LEVEL_UP			= 0x602;		// arg1: new level.
		public static final int GAME_CLOCK_START			= 0x603;		// every entity manager should dispatch this event to all children.
		public static final int GAME_CLOCK_END				= 0x604;
		public static final int TUTORIAL_CUSTOMER_MADE_DECISION = 0x605;	// arg1: Customer.Type.ordinal
		public static final int TUTORIAL_CUSTOMER_EVENT 	= 0x606;
		public static final int ENTITY_DONE					= 0x607;		// arg1: ?, obj: GameEntity.this
		public static final int COMIC_FINISHED			 	= 0x609;
		public static final int SPLASH_FADE_OUT_DONE		= 0x60A;		// arg1: [internal] logo type.
		public static final int REQUEST_GOTO_NEXT_LEVEL		= 0x60B;
		
		// scene manager events
		public static final int SCENE_MANAGER_NEXT_NODE		= 0x700;
		public static final int SCENE_MANAGER_REQUEST_SKIP	= 0x701;
		public static final int SCENE_MANAGER_SCENE_END		= 0x702;
		
		// texture events
		public static final int TEXTURE_LIBRARY_LOAD_BEGIN	= 0x780;		// arg1: texture count,	obj: TextureLibrary
		public static final int TEXTURE_LIBRARY_LOAD_END	= 0x781;		// arg1: always 0,		obj: TextureLibrary
		public static final int TEXTURE_RELOAD_BEGIN		= 0x782;		// arg1: texture count.
		public static final int TEXTURE_RELOAD_END			= 0x783;
		public static final int TEXTURE_LOADING				= 0x784;		// arg1: number of remaining textures.
		
		public static final int DEBUG_TOGGLE_CUSTOMER_BUTTONS	= 0x800;
		
		private static Map<Integer, String> EVENT_NAME;
		static {
			EVENT_NAME = new HashMap<Integer, String>();
			
			EVENT_NAME.put(MAIN_MENU_PLAY,			"MAIN_MENU_PLAY");
			EVENT_NAME.put(MAIN_MENU_HELP,			"MAIN_MENU_HELP");
			EVENT_NAME.put(MAIN_MENU_CREDIT,		"MAIN_MENU_CREDIT");
			EVENT_NAME.put(MAIN_MENU_BUY,			"MAIN_MENU_BUY");
			EVENT_NAME.put(MAIN_MENU_MUSIC,			"MAIN_MENU_MUSIC");
			EVENT_NAME.put(MAIN_MENU_SOUND,			"MAIN_MENU_SOUND");
			EVENT_NAME.put(LEVEL_MAJOR_SELECTED,	"LEVEL_MAJOR_SELECTED");
			EVENT_NAME.put(LEVEL_MINOR_SELECTED,	"LEVEL_MINOR_SELECTED");
			EVENT_NAME.put(MENU_BACK,				"MENU_BACK");
			EVENT_NAME.put(HELP_NEXT_CARD,			"HELP_NEXT_CARD");
			EVENT_NAME.put(HELP_PREV_CARD,			"HELP_PREV_CARD");
			EVENT_NAME.put(PURCHASE_TEE,			"PURCHASE_TEE");
			
			EVENT_NAME.put(RENDERER_FADE_OUT_DONE,	"RENDERER_FADE_OUT_DONE");
			EVENT_NAME.put(ROOT_BACK,				"ROOT_BACK");
			
			EVENT_NAME.put(BUTTON_DOWN,		"BUTTON_DOWN");
			EVENT_NAME.put(BUTTON_UP,		"BUTTON_UP");
			EVENT_NAME.put(BUTTON_CANCEL,	"BUTTON_CANCEL");
			
			EVENT_NAME.put(DRAG_BEGIN,		"DRAG_BEGIN");
			EVENT_NAME.put(DRAG_END,		"DRAG_END");
			EVENT_NAME.put(DROP_GAME_ENTITY,"DROP_GAME_ENTITY");
			EVENT_NAME.put(DROP_ACCEPTED,	"DROP_ACCEPTED");
			EVENT_NAME.put(DROP_REJECTED,	"DROP_REJECTED");
			
			EVENT_NAME.put(ANIMATION_END,	"ANIMATION_END");
			
			EVENT_NAME.put(GAME_PAUSE,		"GAME_PAUSE");
			EVENT_NAME.put(GAME_RESUME,		"GAME_RESUME");
			EVENT_NAME.put(GAME_RESTART,	"GAME_RESTART");
			EVENT_NAME.put(GAME_BACK_TO_MENU,"GAME_BACK_TO_MENU");
			EVENT_NAME.put(GAME_EXIT,		"GAME_EXIT");
			EVENT_NAME.put(GAME_NEXT_LEVEL,	"GAME_NEXT_LEVEL");
			EVENT_NAME.put(GAME_CONFIRM_YES,"GAME_CONFIRM_YES");
			EVENT_NAME.put(GAME_CONFIRM_NO,	"GAME_CONFIRM_NO");
			
			EVENT_NAME.put(FOOD_GENERATED,	"FOOD_GENERATED");
			EVENT_NAME.put(FOOD_CONSUMED,	"FOOD_CONSUMED");
			
			EVENT_NAME.put(BRUNCH_TIME_TO_LEAVE,	"REMOVE_BRUNCH");
			EVENT_NAME.put(BRUNCH_NEED_TOAST_TOP,	"BRUNCH_NEED_TOP_TOAST");
			EVENT_NAME.put(BRUNCH_TOAST_CLOSED,		"BRUNCH_TOAST_CLOSED");
			
			EVENT_NAME.put(FOOD_MACHINE_SHOW,			"FOOD_MACHINE_SHOW");
			EVENT_NAME.put(FOOD_BUTTON_REQUEST_ADD,		"FOOD_BUTTON_REQUEST_ADD");
			EVENT_NAME.put(FOOD_MACHINE_ADD_FOOD,		"FOOD_MACHINE_ADD_FOOD");
			EVENT_NAME.put(FOOD_MACHINE_START_COOKING,	"FOOD_MACHINE_START_COOKING");
			EVENT_NAME.put(FOOD_MACHINE_FINISH_COOKING,	"FOOD_MACHINE_FINISH_COOKING");
			EVENT_NAME.put(CANDY_MACHINE_START_COOKING,	"CANDY_MACHINE_START_COOKING");
			EVENT_NAME.put(CANDY_MACHINE_FINISH_COOKING,"CANDY_MACHINE_FINISH_COOKING");
			EVENT_NAME.put(FOOD_MACHINE_SLOT_FOOD_ADDED,"FOOD_MACHINE_SLOT_FOOD_ADDED");
			
			EVENT_NAME.put(CUSTOMER_SUPER_LADY_ARRIVED,		"CUSTOMER_SUPER_LADY_ARRIVED");
			EVENT_NAME.put(CUSTOMER_TRAMP_ARRIVED,			"CUSTOMER_TRAMP_ARRIVED");
			EVENT_NAME.put(CUSTOMER_FOOD_CRITIC_SATISFIED,	"CUSTOMER_FOOD_CRITIC_SATISFIED");
			EVENT_NAME.put(CUSTOMER_FOOD_CRITIC_WAIT_OUT,	"CUSTOMER_FOOD_CRITIC_WAIT_OUT");
			
			EVENT_NAME.put(CUSTOMER_MANAGER_ADD_CUSTOMER,	"CUSTOMER_MANAGER_ADD_CUSTOMER");
			EVENT_NAME.put(CUSTOMER_MANAGER_SERVICE_RESULT,	"CUSTOMER_MANAGER_SERVICE_RESULT");
			
			EVENT_NAME.put(GAME_SCORE_ADD,			"GAME_SCORE_ADD");
			EVENT_NAME.put(GAME_SCORE_CLEAR,		"GAME_SCORE_CLEAR");
			EVENT_NAME.put(GAME_SCORE_LEVEL_UP,		"GAME_SCORE_LEVEL_UP");
			EVENT_NAME.put(GAME_CLOCK_START,		"GAME_CLOCK_START");
			EVENT_NAME.put(GAME_CLOCK_END,			"GAME_CLOCK_END");
			EVENT_NAME.put(TUTORIAL_CUSTOMER_MADE_DECISION,	"TUTORIAL_CUSTOMER_MADE_DECISION");
			EVENT_NAME.put(TUTORIAL_CUSTOMER_EVENT,	"TUTORIAL_CUSTOMER_EVENT");
			EVENT_NAME.put(ENTITY_DONE,				"ENTITY_DONE");
			EVENT_NAME.put(COMIC_FINISHED,			"COMIC_FINISHED");
			EVENT_NAME.put(SPLASH_FADE_OUT_DONE,	"SPLASH_FADE_OUT_DONE");
			EVENT_NAME.put(REQUEST_GOTO_NEXT_LEVEL,	"REQUEST_GOTO_NEXT_LEVEL");
			
			EVENT_NAME.put(SCENE_MANAGER_NEXT_NODE,		"SCENE_MANAGER_NEXT_NODE");
			EVENT_NAME.put(SCENE_MANAGER_REQUEST_SKIP,	"SCENE_MANAGER_REQUEST_SKIP");
			EVENT_NAME.put(SCENE_MANAGER_SCENE_END,		"SCENE_MANAGER_SCENE_END");
			
			EVENT_NAME.put(TEXTURE_LIBRARY_LOAD_BEGIN,	"TEXTURE_LIBRARY_LOAD_BEGIN");
			EVENT_NAME.put(TEXTURE_LIBRARY_LOAD_END,	"TEXTURE_LIBRARY_LOAD_END");
			EVENT_NAME.put(TEXTURE_RELOAD_BEGIN,		"TEXTURE_RELOAD_BEGIN");
			EVENT_NAME.put(TEXTURE_RELOAD_END,			"TEXTURE_RELOAD_END");
			EVENT_NAME.put(TEXTURE_LOADING,				"TEXTURE_LOADING");
			
			EVENT_NAME.put(DEBUG_TOGGLE_CUSTOMER_BUTTONS, "DEBUG_TOGGLE_CUSTOMER_BUTTONS");
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			
			if (!(o instanceof GameEvent)) {
				return false;
			}
			
			GameEvent lhs = (GameEvent) o;
			return what == lhs.what &&
					arg1 == lhs.arg1 &&
					(obj == null ? lhs.obj == null : obj.equals(lhs.obj));
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + " what:" + EVENT_NAME.get(what) + ", arg1:" + arg1 + ", obj:" + obj;
		}
		
	}
	
	public static class EventMap {
		public GameEvent key;
		public GameEvent value;
		
		public EventMap(GameEvent key, GameEvent value) {
			this.key = key;
			this.value = value;
		}
	}
	
}
