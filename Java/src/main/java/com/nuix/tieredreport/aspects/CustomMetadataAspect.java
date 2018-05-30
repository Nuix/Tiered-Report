package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on the value present in a particular custom metadata field on the item.
 * @author Jason Wells
 *
 */
public class CustomMetadataAspect extends AbstractItemAspect {
	
	private String customMetadataFieldName = null;
	private String reportLabel = null;
	private String valueIfMissing = null;
	
	public CustomMetadataAspect(String customFieldName, String reportLabel, String valueIfMissing){
		if(customFieldName == null || customFieldName.trim().length() < 1){
			throw new IllegalArgumentException("customFieldName cannot be null or only whitespace");
		}
		
		if (reportLabel == null || reportLabel.trim().length() < 1){
			reportLabel = customFieldName;
		}
		
		this.customMetadataFieldName = customFieldName;
		this.reportLabel = reportLabel;
		this.valueIfMissing = valueIfMissing;
	}
	
	public CustomMetadataAspect(String customFieldName, String reportLabel){
		this(customFieldName,reportLabel,"No Value");
	}
	
	public CustomMetadataAspect(String customFieldName){
		this(customFieldName,customFieldName);
	}
	
	@Override
	public String getAspectName() {
		return "Custom Metadata: "+customMetadataFieldName;
	}

	@Override
	public String getAspectReportLabel() {
		return reportLabel;
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		Object oValue = item.getCustomMetadata().get(customMetadataFieldName);
		if(oValue == null){
			if(valueIfMissing != null){
				data.recordItemInfoValue(aspectBitmaps, valueIfMissing, itemInfo);
			} else {
				data.recordItemInfoValue(aspectBitmaps, "", itemInfo);
			}
		} else if(oValue instanceof Boolean) {
			data.recordItemInfoValue(aspectBitmaps, Boolean.toString(((Boolean)oValue)), itemInfo);
		} else if(oValue instanceof Integer || oValue instanceof Long || oValue instanceof Float ||
				oValue instanceof Double) {
			data.recordItemInfoValue(aspectBitmaps, oValue, itemInfo);
		} else {
			data.recordItemInfoValue(aspectBitmaps, oValue.toString(), itemInfo);
		}
	}
}
