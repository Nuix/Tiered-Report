package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

public class ItemKindAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Item Kind";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		String kind = item.getKind().getName();
		data.recordItemInfoValue(aspectBitmaps, kind, itemInfo);
	}
}
