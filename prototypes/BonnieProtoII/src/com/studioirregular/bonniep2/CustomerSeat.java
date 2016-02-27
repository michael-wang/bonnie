package com.studioirregular.bonniep2;

import java.util.Random;


public class CustomerSeat {

	public static final int INVALID_SEAT = -1;
	public static final int NUMBER_OF_SEATS = 4;
	
//	private static final String TAG = "customer-seats";
	
	public static void cleanSeats() {
		for (int i = 0; i < NUMBER_OF_SEATS; i++) {
			seats[i] = false;
		}
	}
	
	private static int[] emptySeats = new int[NUMBER_OF_SEATS];
	private static Random random = new Random(System.currentTimeMillis());
	public static int randomlyPickSeat() {
		int count = 0;
		for (int i = 0; i < NUMBER_OF_SEATS; i++) {
			if (seats[i] == false) {
				emptySeats[count] = i;
				count++;
			}
		}
		
		if (count == 0) {
			return INVALID_SEAT;
		}
		
		int seatIndex = random.nextInt(count);
		return emptySeats[seatIndex];
	}
	
	public static boolean takeSeat(int seat) {
		if (seats[seat] == true) {
			return false;
		}
		
		seats[seat] = true;
//		Log.e(TAG, "takeSeat(" + seat + "): " + seats[0] + "|" + seats[1] + "|" + seats[2] + "|" + seats[3]);
		return true;
	}
	
	public static void leaveSeat(int seat) {
		seats[seat] = false;
//		Log.e(TAG, "leaveSeat(" + seat + "): " + seats[0] + "|" + seats[1] + "|" + seats[2] + "|" + seats[3]);
	}
	
	public static void clearSeats() {
		for (int i = 0; i < NUMBER_OF_SEATS; i++) {
			seats[i] = false;
		}
	}
	
	public static float getCustomerX(int seat) {
		return coordCustomer[seat*2];
	}
	
	public static float getCustomerY(int seat) {
		return coordCustomer[seat*2 + 1];
	}
	
	public static float getOrderBubbleX(int seat) {
		return coordOrderBubble[seat*2];
	}
	
	public static float getOrderBubbleY(int seat) {
		return coordOrderBubble[seat*2 + 1];
	}
	
	public static float getFoodBaseX(int seat) {
		return coordOrderBubble[seat*2] + FOOD_OFFSET_X;
	}
	
	public static float getFoodBaseY(int seat) {
		return coordOrderBubble[seat*2 + 1] + FOOD_OFFSET_Y;
	}
	
	public static float getBeverageX(int seat) {
		return coordOrderBubble[seat*2] + BEVERAGE_OFFSET_X;
	}
	
	public static float getBeverageY(int seat) {
		return coordOrderBubble[seat*2 + 1] + BEVERAGE_OFFSET_Y;
	}
	
	private static float[] coordCustomer = {
		10, 67,
		192, 67,
		374, 67,
		556, 67
	};
	
	private static float[] coordOrderBubble = {
		0, 62,
		180, 62,
		360, 62,
		540, 62
	};
	
	private static final float FOOD_OFFSET_X = 8.0f;
	private static final float FOOD_OFFSET_Y = 29.0f;
	private static final float BEVERAGE_OFFSET_X = 58.0f;
	private static final float BEVERAGE_OFFSET_Y = 51.0f;
	
	private static boolean[] seats =new boolean[NUMBER_OF_SEATS];
}
