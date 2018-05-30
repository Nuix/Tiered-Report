package com.nuix.tieredreport.aspects;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on the named entities found by Nuix during processing.
 * @author Jason Wells
 *
 */
public class NamedEntityAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Named Entity";
	}

	@Override
	public void recordValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Collection<Item> inputItems) {
		try {
			Set<String> entityTypes = nuixCase.getAllEntityTypes();
			for(String entityType : entityTypes){
				String query = "named-entities:"+entityType+";*";
				Set<Item> items = nuixCase.searchUnsorted(query);
				if(items.size() > 0){
					data.recordItemsValue(aspectBitmaps, entityType, utilities.getItemUtility().intersection(items,inputItems));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isPerItem() {
		return false;
	}

	
}
