package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

public class TopLevelItemKindAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Top Level Item Kind";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		Item topLevelItem = item.getTopLevelItem();
		if(topLevelItem == null){
			data.recordItemInfoValue(aspectBitmaps, "No Top Level Item", itemInfo);
		} else {
			String kind = topLevelItem.getKind().getName();
			data.recordItemInfoValue(aspectBitmaps, kind, itemInfo);	
		}
	}
}
