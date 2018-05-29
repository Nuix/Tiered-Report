package com.nuix.tieredreport.aspects;

import java.util.Map;
import java.util.Set;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

public class PropertyNamesAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Property Names";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		Set<String> propertyNames = item.getProperties().keySet();
		for(String name : propertyNames){
			data.recordItemInfoValue(aspectBitmaps, name, itemInfo);
		}
	}
}
