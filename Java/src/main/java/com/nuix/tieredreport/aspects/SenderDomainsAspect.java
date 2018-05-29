package com.nuix.tieredreport.aspects;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Address;
import nuix.Case;
import nuix.Communication;
import nuix.Item;
import nuix.Utilities;

public class SenderDomainsAspect extends AbstractItemAspect {

	private static Pattern splitRegex = Pattern.compile("@",Pattern.CASE_INSENSITIVE);
	
	@Override
	public String getAspectName() {
		return "Sender Domains";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		Communication comm = item.getCommunication();
		if(comm != null){
			Set<Address> senderAddresses = new HashSet<Address>();
			senderAddresses.addAll(comm.getFrom());
			
			Set<String> senderDomains = new HashSet<String>();
			for(Address address : senderAddresses){
				String[] segments = splitRegex.split(address.getAddress());
				String domain = "No Domain";
				if(segments.length > 1)
					domain = segments[segments.length - 1];
				senderDomains.add(domain);
			}
			
			for(String domain : senderDomains){
				data.recordItemInfoValue(aspectBitmaps, domain, itemInfo);	
			}
		} else {
			data.recordItemInfoValue(aspectBitmaps, "Non Communication", itemInfo);	
		}
	}
}
