package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based upon the exclusion they belong to.
 * @author Jason Wells
 *
 */
public class ExclusionNameAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Exclusion Name";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		String exclusionName = item.getExclusion();
		if(exclusionName == null)
			exclusionName = "Not Excluded";
		data.recordItemInfoValue(aspectBitmaps, exclusionName, itemInfo);
	}

	
}
