package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on the custodian name assigned to them.
 * @author Jason Wells
 *
 */
public class CustodianNameAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Custodian";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		String custodian = item.getCustodian();
		if(custodian == null || custodian.trim().isEmpty()){
			data.recordItemInfoValue(aspectBitmaps, "No Custodian", itemInfo);
		} else {
			data.recordItemInfoValue(aspectBitmaps, custodian, itemInfo);	
		}
	}

}
