package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on the original extension as obtained by calling
 * <a href="https://download.nuix.com/releases/desktop/stable/docs/en/scripting/api/nuix/Item.html#getOriginalExtension--">Item.getOriginalExtension</a>
 * @author Jason Wells
 *
 */
public class OriginalExtensionAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Original Extension";
	}
	
	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		String originalExtension = item.getOriginalExtension();
		if(originalExtension == null || originalExtension.trim().length() < 1){
			originalExtension = "No Extension";
		}
		data.recordItemInfoValue(aspectBitmaps, originalExtension, itemInfo);
	}
}
