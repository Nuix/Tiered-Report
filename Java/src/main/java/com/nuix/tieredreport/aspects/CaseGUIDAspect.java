package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items by the GUID of the case to which they belong.
 * @author Jason Wells
 *
 */
public class CaseGUIDAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Case GUID";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		String caseName = nuixCase.getGuid();
		data.recordItemInfoValue(aspectBitmaps, caseName, itemInfo);
	}

}
