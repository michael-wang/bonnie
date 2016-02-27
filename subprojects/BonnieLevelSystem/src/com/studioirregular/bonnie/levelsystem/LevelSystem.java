package com.studioirregular.bonnie.levelsystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;
import android.util.Log;

import com.studioirregular.bonnie.foodsystem.FoodSystem.Food;
import com.studioirregular.bonnie.levelsystem.Level.Customer;
import com.studioirregular.bonnie.levelsystem.Level.Period;
import com.studioirregular.bonnie.levelsystem.Level.WhatsNewDialog;

public class LevelSystem {

	private static final boolean DO_LOG = false;
	private static final String TAG = "level-system";
	
	public LevelSystem() {
		if (DO_LOG) Log.d(TAG, "LevelSystem");
	}
	
	public Level parse(XmlResourceParser input) throws XmlPullParserException, IOException {
		Level level = null;
		List<WhatsNewDialog> dialogs = new ArrayList<WhatsNewDialog>();
		List<Period> periods = new ArrayList<Period>();
		List<Customer> customers = new ArrayList<Customer>();
		
		int event = input.next();
		
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_TAG:
			{
				final String tagName = input.getName();
				if (DO_LOG) Log.d(TAG, "parse start tag:" + tagName);
				
				if (Level.TAG_NAME_LEVEL.equals(tagName)) {
					level = parseLevelTag(input);
				} else if (Level.TAG_NAME_WHATSNEWDIALOG.equals(tagName)) {
					WhatsNewDialog dialog = parseWhatsNewDialog(input);
					if (dialog != null) {
						dialogs.add(dialog);
					}
				} else if (Level.TAG_NAME_TABLE_ITEM_CONFIG.equals(tagName)) {
					parseTableConfig(input, level);
				} else if (Level.TAG_NAME_PERIOD.equals(tagName)) {
					Period period = parsePeriodTag(input);
					if (period != null) {
						periods.add(period);
					}
				} else if (Level.TAG_NAME_CUSTOMER.equals(tagName)) {
					Customer customer = parseCustomer(input);
					if (customer != null) {
						customers.add(customer);
					}
				}
				break;
			}
			case XmlPullParser.END_TAG:
			{
				final String tagName = input.getName();
				if (DO_LOG) Log.d(TAG, "parse end tag:" + tagName);
				break;
			}
			default:
				break;
			}
			event = input.next();
		}
		
		level.whatsNewDialogs = new WhatsNewDialog[dialogs.size()];
		dialogs.toArray(level.whatsNewDialogs);
		
		level.periods = new Period[periods.size()];
		periods.toArray(level.periods);
		
		level.customers = new Customer[customers.size()];
		customers.toArray(level.customers);
		
		return level;
	}
	
	public Level parseLevelTag(XmlResourceParser input) throws XmlPullParserException, IOException {
		assert (Level.TAG_NAME_LEVEL.equals(input.getName()));
		if (DO_LOG) Log.d(TAG, "parseLevelTag tagName:" + input.getName());
		
		Level result = null;
		
		int level = Level.LEVEL_INVALID_VALUE;
		int subLevel = Level.SUB_LEVEL_INVALID_VALUE;
		int type = Level.TYPE_INVALID_VALUE;
		int totalTime = Level.TOTAL_TIME_INVALID_VALUE;
		
		final int attributeCount = input.getAttributeCount();
		for (int i = 0; i < attributeCount; i++) {
			final String attrName = input.getAttributeName(i);
			if (Level.ATTR_NAME_LEVEL_LEVEL.equals(attrName)) {
				level = input.getAttributeIntValue(i, 0);
			} else if (Level.ATTR_NAME_LEVEL_SUBLEVEL.equals(attrName)) {
				subLevel = input.getAttributeIntValue(i, 0);
			} else if (Level.ATTR_NAME_LEVEL_TYPE.equals(attrName)) {
				type = input.getAttributeIntValue(i, 0);
			} else if (Level.ATTR_NAME_LEVEL_TOTAL_TIME.equals(attrName)) {
				totalTime = input.getAttributeIntValue(i, 0);
			} else {
				Log.w(TAG, "unknown attribute:" + attrName);
			}
		}
		
		if (Level.LEVEL_INVALID_VALUE != level
				&& Level.SUB_LEVEL_INVALID_VALUE != subLevel
				&& Level.TYPE_INVALID_VALUE != type
				&& Level.TOTAL_TIME_INVALID_VALUE != totalTime) {
			result = new Level(level, subLevel, type, totalTime);
		} else {
			Log.e(TAG, "cannot create level with param: level:" + level
					+ ", subLevel:" + subLevel + ", type:" + type
					+ ", totalTime:" + totalTime);
		}
		
		return result;
	}
	
	public WhatsNewDialog parseWhatsNewDialog(XmlResourceParser input) throws XmlPullParserException, IOException {
		assert Level.TAG_NAME_WHATSNEWDIALOG.equals(input.getName());
		
		final int attributeCount = input.getAttributeCount();
		if (attributeCount == 0) {
			Log.e(TAG, "expected attribute not found:" + Level.ATTR_NAME_WHATSNEWDIALOG_DRAWABLE);
			return null;
		}
		
		WhatsNewDialog result = new WhatsNewDialog();
		for (int i = 0; i < attributeCount; i++) {
			final String attrName = input.getAttributeName(i);
			if (Level.ATTR_NAME_WHATSNEWDIALOG_DRAWABLE.equals(attrName)) {
				result.drawableResourceName = input.getAttributeValue(i);
				break;
			}
		}
		
		if (result.drawableResourceName == null) {
			Log.e(TAG, "parseWhatsNewDialog cannot find attribute:" + Level.ATTR_NAME_WHATSNEWDIALOG_DRAWABLE);
			return null;
		}
		
		return result;
	}
	
	public void parseTableConfig(XmlResourceParser input, Level level) throws XmlPullParserException, IOException {
		assert Level.TAG_NAME_TABLE_ITEM_CONFIG.equals(input.getName());
		
		int event = input.next();
		boolean done = false;
		while (event != XmlPullParser.END_DOCUMENT && !done) {
			switch (event) {
			case XmlPullParser.START_TAG:
			{
				final String tagName = input.getName();
				if (DO_LOG) Log.d(TAG, "	parseTableConfigTag start tag:" + tagName);
				Integer item = Level.TABLE_ITEM_TAG_2_ID.get(tagName);
				if (item != null) {
					level.addTableItem(item);
				}
				break;
			}
			case XmlPullParser.END_TAG:
			{
				final String tagName = input.getName();
				if (DO_LOG) Log.d(TAG, "	parseTableConfig end tag:" + tagName);
				if (Level.TAG_NAME_TABLE_ITEM_CONFIG.equals(tagName)) {
					done = true;
				}
				break;
			}
			default:
				break;
			}
			
			if (!done) {
				event = input.next();
			}
		}
	}
	
	public Period parsePeriodTag(XmlResourceParser input) throws XmlPullParserException, IOException {
		assert Level.TAG_NAME_PERIOD.equals(input.getName());
		
		Period period = new Period();
		
		final int count = input.getAttributeCount();
		for (int i = 0; i < count; i++) {
			final String attrName = input.getAttributeName(i);
			if (Level.ATTR_NAME_PERDIO_START_FROM.equals(attrName)) {
				period.startFrom = input.getAttributeIntValue(i, 0) * MILLI_PER_SECOND;
			} else if (Level.ATTR_NAME_PERDIO_MIN_CUSTOMER_INTERVAL.equals(attrName)) {
				period.minCustomerInterval = input.getAttributeIntValue(i, 0) * MILLI_PER_SECOND;
			} else if (Level.ATTR_NAME_PERDIO_MAX_CUSTOMER_INTERVAL.equals(attrName)) {
				period.maxCustomerInterval = input.getAttributeIntValue(i, 0) * MILLI_PER_SECOND;
			} else {
				Log.w(TAG, "unknown attribute:" + attrName);
			}
		}
		
		return period;
	}
	
	public Customer parseCustomer(XmlResourceParser input) throws XmlPullParserException, IOException {
		assert Level.TAG_NAME_CUSTOMER.equals(input.getName());
		
		Customer customer = new Customer();
		
		final int count = input.getAttributeCount();
		for (int i = 0; i < count; i++) {
			final String attrName = input.getAttributeName(i);
			if (Level.ATTR_NAME_CUSTOMER_TYPE.equals(attrName)) {
				if (Level.TAG_2_CUSTOMER_TYPE.containsKey(input.getAttributeValue(i))) {
					customer.customerType = Level.TAG_2_CUSTOMER_TYPE.get(input.getAttributeValue(i));
				} else {
					Log.w(TAG, "parseCustomerTag unknown attribute value, name:" + attrName + ", value:" + input.getAttributeValue(i));
					customer.customerType = Level.CUSTOMER_TYPE_INVALID;
				}
			} else {
				Log.w(TAG, "parseCustomerTag unknown attribute:" + attrName);
			}
		}
		
		int event = input.next();
		boolean done = false;
		while (event != XmlPullParser.END_DOCUMENT && !done) {
			switch (event) {
			case XmlPullParser.START_TAG:
			{
				final String tagName = input.getName();
				if (DO_LOG) Log.d(TAG, "	parseCustomer start tag:" + tagName);
				
				if (Level.TAG_2_FOOD.containsKey(tagName)) {
					Food food = Level.TAG_2_FOOD.get(tagName);
					customer.prefferedFood.addFood(food);
				} else {
					Log.w(TAG, "parseCustomer unknown tag:" + tagName);
				}
				break;
			}
			case XmlPullParser.END_TAG:
			{
				final String tagName = input.getName();
				if (DO_LOG) Log.d(TAG, "	parseCustomer end tag:" + tagName);
				if (Level.TAG_NAME_CUSTOMER.equals(tagName)) {
					done = true;
				}
				break;
			}
			default:
				break;
			}
			
			if (!done) {
				event = input.next();
			}
		}
		
		return customer;
	}
	
	private static final int MILLI_PER_SECOND = 1000;
}
