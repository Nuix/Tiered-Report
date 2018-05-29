package com.nuix.tieredreport.aspects;

import java.util.List;
import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

public class PhysicalFileNameAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Physical File Name";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		Item physicalFileAncestor = findPhysicalFileAncestor(item);
		if(physicalFileAncestor == null){
			data.recordItemInfoValue(aspectBitmaps, "No Physical File Ancestor", itemInfo);
		} else {
			data.recordItemInfoValue(aspectBitmaps, physicalFileAncestor.getLocalisedName(), itemInfo);
		}
	}

	/***
	 * Resolves an item to its physical file ancestor if it has one or null if one could not be found.
	 * @param item The item you wish to resolve the physical file ancestor for
	 * @return The physical file ancestor item if there is one, or null if one could not be found
	 */
	public static Item findPhysicalFileAncestor(Item item){
		// Search path in reverse for first physical file ancestor
		List<Item> pathItems = item.getPath();
		Item ancestor = null;
		for (int i = pathItems.size() - 1; i >= 0; i--) {
		    Item currentPathItem = pathItems.get(i);
		    // If this is it, record and break from loop
		    if(currentPathItem.isPhysicalFile()){
		    	ancestor = currentPathItem;
		    	break;
		    }
		}
		return ancestor;
	}
}
