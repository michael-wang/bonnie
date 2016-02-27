package com.studioirregular.bonniesbrunch.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.studioirregular.bonniesbrunch.Config;
import com.studioirregular.bonniesbrunch.FoodSystem.Food;
import com.studioirregular.bonniesbrunch.GameEventSystem;
import com.studioirregular.bonniesbrunch.GameEventSystem.GameEvent;
import com.studioirregular.bonniesbrunch.LevelSystem.TableConfig;
import com.studioirregular.bonniesbrunch.LevelSystem.TableItem;
import com.studioirregular.bonniesbrunch.LevelSystem.TableLevelItem;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.component.RenderComponent;
import com.studioirregular.bonniesbrunch.entity.FoodMachineIcon.Type;



public class Table extends GameEntity {

	private static final String TAG = "table";
	
	public Table(int zOrder) {
		super(zOrder);
	}
	
	public void setup(float x, float y, float width, float height, TableConfig tableConfig, boolean forSpecialLevel) {
		super.setup(x, y, width, height);
		
		this.forSpecialLevel = forSpecialLevel;
		
		RenderComponent bg = new RenderComponent(MIN_GAME_COMPONENT_Z_ORDER);
		bg.setup(width, height);
		bg.setup("game_table_bg");
		add(bg);
		
		final List<TableItem> items = tableConfig.items;
		
		TableItem item = get(items, TableItem.BAGEL);
		if (item != null) {
			FoodProducer tableItem = new FoodProducer(GameEntity.MIN_GAME_ENTITY_Z_ORDER, Food.BAGEL);
			tableItem.setup(95, 243, 117, 158);
			tableItem.setTouchArea(-8, -16, 0, 0);
			add(tableItem);
			tableItems.put(item.type, tableItem);
		}
		
		item = get(items, TableItem.CROISSANT);
		if (item != null) {
			FoodProducer tableItem = new FoodProducer(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 1, Food.CROISSANT);
			tableItem.setup(81, 363, 117, 117);
			tableItem.setTouchArea(-8, -16, 0, 0);
			add(tableItem);
			tableItems.put(item.type, tableItem);
		}
		
		item = get(items, TableItem.MUFFIN_MACHINE);
		if (item != null) {
			FoodMachineIcon machine = new FoodMachineIcon(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 2);
			machine.setup(FoodMachineIcon.Type.Muffin, ((TableLevelItem)item).level);
			add(machine);
			tableItems.put(item.type, machine);
		}
		
		item = get(items, TableItem.TOAST_MACHINE);
		if (item != null) {
			FoodMachineIcon machine = new FoodMachineIcon(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 3);
			machine.setup(FoodMachineIcon.Type.Toast, ((TableLevelItem)item).level);
			add(machine);
			tableItems.put(item.type, machine);
		}
		
		item = get(items, TableItem.FRYING_PAN);
		if (item != null) {
			FoodMachineIcon machine = new FoodMachineIcon(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 4);
			machine.setup(FoodMachineIcon.Type.FryingPan, ((TableLevelItem)item).level);
			add(machine);
			tableItems.put(item.type, machine);
		}
		
		item = get(items, TableItem.TOMATO);
		if (item != null) {
			FoodProducer tableItem = new FoodProducer(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 5, Food.TOMATO);
			tableItem.setup(612, 319, 61, 100);
			tableItem.setTouchArea(-16, -4, 16, 8);
			add(tableItem);
			tableItems.put(item.type, tableItem);
		}
		
		item = get(items, TableItem.BUTTER);
		if (item != null) {
			FoodProducer tableItem = new FoodProducer(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 6, Food.BUTTER);
			tableItem.setup(567, 387, 61, 100);
			tableItem.setTouchArea(-8, 8, 8, 0);
			add(tableItem);
			tableItems.put(item.type, tableItem);
		}
		
		item = get(items, TableItem.HONEY);
		if (item != null) {
			FoodProducer tableItem = new FoodProducer(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 7, Food.HONEY);
			tableItem.setup(658, 387, 61, 100);
			tableItem.setTouchArea(-8, 8, 8, 0);
			add(tableItem);
			tableItems.put(item.type, tableItem);
		}
		
		item = get(items, TableItem.LETTUCE);
		if (item != null) {
			FoodProducer tableItem = new FoodProducer(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 8, Food.LETTUCE);
			tableItem.setup(411, 257, 72, 81);
			tableItem.setTouchArea(0, -8, 0, 8);
			add(tableItem);
			tableItems.put(item.type, tableItem);
		}
		
		item = get(items, TableItem.CHEESE);
		if (item != null) {
			FoodProducer tableItem = new FoodProducer(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 9, Food.CHEESE);
			tableItem.setup(483, 257, 72, 81);
			tableItem.setTouchArea(0, -8, 0, 8);
			add(tableItem);
			tableItems.put(item.type, tableItem);
		}
		
		item = get(items, TableItem.MILK);
		if (item != null) {
			FoodProducer tableItem = new FoodProducer(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 10, Food.MILK);
			tableItem.setup(567, 210, 80, 105);
			tableItem.setTouchArea(-4, -4, 0, 8);
			add(tableItem);
			tableItems.put(item.type, tableItem);
		}
		
		item = get(items, TableItem.COFFEE);
		if (item != null) {
			FoodProducer tableItem = new FoodProducer(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 11, Food.COFFEE);
			tableItem.setup(647, 210, 74, 105);
			tableItem.setTouchArea(0, -4, 0, 8);
			add(tableItem);
			tableItems.put(item.type, tableItem);
		}
		
		item = get(items, TableItem.PLATE);
		if (item != null) {
			Plate plate = new Plate(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 12);
			plate.setup(345, 383, 120, 104);
			add(plate);
			tableItems.put(item.type, plate);
		}
		
		item = get(items, TableItem.CANDY_MACHINE);
		if (item != null) {
			CandyMachine candyMachine = new CandyMachine(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 13);
			candyMachine.setup(0, 230, 95, 168, ((TableLevelItem)item).level);
			add(candyMachine);
			tableItems.put(item.type, candyMachine);
		}
		
		item = get(items, TableItem.TRASHCAN);
		if (item != null) {
			Trashcan trashcan = new Trashcan(GameEntity.MIN_GAME_ENTITY_Z_ORDER + 14);
			trashcan.setup(0, 366, 81, 114);
			add(trashcan);
			tableItems.put(item.type, trashcan);
		}
	}
	
	public void resetLevelState() {
		send(GameEventSystem.getInstance().obtain(GameEvent.BRUNCH_TIME_TO_LEAVE));
		
		if (muffinMachine != null) {
			muffinMachine.hide();
		}
		if (toastMachine != null) {
			toastMachine.hide();
		}
		if (fryingPan != null) {
			fryingPan.hide();
		}
	}
	
	@Override
	public void add(ObjectBase obj) {
		super.add(obj);
		
		if (obj instanceof BrunchEntity) {
			if (plate != null) {
				plate.brunchAdded((BrunchEntity)obj);
			}
		} else if (obj instanceof Plate) {
			plate = (Plate)obj;
		} else if (obj instanceof FoodMachineIcon) {
			FoodMachineIcon icon = (FoodMachineIcon)obj;
			setupFoodMachine(icon);
		}
	}
	
	@Override
	public void remove(ObjectBase obj) {
		super.remove(obj);
		
		if (obj instanceof BrunchEntity) {
			if (plate != null) {
				plate.brunchRemoved();
			}
		}
	}
	
	// turn on/off disable for one table item.
	// itemType: constants from TableItem.
	public boolean enableTableItem(int itemType, boolean enable) {
		if (tableItems.containsKey(itemType) == false) {
			return false;
		}
		tableItems.get(itemType).setDisable(!enable);
		if (Config.DEBUG_LOG) Log.w(TAG, "enableTableItem item:" + tableItems.get(itemType).getClass().getSimpleName() + ",enable:" + enable);
		return true;
	}
	
	// machineType: constants from TableItem (MUFFIN_MACHINE, TOAST_MACHINE, or FRYING_PAN).
	public boolean enableFoodMachineItem(int machineType, FoodMachine.ItemType type, int index, boolean enable) {
		FoodMachine machine= null;
		if (TableItem.MUFFIN_MACHINE == machineType) {
			machine = muffinMachine;
		} else if (TableItem.TOAST_MACHINE == machineType) {
			machine = toastMachine;
		} else if (TableItem.FRYING_PAN == machineType) {
			machine = fryingPan;
		}
		
		if (machine == null) {
			return false;
		}
		
		machine.enableItem(type, index, enable);
		if (Config.DEBUG_LOG) Log.w(TAG, "enableFoodMachineItem machineType:" + tableItems.get(machineType).getClass().getSimpleName() + ",type:" + type + ",index:" + index + ",enable:" + enable);
		return true;
	}
	
	@Override
	protected void getRegionColor(float[] color) {
		color[0] = 0.5f;
		color[1] = 0.0f;
		color[2] = 0.0f;
		color[3] = 0.8f;
	}
	
	@Override
	protected boolean wantThisEvent(GameEvent event) {
		switch (event.what) {
		case GameEvent.FOOD_GENERATED:
		case GameEvent.FOOD_MACHINE_SHOW:
		case GameEvent.BRUNCH_TIME_TO_LEAVE:
		case GameEvent.BRUNCH_TOAST_CLOSED:
		case GameEvent.BRUNCH_NEED_TOAST_TOP:
			return true;
		}
		return false;
	}
	
	@Override
	protected void handleGameEvent(GameEvent event) {
		if (Config.DEBUG_LOG) Log.d(TAG, "handleGameEvent event:" + event);
		if (event.what == GameEvent.FOOD_GENERATED) {
			assert event.arg1 != Food.INVALID_TYPE && event.obj != null;
			
			final int foodType = event.arg1;
			final GameEntity eventSender = (GameEntity) event.obj;
			
			if (brunch == null) {
				setupBrunch(event.arg1);
			}
			
			if (brunch.addFood(foodType)) {
				GameEventSystem.scheduleEvent(GameEvent.FOOD_CONSUMED, foodType, eventSender);
			} else {
				if (Config.DEBUG_LOG) Log.w(TAG, "handleGameEvent add food rejected...");
			}
		} else if (event.what == GameEvent.FOOD_MACHINE_SHOW) {
			Type type = FoodMachineIcon.Type.values()[event.arg1];
			if (type == Type.Muffin) {
				muffinMachine.show();
			} else if (type == Type.Toast) {
				toastMachine.show();
			} else if (type == Type.FryingPan) {
				fryingPan.show();
			}
		} else if (event.what == GameEvent.BRUNCH_TIME_TO_LEAVE) {
			if (brunch != null) {
				onRemovingBrunch(brunch);
			}
			if (toastTop != null) {
				onRemovingToastTop(toastTop);
			}
		} else if (event.what == GameEvent.BRUNCH_NEED_TOAST_TOP) {
			assert toastTop == null;
			final int toastType = event.arg1;
			
			ToastTop toastTop = new ToastTop(MIN_GAME_ENTITY_Z_ORDER + 15);
			toastTop.setup(toastType, plate);
			onAddingToastTop(toastTop);
		} else if (event.what == GameEvent.BRUNCH_TOAST_CLOSED) {
			assert brunch != null;
			assert toastTop != null;
			
			brunch.closeToast();
			onRemovingToastTop(toastTop);
		}
	}
	
	private TableItem get(List<TableItem> items, int itemType) {
		for (TableItem item : items) {
			if (itemType == item.type) {
				return item;
			}
		}
		return null;
	}
	
	private void setupFoodMachine(FoodMachineIcon icon) {
		final FoodMachineIcon.Type type = icon.getType();
		final int level = icon.getLevel();
		final int cookDuration = forSpecialLevel ? 1000 : 5000;
		
		if (type == FoodMachineIcon.Type.Muffin) {
			muffinMachine = new FoodMachine(MIN_GAME_ENTITY_Z_ORDER + 20);
			if (level == 1) {
				muffinMachine.setup(FoodMachine.Type.MEDIUM_MACHINE, new int[] {Food.MUFFIN_CIRCLE}, icon, cookDuration);
			} else if (level == 2) {
				muffinMachine.setup(FoodMachine.Type.MEDIUM_MACHINE, new int[] {Food.MUFFIN_CIRCLE, Food.MUFFIN_SQUARE}, icon, cookDuration);
			}
			
			muffinMachine.hide();
			add(muffinMachine);
		} else if (type == FoodMachineIcon.Type.Toast) {
			toastMachine = new FoodMachine(MIN_GAME_ENTITY_Z_ORDER + 20);
			if (level == 1) {
				toastMachine.setup(FoodMachine.Type.MEDIUM_MACHINE, new int[] {Food.TOAST_WHITE}, icon, cookDuration);
			} else if (level == 2) {
				toastMachine.setup(FoodMachine.Type.MEDIUM_MACHINE, new int[] {Food.TOAST_WHITE, Food.TOAST_BLACK}, icon, cookDuration);
			} else if (level == 3) {
				toastMachine.setup(FoodMachine.Type.LARGE_MACHINE, new int[] {Food.TOAST_WHITE, Food.TOAST_BLACK, Food.TOAST_YELLOW}, icon, cookDuration);
			}
			
			toastMachine.hide();
			add(toastMachine);
		} else if (type == FoodMachineIcon.Type.FryingPan) {
			fryingPan = new FoodMachine(MIN_GAME_ENTITY_Z_ORDER + 20);
			if (level == 1) {
				fryingPan.setup(FoodMachine.Type.MEDIUM_MACHINE, new int[] {Food.EGG}, icon, cookDuration);
			} else if (level == 2) {
				fryingPan.setup(FoodMachine.Type.MEDIUM_MACHINE, new int[] {Food.EGG, Food.HAM}, icon, cookDuration);
			} else if (level == 3) {
				fryingPan.setup(FoodMachine.Type.LARGE_MACHINE, new int[] {Food.EGG, Food.HAM, Food.HOTDOG}, icon, cookDuration);
			}
			
			fryingPan.hide();
			add(fryingPan);
		}
	}
	
	private void setupBrunch(int food) {
		BrunchEntity brunch = new BrunchEntity(MIN_GAME_ENTITY_Z_ORDER + 15);
		brunch.setup(plate);
		//brunch.drawEntityRegion(0.8f, 1.0f, 0.8f, 0.5f);
		onAddingBrunch(brunch);
	}
	
	private void onAddingBrunch(BrunchEntity brunch) {
		add(brunch);
		
		if (disable) {
			brunch.setDisable(disable);
		}
		tableItems.put(TableItem.BRUNCH, brunch);
		
		this.brunch = brunch;
	}
	
	private void onRemovingBrunch(BrunchEntity brunch) {
		remove(brunch);
		tableItems.remove(TableItem.BRUNCH);
		this.brunch = null;
	}
	
	private void onAddingToastTop(ToastTop toastTop) {
		add(toastTop);
		
		if (disable) {
			toastTop.setDisable(disable);
		}
		tableItems.put(TableItem.TOAST_TOP, toastTop);
		
		this.toastTop = toastTop;
	}
	
	private void onRemovingToastTop(ToastTop toastTop) {
		remove(toastTop);
		tableItems.remove(TableItem.TOAST_TOP);
		this.toastTop = null;
	}
	
	private boolean forSpecialLevel;
	private Map<Integer, GameEntity> tableItems = new HashMap<Integer, GameEntity>();
	
	private Plate plate;
	private BrunchEntity brunch;
	private ToastTop toastTop;
	private FoodMachine muffinMachine;
	private FoodMachine toastMachine;
	private FoodMachine fryingPan;	// for egg, ham, and hotdog.

}
