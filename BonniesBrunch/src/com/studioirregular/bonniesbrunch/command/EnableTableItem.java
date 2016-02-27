package com.studioirregular.bonniesbrunch.command;

import com.studioirregular.bonniesbrunch.entity.FoodMachine;
import com.studioirregular.bonniesbrunch.entity.Table;

public class EnableTableItem implements Command {

	private Table table;
	private int itemType;
	private boolean enable;
	
	private FoodMachine.ItemType machineItemType = null;
	private int machineItemIndex = -1;
	
	public EnableTableItem(Table table, int itemType, boolean enable) {
		this.table = table;
		this.itemType = itemType;
		this.enable = enable;
	}
	
	public EnableTableItem(Table table, int machineType, FoodMachine.ItemType itemType, int index, boolean enable) {
		this.table = table;
		this.itemType = machineType;
		this.machineItemType = itemType;
		this.machineItemIndex = index;
		this.enable = enable;
	}
	
	@Override
	public void execute() {
		if (machineItemType == null) {
			table.enableTableItem(itemType, enable);
		} else {
			table.enableFoodMachineItem(itemType, machineItemType, machineItemIndex, enable);
		}
	}

}
