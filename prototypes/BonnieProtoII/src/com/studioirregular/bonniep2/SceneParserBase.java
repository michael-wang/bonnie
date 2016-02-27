package com.studioirregular.bonniep2;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.studioirregular.bonnie.levelsystem.Level;
import com.studioirregular.bonnie.levelsystem.LevelSystem;

public class SceneParserBase implements SceneParser {

	private static final String TAG = "scene-parser-base";
	
	protected MainScene scene;
	protected int levelConfigXml;
	
	public SceneParserBase(int levelConfigXml) {
		this.levelConfigXml = levelConfigXml;
	}
	
	protected int getLevelConfig() {
		return levelConfigXml;
	}
	
	@Override
	public boolean parse(Context context, SceneBase scene) {
		if (!(scene instanceof MainScene)) {
			Log.e(TAG, "parse scene should be MainScene!");
			return false;
		}
		this.scene = (MainScene)scene;
		
		Level level = parseLevel(context);
		if (level == null) {
			return false;
		}
		
		SceneNode node = genTable(level);
		genStart(node);
		
		return true;
	}
	
	protected Level parseLevel(Context context) {
		LevelSystem levelSystem = new LevelSystem();
		
		Level level = null;
		
		XmlResourceParser input = context.getResources().getXml(getLevelConfig());
		try {
			level = levelSystem.parse(input);
			if (level != null) {
				((MainScene)scene).setLevel(level);
			}
		} catch (XmlPullParserException e) {
			Log.e(TAG, "levelSystem.parse raise exception:" + e);
			return null;
		} catch (IOException e) {
			Log.e(TAG, "levelSystem.parse raise exception:" + e);
			return null;
		}
		return level;
	}
	
	protected SceneNode genTable(Level level) {
		SceneNode node = new SceneNode("stage");
		
		// background
		node.addCommand(new AddEntityCommand(scene, scene.getBackgroundEntityId()));
		switch (level.level) {
		case 1:
			node.addCommand(new AddRenderComponentCommand(scene, scene.getBackgroundEntityId(), "render", 0, 0, 720, 260, "game_bg_01"));
			break;
		case 2:
			node.addCommand(new AddRenderComponentCommand(scene, scene.getBackgroundEntityId(), "render", 0, 0, 720, 260, "game_bg_02"));
			break;
		case 3:
			node.addCommand(new AddRenderComponentCommand(scene, scene.getBackgroundEntityId(), "render", 0, 0, 720, 260, "game_bg_03"));
			break;
		case 4:
			node.addCommand(new AddRenderComponentCommand(scene, scene.getBackgroundEntityId(), "render", 0, 0, 720, 260, "game_bg_04"));
			break;
		case 5:
			node.addCommand(new AddRenderComponentCommand(scene, scene.getBackgroundEntityId(), "render", 0, 0, 720, 260, "game_bg_05"));
			break;
		default:
			Log.e(TAG, "unknown level:" + level.level);
			break;
		}
		
		// table
		node.addCommand(new AddEntityCommand(scene, "game_table_bg"));
		node.addCommand(new AddRenderComponentCommand(scene, "game_table_bg", "render", 0, 250, 720, 230, "game_table_bg"));
		
		// trashcan
		if ((level.tableConfig & Level.TABLE_ITEM_TRASHCAN) != 0) {
			node.addCommand(new AddTrashcanEntityCommand(scene, "game_table_trashcan"));
		}
		
		// bagel
		if ((level.tableConfig & Level.TABLE_ITEM_BAGEL) != 0) {
			node.addCommand(new AddFoodProducerCommand(scene, "game_table_bagel", FoodComponent.Type.Bagel));
			node.addCommand(new AddRenderComponentCommand(scene, "game_table_bagel", "render", 95, 243, 117, 158, "game_table_bagel"));
			node.addCommand(new AddButtonComponentCommand(scene, "game_table_bagel", "button", 95, 243, 117, 158));
		}
		
		// croissant
		if ((level.tableConfig & Level.TABLE_ITEM_BAGEL) != 0) {
			node.addCommand(new AddFoodProducerCommand(scene, "game_table_croissant", FoodComponent.Type.Croissant));
			node.addCommand(new AddRenderComponentCommand(scene, "game_table_croissant", "render", 81, 363, 117, 117, "game_table_croissant"));
			node.addCommand(new AddButtonComponentCommand(scene, "game_table_croissant", "button", 81, 363, 117, 117));
		}
		
		// butter
		if ((level.tableConfig & Level.TABLE_ITEM_SAUCE_BUTTER) != 0) {
			node.addCommand(new AddFoodProducerCommand(scene, "game_table_butter", FoodComponent.Type.Butter));
			node.addCommand(new AddRenderComponentCommand(scene, "game_table_butter", "render", 567, 387, 61, 100, "game_table_butter"));
			node.addCommand(new AddButtonComponentCommand(scene, "game_table_butter", "button", 567, 387, 61, 100));
		}
		
		// honey
		if ((level.tableConfig & Level.TABLE_ITEM_SAUCE_HONEY) != 0) {
			node.addCommand(new AddFoodProducerCommand(scene, "game_table_honey", FoodComponent.Type.Honey));
			node.addCommand(new AddRenderComponentCommand(scene, "game_table_honey", "render", 658, 387, 61, 100, "game_table_honey"));
			node.addCommand(new AddButtonComponentCommand(scene, "game_table_honey", "button", 658, 387, 61, 100));
		}
		
		// tomato
		if ((level.tableConfig & Level.TABLE_ITEM_SAUCE_TOMATO) != 0) {
			node.addCommand(new AddFoodProducerCommand(scene, "game_table_tomato", FoodComponent.Type.Tomato));
			node.addCommand(new AddRenderComponentCommand(scene, "game_table_honey", "render", 612, 319, 61, 100, "game_table_tomato"));
			node.addCommand(new AddButtonComponentCommand(scene, "game_table_tomato", "button", 612, 319, 61, 100));
		}
		
		// lettuce
		if ((level.tableConfig & Level.TABLE_ITEM_LETTUCE) != 0) {
			node.addCommand(new AddFoodProducerCommand(scene, "game_table_lettuce", FoodComponent.Type.Lettuce));
			node.addCommand(new AddRenderComponentCommand(scene, "game_table_lettuce", "render", 411, 257, 72, 81, "game_table_lettuce"));
			node.addCommand(new AddButtonComponentCommand(scene, "game_table_lettuce", "button", 411, 257, 72, 81));
		}
		
		// cheese
		if ((level.tableConfig & Level.TABLE_ITEM_CHEESE) != 0) {
			node.addCommand(new AddFoodProducerCommand(scene, "game_table_cheese", FoodComponent.Type.Cheese));
			node.addCommand(new AddRenderComponentCommand(scene, "game_table_cheese", "render", 483, 257, 72, 81, "game_table_cheese"));
			node.addCommand(new AddButtonComponentCommand(scene, "game_table_cheese", "button", 483, 257, 72, 81));
		}
		
		// milk
		if ((level.tableConfig & Level.TABLE_ITEM_MILK) != 0) {
			node.addCommand(new AddFoodProducerCommand(scene, "game_table_milk", FoodComponent.Type.Milk));
			node.addCommand(new AddRenderComponentCommand(scene, "game_table_milk", "render", 567, 210, 80, 105, "game_table_milk"));
			node.addCommand(new AddButtonComponentCommand(scene, "game_table_milk", "button", 567, 210, 80, 105));
		}
		
		// coffee
		if ((level.tableConfig & Level.TABLE_ITEM_COFFEE) != 0) {
			node.addCommand(new AddFoodProducerCommand(scene, "game_table_coffee", FoodComponent.Type.Coffee));
			node.addCommand(new AddRenderComponentCommand(scene, "game_table_coffee", "render", 647, 210, 74, 105, "game_table_coffee"));
			node.addCommand(new AddButtonComponentCommand(scene, "game_table_coffee", "button", 647, 210, 74, 105));
		}
		
		// plate
		node.addCommand(new AddFoodPlateCommand(scene, "game_table_plate_b"));
		
		// muffin food machine
		if ((level.tableConfig & Level.TABLE_ITEM_MASK_MUFFIN) != 0) {
			int machineLevel = 0;
			if ((level.tableConfig & Level.TABLE_ITEM_MUFFIN_LV1) != 0) {
				machineLevel = 1;
			} else {
				machineLevel = 2;
			}
			node.addCommand(new AddMuffinMachineCommand(scene, "muffin-machine", machineLevel));
		}
		
		// toast food machine
		if ((level.tableConfig & Level.TABLE_ITEM_MASK_TOAST) != 0) {
			int machineLevel = 0;
			if ((level.tableConfig & Level.TABLE_ITEM_TOAST_LV1) != 0) {
				machineLevel = 1;
			} else if ((level.tableConfig & Level.TABLE_ITEM_TOAST_LV2) != 0) {
				machineLevel = 2;
			} else {
				machineLevel = 3;
			}
			node.addCommand(new AddToastMachineCommand(scene, "toast-machine", machineLevel));
		}
		
		// frying pan food machine
		if ((level.tableConfig & Level.TABLE_ITEM_MASK_PAN) != 0) {
			int machineLevel = 0;
			if ((level.tableConfig & Level.TABLE_ITEM_PAN_LV1) != 0) {
				machineLevel = 1;
			} else if ((level.tableConfig & Level.TABLE_ITEM_PAN_LV2) != 0) {
				machineLevel = 2;
			} else if ((level.tableConfig & Level.TABLE_ITEM_PAN_LV3) != 0) {
				machineLevel = 3;
			}
			node.addCommand(new AddFryingPanMachineCommand(scene, "frying-pan", machineLevel));
		}
		
		if ((level.tableConfig & Level.TABLE_ITEM_MASK_CANDY) != 0) {
			int machineLevel = 0;
			if ((level.tableConfig & Level.TABLE_ITEM_CANDY_LV1) != 0) {
				machineLevel = 1;
			} else if ((level.tableConfig & Level.TABLE_ITEM_CANDY_LV2) != 0) {
				machineLevel = 2;
			} else if ((level.tableConfig & Level.TABLE_ITEM_CANDY_LV3) != 0) {
				machineLevel = 3;
			}
			node.addCommand(new AddCandyMachineCommand(scene, "candy-machine", machineLevel));
		}
		
		// pause button
		node.addCommand(new AddPauseCommand(scene));
		
		return node;
	}
	
	protected void genStart(SceneNode node) {
		Event startPlay = new EntityEvent(SceneEvent.LEVEL_START, "main-scene");
		Command sendPlay = new SendEventCommand(startPlay, scene);
		node.addCommand(sendPlay);
		
		scene.addSceneNode(node);
	}

}
