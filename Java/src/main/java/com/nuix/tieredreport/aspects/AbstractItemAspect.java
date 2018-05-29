package com.nuix.tieredreport.aspects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

public abstract class AbstractItemAspect implements ItemAspect {

	public abstract String getAspectName();
	
	public String getAspectReportLabel(){
		return getAspectName();
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
	}
	
	@Override
	public void recordValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Collection<Item> inputItems) {
	}
	
	@Override
	public boolean isPerItem(){
		return true;
	}

	@Override
	public void setup(Case nuixCase, Utilities utilities) {
	}

	@Override
	public void cleanup() {
	}

	@Override
	public List<Object> customizeValueSort(Collection<Object> values) {
		List<Integer> intValues = new ArrayList<Integer>();
		List<Long> longValues = new ArrayList<Long>();
		List<Float> floatValues = new ArrayList<Float>();
		List<Double> doubleValues = new ArrayList<Double>();
		List<String> stringValues = new ArrayList<String>();
		
		for (Object value : values) {
			if(value instanceof Integer){ intValues.add((Integer)value); }
			else if(value instanceof Long){ longValues.add((Long)value); }
			else if(value instanceof Float){ floatValues.add((Float)value); }
			else if(value instanceof Double){ doubleValues.add((Double)value); }
			else { stringValues.add(value.toString()); }
		}
		
		Collections.sort(intValues);
		Collections.sort(longValues);
		Collections.sort(floatValues);
		Collections.sort(doubleValues);
		Collections.sort(stringValues);
		
		List<Object> result = new ArrayList<Object>();
		
		result.addAll(intValues);
		result.addAll(longValues);
		result.addAll(floatValues);
		result.addAll(doubleValues);
		result.addAll(stringValues);
		
		return result;
	}
}
