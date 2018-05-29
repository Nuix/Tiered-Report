package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.joda.time.DateTime;
import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

public class TopLevelItemDateMonthAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Top Level Item Date Month";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		DateTime topLevelItemDate = item.getTopLevelItemDate();
		if(topLevelItemDate == null){
			data.recordItemInfoValue(aspectBitmaps, "No Top Level Item Date", itemInfo);
		} else {
			data.recordItemInfoValue(aspectBitmaps, topLevelItemDate.toString("MM"), itemInfo);
		}
	}
}
