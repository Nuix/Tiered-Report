package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on the languages detected by Nuix.
 * @author Jason Wells
 *
 */
public class LanguageAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Detected Language";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		String lang = item.getLanguage();
		if(lang == null){
			data.recordItemInfoValue(aspectBitmaps, "No Language Detected", itemInfo);
		} else {
			data.recordItemInfoValue(aspectBitmaps, lang, itemInfo);
		}
	}
}
