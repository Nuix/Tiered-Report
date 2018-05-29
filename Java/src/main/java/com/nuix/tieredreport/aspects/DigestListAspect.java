package com.nuix.tieredreport.aspects;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.DigestListStore;
import nuix.Item;
import nuix.Utilities;

public class DigestListAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Digest Lists";
	}

	@Override
	public void recordValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Collection<Item> inputItems) {
		try {
			DigestListStore store = utilities.getDigestListStore();
			Collection<String> digestListNames = store.getDigestListNames();
			for(String digestListName : digestListNames){
				String query = "digest-list:\""+digestListName+"\"";
				Set<Item> items = nuixCase.searchUnsorted(query);
				if(items.size() > 0){
					data.recordItemsValue(aspectBitmaps, digestListName, utilities.getItemUtility().intersection(items,inputItems));
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
