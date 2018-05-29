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

public class RecipientAddressesAspect extends AbstractItemAspect {

	@Override
	public String getAspectName() {
		return "Recipient Addresses";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		Communication comm = item.getCommunication();
		if(comm != null){
			Set<Address> recipientAddresses = new HashSet<Address>();
			recipientAddresses.addAll(comm.getTo());
			recipientAddresses.addAll(comm.getCc());
			recipientAddresses.addAll(comm.getBcc());
			
			for(Address address : recipientAddresses){
				data.recordItemInfoValue(aspectBitmaps, address.getAddress(), itemInfo);
			}
		} else {
			data.recordItemInfoValue(aspectBitmaps, "Non Communication", itemInfo);	
		}
	}
}
