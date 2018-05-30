package com.nuix.tieredreport.aspects;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.ProductionSet;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on the name of the production sets the items belong to.
 * @author Jason Wells
 *
 */
public class ProductionSetAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Production Set";
	}

	@Override
	public void recordValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Collection<Item> inputItems) {
		try {
			for(ProductionSet prodSet : nuixCase.getProductionSets()){
				data.recordItemsValue(aspectBitmaps, prodSet.getName(), utilities.getItemUtility().intersection(prodSet.getItems(), inputItems));
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
