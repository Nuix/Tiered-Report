package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based upon the localized name (user friendly name) of the type attributed to the item by Nuix. 
 * @author Jason Wells
 *
 */
public class ItemTypeAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Item Type";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		String typeName = item.getType().getLocalisedName();
		data.recordItemInfoValue(aspectBitmaps, typeName, itemInfo);
	}

}
