package com.nuix.tieredreport.aspects;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Address;
import nuix.Case;
import nuix.Communication;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on the To addresses associated with those items.
 * @author Jason Wells
 *
 */
public class ToAddressesAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "To Addresses";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		Communication comm = item.getCommunication();
		if(comm != null){
			Set<Address> distinctAddresses = new HashSet<Address>();
			distinctAddresses.addAll(comm.getTo());
			
			for(Address address : distinctAddresses){
				data.recordItemInfoValue(aspectBitmaps, address.getAddress(), itemInfo);
			}
		} else {
			data.recordItemInfoValue(aspectBitmaps, "Non Communication", itemInfo);	
		}
	}
}
