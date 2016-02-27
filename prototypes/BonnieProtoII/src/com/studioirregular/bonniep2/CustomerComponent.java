package com.studioirregular.bonniep2;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.util.Pair;

import com.studioirregular.bonnie.levelsystem.Level;

public class CustomerComponent implements Component {

	// customer type
	public static final int JOGGING_GIRL		= 1;
	public static final int WORKING_MAN			= 2;
	public static final int BALLOON_BOY			= 3;
	public static final int GIRL_WITH_DOG		= 4;
	public static final int FOOD_CRITIC			= 5;
	public static final int SUPERSTAR_LADY		= 6;
	public static final int PHYSICIST			= 7;
	public static final int TRAMP				= 8;
	public static final int GRANNY				= 9;
	public static final int SUPERSTAR_MAN		= 10;
	
	public static CustomerComponent newInstance(String id, int type, EventHost host) {
		if (type == PHYSICIST) {
			return new CustomerPhysicistComponent(id, host);
		} else {
			return new CustomerComponent(id, type, host);
		}
	}
	
	public static int getCustomerType(Level.Customer customer) {
		return customer.customerType;
	}
	
	protected static final int PATIENCE_DROP_TIME = 10000;
	
	private static final String TAG = "customer-component";
	
	protected final int customerType;
	private final String id;
	protected EventHost host;
	
	int movingSpeed;	// pixel/second
	int orderingPeriod;	// in millisecond
	int totalPatience;
	int tip;
	
	private boolean isWaiting;
	private int currentPatience;
	private boolean tutorialMode = false;
	private long elapsedTime;
	
	// frame animations
	protected List< Pair<String, Long> > frameListNormal = new ArrayList< Pair<String, Long> >();
	protected List< Pair<String, Long> > frameListExcited = new ArrayList< Pair<String, Long> >();
	protected List< Pair<String, Long> > frameListUnhappy = new ArrayList< Pair<String, Long> >();
	protected List< Pair<String, Long> > frameListAngry = new ArrayList< Pair<String, Long> >();
	protected List< Pair<String, Long> > frameListSick = new ArrayList< Pair<String, Long> >();
	
	protected CustomerComponent(String id, int customerType, EventHost host) {
		this.id = id;
		this.customerType = customerType;
		this.host = host;
		
		setup();
		currentPatience = totalPatience;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	public void startWaiting() {
		elapsedTime = 0;
		currentPatience = totalPatience;
		isWaiting = true;
	}
	
	public boolean isWaiting() {
		return isWaiting;
	}
	
	public void stopWaiting() {
		isWaiting = false;
	}
	
	public void setTutorialMode(boolean tutorialMode) {
		this.tutorialMode = tutorialMode;
	}
	
	public void update(long timeDiff) {
		if  (!tutorialMode && isWaiting) {
			elapsedTime += timeDiff;
//			Log.w(TAG, "update elapsed:" + elapsedTime);
			int patienceDrop = (int)(elapsedTime / PATIENCE_DROP_TIME);
			if (patienceDrop <= totalPatience) {
				currentPatience = totalPatience - patienceDrop;
			} else {
				currentPatience = 0;
			}
		}
	}
	
	public int getCurrentPatience() {
		return currentPatience;
	}
	
	public int getPatienceDropt() {
		return totalPatience - currentPatience;
	}
	
	public void increasePatience(int diff) {
		elapsedTime -= diff*PATIENCE_DROP_TIME;
		if (elapsedTime < 0) {
			elapsedTime = 0;
		}
 	}
	
	protected int MOVING_SPPED_MULTIPLIER = 80;
	
	protected void setup() {
		switch (customerType) {
		case JOGGING_GIRL:
			movingSpeed = 4 * MOVING_SPPED_MULTIPLIER;
			orderingPeriod = 1000;
			totalPatience = 4;
			tip = 3;
			
			frameListNormal.add(Pair.create("game_guest_01_w1_normal_1", 300L));
			frameListNormal.add(Pair.create("game_guest_01_w1_normal_2", 300L));
			frameListNormal.add(Pair.create("game_guest_01_w1_normal_3", 300L));
			frameListNormal.add(Pair.create("game_guest_01_w1_normal_2", 300L));
			
			frameListExcited.add(Pair.create("game_guest_01_r1_excited_1", 250L));
			frameListExcited.add(Pair.create("game_guest_01_r1_excited_2", 250L));
			frameListExcited.add(Pair.create("game_guest_01_r1_excited_1", 250L));
			frameListExcited.add(Pair.create("game_guest_01_r1_excited_3", 250L));
			
			frameListUnhappy.add(Pair.create("game_guest_01_w2_unhappy_1", 500L));
			frameListUnhappy.add(Pair.create("game_guest_01_w2_unhappy_2", 500L));
			frameListUnhappy.add(Pair.create("game_guest_01_w2_unhappy_1", 500L));
			frameListUnhappy.add(Pair.create("game_guest_01_w2_unhappy_2", 500L));
			frameListUnhappy.add(Pair.create("game_guest_01_w2_unhappy_3", 100L));
			frameListUnhappy.add(Pair.create("game_guest_01_w2_unhappy_2", 50L));
			frameListUnhappy.add(Pair.create("game_guest_01_w2_unhappy_5", 300L));
			frameListUnhappy.add(Pair.create("game_guest_01_w2_unhappy_4", 100L));
			frameListUnhappy.add(Pair.create("game_guest_01_w2_unhappy_5", 100L));
			frameListUnhappy.add(Pair.create("game_guest_01_w2_unhappy_4", 100L));
			
			frameListAngry.add(Pair.create("game_guest_01_w3_angry_1", 200L));
			frameListAngry.add(Pair.create("game_guest_01_w3_angry_2", 200L));
			
			frameListSick.add(Pair.create("game_guest_01_s1_sick_1", 200L));
			frameListSick.add(Pair.create("game_guest_01_s1_sick_2", 200L));
			frameListSick.add(Pair.create("game_guest_01_s1_sick_3", 200L));
			break;
		case WORKING_MAN:
			movingSpeed = 5 * MOVING_SPPED_MULTIPLIER;
			orderingPeriod = 1000;
			totalPatience = 4;
			tip = 4;
			
			frameListNormal.add(Pair.create("game_guest_02_w1_normal_1", 1000L));
			frameListNormal.add(Pair.create("game_guest_02_w1_normal_2", 250L));
			frameListNormal.add(Pair.create("game_guest_02_w1_normal_3", 250L));
			frameListNormal.add(Pair.create("game_guest_02_w1_normal_2", 250L));
			frameListNormal.add(Pair.create("game_guest_02_w1_normal_3", 250L));
			frameListNormal.add(Pair.create("game_guest_02_w1_normal_2", 250L));
			
			frameListExcited.add(Pair.create("game_guest_02_r1_excited_1", 200L));
			frameListExcited.add(Pair.create("game_guest_02_r1_excited_2", 600L));
			
			frameListUnhappy.add(Pair.create("game_guest_02_w2_unhappy_2", 200L));
			frameListUnhappy.add(Pair.create("game_guest_02_w2_unhappy_1", 200L));
			frameListUnhappy.add(Pair.create("game_guest_02_w2_unhappy_2", 200L));
			frameListUnhappy.add(Pair.create("game_guest_02_w2_unhappy_1", 1000L));
			
			frameListAngry.add(Pair.create("game_guest_02_w3_angry_1", 200L));
			frameListAngry.add(Pair.create("game_guest_02_w3_angry_2", 200L));
			
			frameListSick.add(Pair.create("game_guest_02_s1_sick_1", 500L));
			frameListSick.add(Pair.create("game_guest_02_s1_sick_2", 200L));
			break;
		case BALLOON_BOY:
			movingSpeed = 3 * MOVING_SPPED_MULTIPLIER;
			orderingPeriod = 2000;
			totalPatience = 5;
			tip = 1;
			
			frameListNormal.add(Pair.create("game_guest_04_w1_normal_1", 250L));
			frameListNormal.add(Pair.create("game_guest_04_w1_normal_2", 250L));
			frameListNormal.add(Pair.create("game_guest_04_w1_normal_3", 250L));
			frameListNormal.add(Pair.create("game_guest_04_w1_normal_2", 250L));
			
			frameListExcited.add(Pair.create("game_guest_04_r1_excited_1", 200L));
			frameListExcited.add(Pair.create("game_guest_04_r1_excited_2", 200L));
			frameListExcited.add(Pair.create("game_guest_04_r1_excited_3", 200L));
			
			frameListUnhappy.add(Pair.create("game_guest_04_w2_unhappy_1", 250L));
			frameListUnhappy.add(Pair.create("game_guest_04_w2_unhappy_2", 250L));
			frameListUnhappy.add(Pair.create("game_guest_04_w2_unhappy_1", 250L));
			frameListUnhappy.add(Pair.create("game_guest_04_w2_unhappy_2", 250L));
			frameListUnhappy.add(Pair.create("game_guest_04_w2_unhappy_3", 250L));
			
			frameListAngry.add(Pair.create("game_guest_04_w3_angry_1", 250L));
			frameListAngry.add(Pair.create("game_guest_04_w3_angry_2", 250L));
			
			frameListSick.add(Pair.create("game_guest_04_s1_sick_1", 200L));
			frameListSick.add(Pair.create("game_guest_04_s1_sick_2", 200L));
			frameListSick.add(Pair.create("game_guest_04_s1_sick_1", 200L));
			frameListSick.add(Pair.create("game_guest_04_s1_sick_3", 200L));
			break;
		case GIRL_WITH_DOG:
			movingSpeed = 2 * MOVING_SPPED_MULTIPLIER;
			orderingPeriod = 2000;
			totalPatience = 5;
			tip = 2;
			
			frameListNormal.add(Pair.create("game_guest_05_w1_normal_1", 250L));
			frameListNormal.add(Pair.create("game_guest_05_w1_normal_2", 250L));
			frameListNormal.add(Pair.create("game_guest_05_w1_normal_3", 250L));
			frameListNormal.add(Pair.create("game_guest_05_w1_normal_2", 250L));
			
			frameListExcited.add(Pair.create("game_guest_05_r1_excited_1", 270L));
			frameListExcited.add(Pair.create("game_guest_05_r1_excited_2", 270L));
//			frameListExcited.add(Pair.create("game_guest_05_r1_excited_3", 200L));
			
			frameListUnhappy.add(Pair.create("game_guest_05_w2_unhappy_1", 250L));
			frameListUnhappy.add(Pair.create("game_guest_05_w2_unhappy_2", 250L));
			frameListUnhappy.add(Pair.create("game_guest_05_w2_unhappy_1", 250L));
			frameListUnhappy.add(Pair.create("game_guest_05_w2_unhappy_2", 250L));
			frameListUnhappy.add(Pair.create("game_guest_05_w2_unhappy_3", 250L));
			
			frameListAngry.add(Pair.create("game_guest_05_w3_angry_1", 250L));
			frameListAngry.add(Pair.create("game_guest_05_w3_angry_2", 250L));
			
			frameListSick.add(Pair.create("game_guest_05_s1_sick_1", 200L));
			frameListSick.add(Pair.create("game_guest_05_s1_sick_2", 200L));
			frameListSick.add(Pair.create("game_guest_05_s1_sick_3", 200L));
			frameListSick.add(Pair.create("game_guest_05_s1_sick_2", 200L));
			break;
		case GRANNY:
			movingSpeed = 1 * MOVING_SPPED_MULTIPLIER;
			orderingPeriod = 4000;
			totalPatience = 5;
			tip = 3;
			
			frameListNormal.add(Pair.create("game_guest_11_w1_normal_1", 400L));
			frameListNormal.add(Pair.create("game_guest_11_w1_normal_2", 100L));
			frameListNormal.add(Pair.create("game_guest_11_w1_normal_3", 100L));
			frameListNormal.add(Pair.create("game_guest_11_w1_normal_2", 100L));
			frameListNormal.add(Pair.create("game_guest_11_w1_normal_3", 100L));
			frameListNormal.add(Pair.create("game_guest_11_w1_normal_2", 100L));
			
			frameListExcited.add(Pair.create("game_guest_11_r1_excited_1", 250L));
			frameListExcited.add(Pair.create("game_guest_11_r1_excited_2", 400L));
			
			frameListUnhappy.add(Pair.create("game_guest_11_w2_unhappy_1", 500L));
			frameListUnhappy.add(Pair.create("game_guest_11_w2_unhappy_2", 500L));
			
			frameListAngry.add(Pair.create("game_guest_11_w3_angry_1", 200L));
			frameListAngry.add(Pair.create("game_guest_11_w3_angry_2", 200L));
			
			frameListSick.add(Pair.create("game_guest_11_s1_sick_1", 250L));
			frameListSick.add(Pair.create("game_guest_11_s1_sick_2", 250L));
			break;
		case SUPERSTAR_MAN:
			movingSpeed = 5 * MOVING_SPPED_MULTIPLIER;
			orderingPeriod = 0;
			totalPatience = 3;
			tip = 3;
			
			frameListNormal.add(Pair.create("game_guest_12_w1_normal_1", 200L));
			frameListNormal.add(Pair.create("game_guest_12_w1_normal_2", 100L));
			frameListNormal.add(Pair.create("game_guest_12_w1_normal_3", 200L));
			frameListNormal.add(Pair.create("game_guest_12_w1_normal_2", 200L));
			frameListNormal.add(Pair.create("game_guest_12_w1_normal_3", 200L));
			frameListNormal.add(Pair.create("game_guest_12_w1_normal_2", 100L));
			frameListNormal.add(Pair.create("game_guest_12_w1_normal_1", 100L));
			frameListNormal.add(Pair.create("game_guest_12_w1_normal_4", 200L));
			
			frameListExcited.add(Pair.create("game_guest_12_r1_excited_1", 200L));
			frameListExcited.add(Pair.create("game_guest_12_r1_excited_2", 200L));
			
			frameListUnhappy.add(Pair.create("game_guest_12_w2_unhappy_1", 200L));
			frameListUnhappy.add(Pair.create("game_guest_12_w2_unhappy_2", 200L));
			frameListUnhappy.add(Pair.create("game_guest_12_w2_unhappy_3", 200L));
			frameListUnhappy.add(Pair.create("game_guest_12_w2_unhappy_4", 200L));
			
			frameListAngry.add(Pair.create("game_guest_12_w3_angry_1", 200L));
			frameListAngry.add(Pair.create("game_guest_12_w3_angry_2", 200L));
			
			frameListSick.add(Pair.create("game_guest_12_s1_sick_1", 200L));
			frameListSick.add(Pair.create("game_guest_12_s1_sick_2", 200L));
			frameListSick.add(Pair.create("game_guest_12_s1_sick_3", 200L));
			frameListSick.add(Pair.create("game_guest_12_s1_sick_4", 200L));
			break;
		case PHYSICIST:
			movingSpeed = 1 * MOVING_SPPED_MULTIPLIER;
			orderingPeriod = 3000;
			totalPatience = 4;
			tip = 2;
			
			frameListNormal.add(Pair.create("game_guest_08_w1_normal_1", 200L));
			frameListNormal.add(Pair.create("game_guest_08_w1_normal_2", 100L));
			frameListNormal.add(Pair.create("game_guest_08_w1_normal_3", 200L));
			frameListNormal.add(Pair.create("game_guest_08_w1_normal_4", 100L));
			frameListNormal.add(Pair.create("game_guest_08_w1_normal_5", 200L));
			
			frameListExcited.add(Pair.create("game_guest_08_r1_excited_1", 200L));
			frameListExcited.add(Pair.create("game_guest_08_r1_excited_2", 200L));
			frameListExcited.add(Pair.create("game_guest_08_r1_excited_3", 200L));
			frameListExcited.add(Pair.create("game_guest_08_r1_excited_4", 200L));
			
			frameListUnhappy.add(Pair.create("game_guest_08_w2_unhappy_1", 150L));
			frameListUnhappy.add(Pair.create("game_guest_08_w2_unhappy_2", 150L));
			
			frameListAngry.add(Pair.create("game_guest_08_w3_angry_1", 100L));
			frameListAngry.add(Pair.create("game_guest_08_w3_angry_2", 100L));
			
			frameListSick.add(Pair.create("game_guest_08_s1_sick_1", 150L));
			frameListSick.add(Pair.create("game_guest_08_s1_sick_2", 100L));
			frameListSick.add(Pair.create("game_guest_08_s1_sick_3", 150L));
			frameListSick.add(Pair.create("game_guest_08_s1_sick_2", 100L));
			break;
		case SUPERSTAR_LADY:
			movingSpeed = 2 * MOVING_SPPED_MULTIPLIER;
			orderingPeriod = 2000;
			totalPatience = 5;
			tip = 5;
			
			frameListNormal.add(Pair.create("game_guest_07_w1_normal_1", 500L));
			frameListNormal.add(Pair.create("game_guest_07_w1_normal_2", 500L));
			frameListNormal.add(Pair.create("game_guest_07_w1_normal_1", 500L));
			frameListNormal.add(Pair.create("game_guest_07_w1_normal_2", 500L));
			frameListNormal.add(Pair.create("game_guest_07_w1_normal_1", 500L));
			frameListNormal.add(Pair.create("game_guest_07_w1_normal_3", 200L));
			frameListNormal.add(Pair.create("game_guest_07_w1_normal_4", 200L));
			frameListNormal.add(Pair.create("game_guest_07_w1_normal_3", 200L));
			frameListNormal.add(Pair.create("game_guest_07_w1_normal_4", 500L));
			
			frameListExcited.add(Pair.create("game_guest_07_r1_excited_1", 200L));
			frameListExcited.add(Pair.create("game_guest_07_r1_excited_2", 500L));
			
			frameListUnhappy.add(Pair.create("game_guest_07_w2_unhappy_1", 500L));
			frameListUnhappy.add(Pair.create("game_guest_07_w2_unhappy_2", 500L));
			
			frameListAngry.add(Pair.create("game_guest_07_w3_angry_1", 250L));
			frameListAngry.add(Pair.create("game_guest_07_w3_angry_2", 250L));
			
			frameListSick.add(Pair.create("game_guest_07_s1_sick_1", 500L));
			frameListSick.add(Pair.create("game_guest_07_s1_sick_2", 500L));
			break;
		case FOOD_CRITIC:
			movingSpeed = 3 * MOVING_SPPED_MULTIPLIER;
			orderingPeriod = 0;
			totalPatience = 3;
			tip = 3;
			
			frameListNormal.add(Pair.create("game_guest_06_w1_normal_2", 500L));
			frameListNormal.add(Pair.create("game_guest_06_w1_normal_1", 500L));
			frameListNormal.add(Pair.create("game_guest_06_w1_normal_2", 500L));
			frameListNormal.add(Pair.create("game_guest_06_w1_normal_1", 500L));
			frameListNormal.add(Pair.create("game_guest_06_w1_normal_4", 200L));
			frameListNormal.add(Pair.create("game_guest_06_w1_normal_3", 100L));
			frameListNormal.add(Pair.create("game_guest_06_w1_normal_4", 100L));
			frameListNormal.add(Pair.create("game_guest_06_w1_normal_3", 100L));
			frameListNormal.add(Pair.create("game_guest_06_w1_normal_4", 100L));
			frameListNormal.add(Pair.create("game_guest_06_w1_normal_3", 200L));
			
			frameListExcited.add(Pair.create("game_guest_06_r1_excited_1", 200L));
			frameListExcited.add(Pair.create("game_guest_06_r1_excited_2", 500L));
			
			frameListUnhappy.add(Pair.create("game_guest_06_w2_unhappy_1", 200L));
			frameListUnhappy.add(Pair.create("game_guest_06_w2_unhappy_2", 100L));
			frameListUnhappy.add(Pair.create("game_guest_06_w2_unhappy_3", 200L));
			frameListUnhappy.add(Pair.create("game_guest_06_w2_unhappy_2", 100L));
			
			frameListAngry.add(Pair.create("game_guest_06_w3_angry_1", 200L));
			frameListAngry.add(Pair.create("game_guest_06_w3_angry_2", 200L));
			
			frameListSick.add(Pair.create("game_guest_06_s1_sick_1", 200L));
			frameListSick.add(Pair.create("game_guest_06_s1_sick_2", 200L));
			frameListSick.add(Pair.create("game_guest_06_s1_sick_3", 200L));
			frameListSick.add(Pair.create("game_guest_06_s1_sick_2", 200L));
			break;
		case TRAMP:
			movingSpeed = 3 * MOVING_SPPED_MULTIPLIER;
			orderingPeriod = 3000;
			totalPatience = 5;
			tip = 0;
			
			frameListNormal.add(Pair.create("game_guest_09_w1_normal_1", 200L));
			frameListNormal.add(Pair.create("game_guest_09_w1_normal_2", 200L));
			frameListNormal.add(Pair.create("game_guest_09_w1_normal_3", 200L));
			frameListNormal.add(Pair.create("game_guest_09_w1_normal_1", 200L));
			frameListNormal.add(Pair.create("game_guest_09_w1_normal_2", 200L));
			frameListNormal.add(Pair.create("game_guest_09_w1_normal_3", 200L));
			frameListNormal.add(Pair.create("game_guest_09_w1_normal_1", 200L));
			frameListNormal.add(Pair.create("game_guest_09_w1_normal_2", 200L));
			frameListNormal.add(Pair.create("game_guest_09_w1_normal_3", 200L));
			frameListNormal.add(Pair.create("game_guest_09_w1_normal_4", 200L));
			frameListNormal.add(Pair.create("game_guest_09_w1_normal_5", 200L));
			frameListNormal.add(Pair.create("game_guest_09_w1_normal_6", 200L));
			frameListNormal.add(Pair.create("game_guest_09_w1_normal_4", 200L));
			frameListNormal.add(Pair.create("game_guest_09_w1_normal_5", 200L));
			frameListNormal.add(Pair.create("game_guest_09_w1_normal_6", 200L));
			
			frameListExcited.add(Pair.create("game_guest_09_r1_excited_1", 250L));
			frameListExcited.add(Pair.create("game_guest_09_r1_excited_2", 250L));
			frameListExcited.add(Pair.create("game_guest_09_r1_excited_3", 250L));
			frameListExcited.add(Pair.create("game_guest_09_r1_excited_2", 250L));
			
			frameListUnhappy.add(Pair.create("game_guest_09_w2_unhappy_1", 200L));
			frameListUnhappy.add(Pair.create("game_guest_09_w2_unhappy_2", 200L));
			frameListUnhappy.add(Pair.create("game_guest_09_w2_unhappy_1", 200L));
			frameListUnhappy.add(Pair.create("game_guest_09_w2_unhappy_2", 200L));
			frameListUnhappy.add(Pair.create("game_guest_09_w2_unhappy_1", 200L));
			frameListUnhappy.add(Pair.create("game_guest_09_w2_unhappy_2", 200L));
			frameListUnhappy.add(Pair.create("game_guest_09_w2_unhappy_1", 200L));
			frameListUnhappy.add(Pair.create("game_guest_09_w2_unhappy_2", 200L));
			frameListUnhappy.add(Pair.create("game_guest_09_w2_unhappy_3", 200L));
			
			frameListAngry.add(Pair.create("game_guest_09_w3_angry_1", 250L));
			frameListAngry.add(Pair.create("game_guest_09_w3_angry_2", 250L));
			break;
		default:
			Log.e(TAG, "unknown customer type:" + customerType);
			break;
		}
	}

}
