package com.nuix.tieredreport.aspects;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

public interface ItemAspect {
	public String getAspectName();
	public String getAspectReportLabel();
	public boolean isPerItem();
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object,RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo);
	public void setup(Case nuixCase, Utilities utilities);
	public void cleanup();
	public void recordValues(Case nuixCase, Utilities utilities, Map<Object,RoaringBitmap> aspectBitmaps, TieredReportData data, Collection<Item> inputItems);
	public List<Object> customizeValueSort(Collection<Object> values);
}
