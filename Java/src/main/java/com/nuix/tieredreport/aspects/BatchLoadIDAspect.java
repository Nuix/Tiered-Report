package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.BatchLoadDetails;
import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items by their batch load's ID.
 * @author Jason Wells
 *
 */
public class BatchLoadIDAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Batch Load ID";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		BatchLoadDetails details = item.getBatchLoadDetails();
		String batchLoadIdentifier = details.getBatchId();
		data.recordItemInfoValue(aspectBitmaps, batchLoadIdentifier, itemInfo);
	}
}
