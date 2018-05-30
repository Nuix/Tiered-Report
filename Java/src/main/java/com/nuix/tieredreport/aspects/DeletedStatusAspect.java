package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on their deleted status, as obtained by calling
 * <a href="https://download.nuix.com/releases/desktop/stable/docs/en/scripting/api/nuix/ItemMetadata.html#isDeleted--">Item.isDeleted</a>.
 * @author Jason Wells
 *
 */
public class DeletedStatusAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Is Deleted";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		data.recordItemInfoValue(aspectBitmaps, item.isDeleted(), itemInfo);		
	}

}
