package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on their material status (audited / non-audited).
 * @author Jason Wells
 *
 */
public class MaterialStatusAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Material Status";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		String materialStatus = item.isAudited() ? "Material" : "Immaterial";
		data.recordItemInfoValue(aspectBitmaps, materialStatus, itemInfo);
	}

}
