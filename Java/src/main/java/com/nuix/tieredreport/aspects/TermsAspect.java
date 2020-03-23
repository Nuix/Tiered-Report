package com.nuix.tieredreport.aspects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on whether they are matches to a list of terms.
 * @author Jason Wells
 *
 */
public class TermsAspect extends AbstractItemAspect {

	// == List of Terms ==
	private static List<String> terms = new ArrayList<String>();
	public static List<String> getTerms(){
		return terms;
	}
	public static void setTerms(List<String> termList){
		terms = termList;
	}
	
	// == Default search fields for terms ==
	private static List<String> defaultFields = new ArrayList<String>();
	private static Map<String,Object> searchSettings = new HashMap<String,Object>();
	
	public static List<String> getDefaultFields(){
		return defaultFields;
	}
	
	public static void setDefaultFields(List<String> fields) {
		defaultFields = fields;
		searchSettings.clear();
		searchSettings.put("defaultFields", fields);
	}
	
	@Override
	public String getAspectName() {
		return "Terms";
	}

	@Override
	public void recordValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Collection<Item> inputItems) {
		try {
			for(String term : terms){
				Set<Item> items = nuixCase.searchUnsorted(term,searchSettings);
				if(items.size() > 0){
					data.recordItemsValue(aspectBitmaps, term, utilities.getItemUtility().intersection(items,inputItems));
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
