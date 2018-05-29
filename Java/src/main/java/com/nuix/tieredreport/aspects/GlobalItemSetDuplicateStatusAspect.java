package com.nuix.tieredreport.aspects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

public class GlobalItemSetDuplicateStatusAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Global Item Set Duplicate Status";
	}

	@Override
	public void recordValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps,
			TieredReportData data, Collection<Item> inputItems) {
		try {
			Set<Item> items = nuixCase.searchUnsorted("has-item-set:1 AND is-item-original:1");
			data.recordItemsValue(aspectBitmaps, "Original", utilities.getItemUtility().intersection(items,inputItems));
			
			items = nuixCase.searchUnsorted("has-item-set:1 AND is-item-original:0");
			data.recordItemsValue(aspectBitmaps, "Duplicate", utilities.getItemUtility().intersection(items,inputItems));
			
			items = nuixCase.searchUnsorted("has-item-set:0");
			data.recordItemsValue(aspectBitmaps, "No Item Set", utilities.getItemUtility().intersection(items,inputItems));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public List<Object> customizeValueSort(Collection<Object> values) {
		// Customize order that categories are reported
		List<Object> result = new ArrayList<Object>();
		result.add("Original");
		if(values.stream().anyMatch(v -> ((String)v).equals("Duplicate"))){
			result.add("Duplicate");	
		}
		if(values.stream().anyMatch(v -> ((String)v).equals("No Item Set"))){
			result.add("No Item Set");	
		}
		return result;
	}
	
	@Override
	public boolean isPerItem() {
		return false;
	}
}
