package com.nuix.tieredreport.aspects;

import java.util.Map;
import java.util.function.BiFunction;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

public class BasicScriptableAspect extends AbstractItemAspect {

	private String aspectName = null;
	private String aspectReportLabel = null;
	private BiFunction<Case,Item,Object> valueFunction = null;
	
	@Override
	public String getAspectName() {
		return aspectName;
	}

	@Override
	public String getAspectReportLabel() {
		return aspectReportLabel;
	}

	public BasicScriptableAspect(String aspectName, String aspectReportLabel, BiFunction<Case,Item,Object> valueFunction) {
		this.aspectName = aspectName;
		this.aspectReportLabel = aspectReportLabel;
		this.valueFunction = valueFunction;
	}
	
	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		if(item != null) {
			Object value = valueFunction.apply(nuixCase, item);
			if(value != null) {
				data.recordItemInfoValue(aspectBitmaps, value, itemInfo);
			}	
		}
	}
}
