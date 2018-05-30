package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on the mime type detected by Nuix.
 * @author Jason Wells
 *
 */
public class MimeTypeAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Mime Type";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		String mimeType = item.getType().getName();
		data.recordItemInfoValue(aspectBitmaps, mimeType, itemInfo);
	}

}
