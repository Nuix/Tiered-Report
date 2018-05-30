package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items by the location (file system path) of the case to which they belong.
 * @author Jason Wells
 *
 */
public class CaseLocationAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Case Location";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		String caseLocation = nuixCase.getLocation().getPath();
		data.recordItemValue(aspectBitmaps, caseLocation, item);
	}
}
