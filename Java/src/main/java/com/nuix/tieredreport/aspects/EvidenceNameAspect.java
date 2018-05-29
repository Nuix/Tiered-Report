package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

public class EvidenceNameAspect extends AbstractItemAspect {
	@Override
	public String getAspectName() {
		return "Evidence Name";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		String evidenceName = item.getRoot().getName();
		data.recordItemInfoValue(aspectBitmaps, evidenceName, itemInfo);
	}
}
