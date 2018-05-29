package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

public class CorrectedExtensionAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Corrected Extension";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		String correctedExtension = item.getCorrectedExtension();
		if(correctedExtension == null || correctedExtension.trim().length() < 1){
			correctedExtension = "No Extension";
		}
		data.recordItemInfoValue(aspectBitmaps, correctedExtension, itemInfo);
	}
}
