package com.nuix.tieredreport.aspects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

public class ItemCategoryAspect extends AbstractItemAspect {
	
	private static Map<String,Integer> valueRanking = new HashMap<String,Integer>();
	
	{
		valueRanking.put("Electronic Directory",0);
		valueRanking.put("Electronic File",1);
		valueRanking.put("Email",2);
		valueRanking.put("Attachment",3);
	}

	@Override
	public String getAspectName() {
		return "Item Category";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		String category = item.getItemCategory();
		if(category == null){
			category = "No Category";
		}
		data.recordItemInfoValue(aspectBitmaps, category, itemInfo);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Object> customizeValueSort(Collection<Object> values) {
		List<String> sorted = new ArrayList<String>();
		for(Object value : values){
			sorted.add(value.toString());
		}
		sorted.sort((a,b) -> {
			int rankA = 0;
			int rankB = 0;
			// In case a category is ever encountered we don't know
			// assume it should be sorted towards bottom
			if(valueRanking.containsKey(a)){
				rankA = valueRanking.get(a);
			} else {
				rankA = 9999;
			}
			if(valueRanking.containsKey(b)){
				rankB = valueRanking.get(b);
			} else {
				rankB = 9999;
			}
			return Integer.compare(rankA,rankB);
		});
		return (List)sorted;
	}

}
