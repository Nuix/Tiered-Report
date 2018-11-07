package com.nuix.tieredreport;

import java.util.HashMap;
import java.util.Map;

/***
 * Represents association between an item info and its offset within Roaring Bitmaps it
 * may belong to.
 * @author Jason Wells
 *
 */
public class OffsetStore {
	private Map<ItemInfo, Integer> offsetLookup = new HashMap<ItemInfo, Integer>();
	private Map<Integer, ItemInfo> itemInfoLookup = new HashMap<Integer, ItemInfo>();
	
	public int size(){
		return offsetLookup.size();
	}
	
	public void clear(){
		offsetLookup.clear();
		itemInfoLookup.clear();
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public int getOffset(ItemInfo itemInfo){
		if(!itemInfoLookup.containsKey(itemInfo)){
			int preAddSize = itemInfoLookup.size();
			offsetLookup.put(itemInfo,preAddSize);
			itemInfoLookup.put(preAddSize,itemInfo);
			return preAddSize;
		}
		else {
			return offsetLookup.get(itemInfo);
		}
	}
	
	public ItemInfo getItemInfo(int offset){
		return itemInfoLookup.get(offset);
	}
}
