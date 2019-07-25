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

/***
 * Item aspect which categorizes items based on the recipient address domains associated with those items.
 * @author Jason Wells
 *
 */
public class CcDomainsAspect extends AbstractItemAspect {

	private static Pattern splitRegex = Pattern.compile("@",Pattern.CASE_INSENSITIVE);
	
	@Override
	public String getAspectName() {
		return "CC Domains";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		Communication comm = item.getCommunication();
		if(comm != null){
			Set<Address> recipientAddresses = new HashSet<Address>();
			recipientAddresses.addAll(comm.getCc());
			
			Set<String> recipientDomains = new HashSet<String>();
			for(Address address : recipientAddresses){
				String[] segments = splitRegex.split(address.getAddress());
				String domain = "No Domain";
				if(segments.length > 1)
					domain = segments[segments.length - 1];
				recipientDomains.add(domain);
			}
			
			for(String domain : recipientDomains){
				data.recordItemInfoValue(aspectBitmaps, domain, itemInfo);	
			}
		} else {
			data.recordItemInfoValue(aspectBitmaps, "No Communication", itemInfo);	
		}
	}
}