package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.joda.time.DateTime;
import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on their item date's month.
 * @author Jason Wells
 *
 */
public class ItemDateMonthAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Item Date Month";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		DateTime itemDate = item.getDate();
		if(itemDate == null){
			data.recordItemInfoValue(aspectBitmaps, "No Date", itemInfo);
		} else {
			data.recordItemInfoValue(aspectBitmaps, itemDate.toString("MM"), itemInfo);
		}
	}
}
