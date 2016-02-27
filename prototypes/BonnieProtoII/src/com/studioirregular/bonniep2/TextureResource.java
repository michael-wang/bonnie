package com.studioirregular.bonniep2;

import java.util.HashMap;
import java.util.Map;

public class TextureResource {

	static final String EXTERNAL_RESOURCES_PACKAGE = "com.studioirregular.bonniep2.res";
	static final String RES_PREFIX = EXTERNAL_RESOURCES_PACKAGE + ":drawable/";
	
	static Map<String, Integer> MAPPING = new HashMap<String, Integer>();
	static {
//		MAPPING.put("story_start_p1",		R.drawable.story_start_p1);
//		MAPPING.put("story_start_p2",		R.drawable.story_start_p2);
//		
//		MAPPING.put("opening_bg_ep1",		R.drawable.opening_bg_ep1);
//		
//		// table
//		MAPPING.put("game_bg_01",			R.drawable.game_bg_01);
//		MAPPING.put("game_table_bg",		R.drawable.game_table_bg);
//		MAPPING.put("game_table_trashcan",	R.drawable.game_table_trashcan);
//		MAPPING.put("game_table_trashcan_open",	R.drawable.game_table_trashcan_open);
//		MAPPING.put("game_table_bagel",		R.drawable.game_table_bagel);
//		MAPPING.put("game_table_croissant",	R.drawable.game_table_croissant);
//		MAPPING.put("game_table_butter",	R.drawable.game_table_butter);
//		MAPPING.put("game_table_honey",		R.drawable.game_table_honey);
//		MAPPING.put("game_table_milk",		R.drawable.game_table_milk);
//		MAPPING.put("game_table_coffee",	R.drawable.game_table_coffee);
//		MAPPING.put("game_table_plate_b",	R.drawable.game_table_plate_b);
//		MAPPING.put("game_table_waffle_lv1_normal",	R.drawable.game_table_waffle_lv1_normal);
//		MAPPING.put("game_table_waffle_lv1_work_1",	R.drawable.game_table_waffle_lv1_work_1);
//		MAPPING.put("game_table_waffle_lv1_work_2",	R.drawable.game_table_waffle_lv1_work_2);
//		MAPPING.put("game_table_waffle_lv2_normal",	R.drawable.game_table_waffle_lv2_normal);
//		MAPPING.put("game_table_waffle_lv2_work_1",	R.drawable.game_table_waffle_lv2_work_1);
//		MAPPING.put("game_table_waffle_lv2_work_2",	R.drawable.game_table_waffle_lv2_work_2);
//		MAPPING.put("popup_three_bg",		R.drawable.popup_three_bg);
//		MAPPING.put("popup_five_bg",		R.drawable.popup_five_bg);
//		MAPPING.put("popup_empty_bg",		R.drawable.popup_empty_bg);
//		MAPPING.put("popup_full_bg",		R.drawable.popup_full_bg);
//		MAPPING.put("store_mask_00",		R.drawable.store_mask_00);
//		MAPPING.put("store_mask_01",		R.drawable.store_mask_01);
//		MAPPING.put("store_mask_02",		R.drawable.store_mask_02);
//		MAPPING.put("store_mask_03",		R.drawable.store_mask_03);
//		MAPPING.put("store_mask_04",		R.drawable.store_mask_04);
//		MAPPING.put("store_mask_05",		R.drawable.store_mask_05);
//		MAPPING.put("store_mask_06",		R.drawable.store_mask_06);
//		MAPPING.put("store_mask_07",		R.drawable.store_mask_07);
//		MAPPING.put("store_mask_08",		R.drawable.store_mask_08);
//		MAPPING.put("store_mask_09",		R.drawable.store_mask_09);
//		MAPPING.put("store_mask_10",		R.drawable.store_mask_10);
//		MAPPING.put("store_mask_11",		R.drawable.store_mask_11);
//		
//		// tutorial 1-1
//		MAPPING.put("game_text_4_tutorial",		R.drawable.game_text_4_tutorial);
//		MAPPING.put("tutorial_frame_big_001",	R.drawable.tutorial_frame_big_001);
//		MAPPING.put("tutorial_1_1_text_001",	R.drawable.tutorial_1_1_text_001);
//		MAPPING.put("tutorial_1_1_text_002",	R.drawable.tutorial_1_1_text_002);
//		MAPPING.put("tutorial_1_1_text_003",	R.drawable.tutorial_1_1_text_003);
//		MAPPING.put("tutorial_1_1_text_004",	R.drawable.tutorial_1_1_text_004);
//		
//		MAPPING.put("tutorial_frame_ok",		R.drawable.tutorial_frame_ok);
//		MAPPING.put("tutorial_frame_ok_pressed",R.drawable.tutorial_frame_ok_pressed);
//		
//		MAPPING.put("tutorial_sign_arrow_001",	R.drawable.tutorial_sign_arrow_001);
//		MAPPING.put("tutorial_sign_tap_001",	R.drawable.tutorial_sign_tap_001);
//		MAPPING.put("tutorial_sign_tap_003",	R.drawable.tutorial_sign_tap_003);
//		
//		MAPPING.put("tutorial_bonnie_normal_1",	R.drawable.tutorial_bonnie_normal_1);
//		MAPPING.put("tutorial_bonnie_normal_2",	R.drawable.tutorial_bonnie_normal_2);
//		MAPPING.put("tutorial_bonnie_normal_3",	R.drawable.tutorial_bonnie_normal_3);
//		MAPPING.put("tutorial_bonnie_normal_4",	R.drawable.tutorial_bonnie_normal_4);
//		MAPPING.put("tutorial_bonnie_normal_5",	R.drawable.tutorial_bonnie_normal_5);
//		
//		MAPPING.put("tutorial_bonnie_great_1",	R.drawable.tutorial_bonnie_great_1);
//		MAPPING.put("tutorial_bonnie_great_2",	R.drawable.tutorial_bonnie_great_2);
//		
//		// order bubble
//		MAPPING.put("game_order_bg",			R.drawable.game_order_bg);
//		MAPPING.put("game_order_think_1",		R.drawable.game_order_think_1);
//		MAPPING.put("game_order_think_2",		R.drawable.game_order_think_2);
//		MAPPING.put("game_order_think_3",		R.drawable.game_order_think_3);
//		
//		// customer: jogging girl
//		MAPPING.put("game_guest_01_w1_normal_1",	R.drawable.game_guest_01_w1_normal_1);
//		MAPPING.put("game_guest_01_w1_normal_2",	R.drawable.game_guest_01_w1_normal_2);
//		MAPPING.put("game_guest_01_w1_normal_3",	R.drawable.game_guest_01_w1_normal_3);
//		
//		MAPPING.put("game_guest_01_r1_excited_1",	R.drawable.game_guest_01_r1_excited_1);
//		MAPPING.put("game_guest_01_r1_excited_2",	R.drawable.game_guest_01_r1_excited_2);
//		MAPPING.put("game_guest_01_r1_excited_3",	R.drawable.game_guest_01_r1_excited_2);
//		
//		MAPPING.put("game_guest_01_w2_unhappy_1",	R.drawable.game_guest_01_w2_unhappy_1);
//		MAPPING.put("game_guest_01_w2_unhappy_2",	R.drawable.game_guest_01_w2_unhappy_2);
//		MAPPING.put("game_guest_01_w2_unhappy_3",	R.drawable.game_guest_01_w2_unhappy_3);
//		MAPPING.put("game_guest_01_w2_unhappy_4",	R.drawable.game_guest_01_w2_unhappy_4);
//		MAPPING.put("game_guest_01_w2_unhappy_5",	R.drawable.game_guest_01_w2_unhappy_5);
//		
//		MAPPING.put("game_guest_01_w3_angry_1",		R.drawable.game_guest_01_w3_angry_1);
//		MAPPING.put("game_guest_01_w3_angry_2",		R.drawable.game_guest_01_w3_angry_2);
//		
//		// customer: working man
//		MAPPING.put("game_guest_02_w1_normal_1",	R.drawable.game_guest_02_w1_normal_1);
//		MAPPING.put("game_guest_02_w1_normal_2",	R.drawable.game_guest_02_w1_normal_2);
//		MAPPING.put("game_guest_02_w1_normal_3",	R.drawable.game_guest_02_w1_normal_3);
//		
//		MAPPING.put("game_guest_02_r1_excited_1",	R.drawable.game_guest_02_r1_excited_1);
//		MAPPING.put("game_guest_02_r1_excited_2",	R.drawable.game_guest_02_r1_excited_2);
//		
//		MAPPING.put("game_guest_02_w2_unhappy_1",	R.drawable.game_guest_02_w2_unhappy_1);
//		MAPPING.put("game_guest_02_w2_unhappy_2",	R.drawable.game_guest_02_w2_unhappy_2);
//		
//		MAPPING.put("game_guest_02_w3_angry_1",		R.drawable.game_guest_02_w3_angry_1);
//		MAPPING.put("game_guest_02_w3_angry_2",		R.drawable.game_guest_02_w3_angry_2);
//		
//		// customer: balloon boy
//		MAPPING.put("game_guest_04_w1_normal_1",	R.drawable.game_guest_04_w1_normal_1);
//		MAPPING.put("game_guest_04_w1_normal_2",	R.drawable.game_guest_04_w1_normal_2);
//		MAPPING.put("game_guest_04_w1_normal_3",	R.drawable.game_guest_04_w1_normal_3);
//		
//		MAPPING.put("game_guest_04_r1_excited_1",	R.drawable.game_guest_04_r1_excited_1);
//		MAPPING.put("game_guest_04_r1_excited_2",	R.drawable.game_guest_04_r1_excited_2);
//		MAPPING.put("game_guest_04_r1_excited_3",	R.drawable.game_guest_04_r1_excited_2);
//		
//		MAPPING.put("game_guest_04_w2_unhappy_1",	R.drawable.game_guest_04_w2_unhappy_1);
//		MAPPING.put("game_guest_04_w2_unhappy_2",	R.drawable.game_guest_04_w2_unhappy_2);
//		MAPPING.put("game_guest_04_w2_unhappy_3",	R.drawable.game_guest_04_w2_unhappy_3);
//		
//		MAPPING.put("game_guest_04_w3_angry_1",		R.drawable.game_guest_04_w3_angry_1);
//		MAPPING.put("game_guest_04_w3_angry_2",		R.drawable.game_guest_04_w3_angry_2);
//		
//		// food: bread
//		MAPPING.put("food_3_bagel_b",		R.drawable.food_3_bagel_b);
//		MAPPING.put("food_3_bagel_m",		R.drawable.food_3_bagel_m);
//		MAPPING.put("food_3_croissant_b",	R.drawable.food_3_croissant_b);
//		MAPPING.put("food_3_croissant_m",	R.drawable.food_3_croissant_m);
//		MAPPING.put("food_2_waffle_circular_b",	R.drawable.food_2_waffle_circular_b);
//		MAPPING.put("food_2_waffle_circular_m",	R.drawable.food_2_waffle_circular_m);
//		MAPPING.put("food_2_waffle_square_b",	R.drawable.food_2_waffle_square_b);
//		MAPPING.put("food_2_waffle_square_m",	R.drawable.food_2_waffle_square_m);
//		
//		// food: sauce
//		MAPPING.put("food_6_butter_b",		R.drawable.food_6_butter_b);
//		MAPPING.put("food_6_butter_m",		R.drawable.food_6_butter_m);
//		MAPPING.put("food_6_honey_b",		R.drawable.food_6_honey_b);
//		MAPPING.put("food_6_honey_m",		R.drawable.food_6_honey_m);
//		MAPPING.put("food_6_tomato_b",		R.drawable.food_6_tomato_b);
//		MAPPING.put("food_6_tomato_m",		R.drawable.food_6_tomato_m);
//		
//		// food: beverage
//		MAPPING.put("food_7_milk_b",		R.drawable.food_7_milk_b);
//		MAPPING.put("food_7_milk_m",		R.drawable.food_7_milk_m);
//		MAPPING.put("food_7_coffee_b",		R.drawable.food_7_coffee_b);
//		MAPPING.put("food_7_coffee_m",		R.drawable.food_7_coffee_m);
	}

}
