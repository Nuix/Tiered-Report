package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on the item type of their top level item. 
 * @author Jason Wells
 *
 */
public class TopLevelItemTypeAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Top Level Item Type";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		Item topLevelItem = item.getTopLevelItem();
		if(topLevelItem == null){
			data.recordItemInfoValue(aspectBitmaps, "No Top Level Item", itemInfo);
		} else {
			String typeName = topLevelItem.getType().getLocalisedName();
			data.recordItemInfoValue(aspectBitmaps, typeName, itemInfo);	
		}
	}
}
