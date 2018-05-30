package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on the value found in a particular metadata property.
 * @author Jason Wells
 *
 */
public class PropertyMetadataAspect extends AbstractItemAspect {

	private String propertyName = null;
	private String reportLabel = null;
	private String valueIfMissing = null;
	
	public PropertyMetadataAspect(String propertyName, String reportLabel, String valueIfMissing){
		if(propertyName == null || propertyName.trim().length() < 1){
			throw new IllegalArgumentException("propertyName cannot be null or only whitespace");
		}
		
		if (reportLabel == null || reportLabel.trim().length() < 1){
			reportLabel = propertyName;
		}
		
		this.propertyName = propertyName;
		this.reportLabel = reportLabel;
		this.valueIfMissing = valueIfMissing;
	}
	
	public PropertyMetadataAspect(String propertyName, String reportLabel){
		this(propertyName,reportLabel,"No Value");
	}
	
	@Override
	public String getAspectName() {
		return "Metadata Property: "+propertyName;
	}

	@Override
	public String getAspectReportLabel() {
		return reportLabel;
	}
	
	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		Object oValue = item.getProperties().get(propertyName);
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
