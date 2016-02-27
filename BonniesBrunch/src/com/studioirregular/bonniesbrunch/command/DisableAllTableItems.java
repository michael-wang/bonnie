package com.studioirregular.bonniesbrunch.command;

import com.studioirregular.bonniesbrunch.entity.Table;

public class DisableAllTableItems implements Command {

	private Table table;
	private boolean disable;
	
	public DisableAllTableItems(Table table, boolean disable) {
		this.table = table;
		this.disable = disable;
	}
	
	@Override
	public void execute() {
		table.setDisable(disable);
	}

}
