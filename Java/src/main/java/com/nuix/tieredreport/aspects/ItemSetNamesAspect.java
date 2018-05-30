package com.nuix.tieredreport.aspects;

import java.util.Map;
import java.util.Set;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.ItemSet;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on the name of the item sets to which they are a member of.
 * @author Jason Wells
 *
 */
public class ItemSetNamesAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Item Set";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		Set<ItemSet> itemSets = item.getItemSets();
		if(itemSets.size() > 0){
			for(ItemSet itemSet : itemSets){
				String name = itemSet.getName();
				data.recordItemInfoValue(aspectBitmaps, name, itemInfo);
			}
		}
		else {
			data.recordItemInfoValue(aspectBitmaps, "Not in Item Sets", itemInfo);
		}
	}
}
