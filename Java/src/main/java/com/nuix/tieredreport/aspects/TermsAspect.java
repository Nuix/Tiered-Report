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
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on whether they are matches to a list of terms.
 * @author Jason Wells
 *
 */
public class TermsAspect extends AbstractItemAspect {

	private static List<String> terms = new ArrayList<String>();
	public static List<String> getTerms(){
		return terms;
	}
	public static void setTerms(List<String> termList){
		terms = termList;
	}
	
	@Override
	public String getAspectName() {
		return "Terms";
	}

	@Override
	public void recordValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Collection<Item> inputItems) {
		try {
			for(String term : terms){
				Set<Item> items = nuixCase.searchUnsorted(term);
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
