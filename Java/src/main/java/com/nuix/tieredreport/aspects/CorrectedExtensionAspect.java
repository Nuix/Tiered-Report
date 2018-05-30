package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on the corrected extension associated to them, obtained by
 * calling <a href="https://download.nuix.com/releases/desktop/stable/docs/en/scripting/api/nuix/Item.html#getCorrectedExtension--">Item.getCorrectedExtension</a>.
 * @author Jason Wells
 *
 */
public class CorrectedExtensionAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Corrected Extension";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		String correctedExtension = item.getCorrectedExtension();
		if(correctedExtension == null || correctedExtension.trim().length() < 1){
			correctedExtension = "No Extension";
		}
		data.recordItemInfoValue(aspectBitmaps, correctedExtension, itemInfo);
	}
}
