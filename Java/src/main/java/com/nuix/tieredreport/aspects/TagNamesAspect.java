package com.nuix.tieredreport.aspects;

import java.util.Map;
import java.util.Set;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on the tags assigned to them.
 * @author Jason Wells
 *
 */
public class TagNamesAspect extends AbstractItemAspect{

	@Override
	public String getAspectName() {
		return "Tags";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		Set<String> tags = item.getTags();
		if(tags.size() > 0){
			for(String tag : tags){
				data.recordItemInfoValue(aspectBitmaps, tag, itemInfo);
			}
		} else {
			data.recordItemInfoValue(aspectBitmaps, "No Tags", itemInfo);
		}
	}
}
