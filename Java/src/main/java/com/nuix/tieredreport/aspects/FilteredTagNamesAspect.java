package com.nuix.tieredreport.aspects;

import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

public class FilteredTagNamesAspect extends AbstractItemAspect {

	private String name = "FilteredTagNamesAspect";
	private BiFunction<Item,List<String>,List<String>> tagModifierFunction = null;
	
	public FilteredTagNamesAspect(String name, BiFunction<Item,List<String>,List<String>> tagModifierFunction) {
		this.name = name;
		this.tagModifierFunction = tagModifierFunction;
	}
	
	@Override
	public String getAspectName() {
		return name;
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		Set<String> tags = item.getTags();
		
		List<String> inputTags = new ArrayList<String>();
		inputTags.addAll(tags);
		List<String> outputTags = tagModifierFunction.apply(item,inputTags);
		Set<String> outputTagsSet = new HashSet<String>();
		outputTagsSet.addAll(outputTags);
		
		if(outputTagsSet.size() > 0){
			for(String tag : outputTagsSet){
				data.recordItemInfoValue(aspectBitmaps, tag, itemInfo);
			}
		} else {
			data.recordItemInfoValue(aspectBitmaps, "No Tags", itemInfo);
		}
	}
}
