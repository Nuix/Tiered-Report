package com.nuix.tieredreport.aspects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.ItemSet;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on the item set to which they belong and whether they are original or duplicate
 * within that item set.
 * @author Jason Wells
 *
 */
public class ItemSetNameAndDuplicatStatus extends AbstractItemAspect {

	private List<String> nameOrder = null;
	
	@Override
	public String getAspectName() {
		return "Item Set Name and Duplicate Status";
	}

	@Override
	public void setup(Case nuixCase, Utilities utilities) {
		nameOrder = new ArrayList<String>();
	}

	@Override
	public void recordValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps,
			TieredReportData data, Collection<Item> inputItems) {
		try {
			for(ItemSet itemSet : nuixCase.getAllItemSets()){
				String origName = itemSet.getName() + " - Originals";
				String dupeName = itemSet.getName() + " - Duplicates";
				data.recordItemsValue(aspectBitmaps, origName, utilities.getItemUtility().intersection(itemSet.getOriginals(),inputItems));
				data.recordItemsValue(aspectBitmaps, dupeName, utilities.getItemUtility().intersection(itemSet.getDuplicates(),inputItems));
				Set<Item> items = nuixCase.searchUnsorted("has-item-set:0");
				data.recordItemsValue(aspectBitmaps, "No Item Set", utilities.getItemUtility().intersection(items,inputItems));
				nameOrder.add(origName);
				nameOrder.add(dupeName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public List<Object> customizeValueSort(Collection<Object> values) {
		// Customize order that categories are reported
		List<Object> result = new ArrayList<Object>();
		
		for(String name : nameOrder){
			if(values.stream().anyMatch(v -> ((String)v).equals(name)) && !result.stream().anyMatch(v -> ((String)v).equals(name))){
				result.add(name);	
			}
		}
		result.add("No Item Set");
		return result;
	}
	
	@Override
	public boolean isPerItem() {
		return false;
	}
}
