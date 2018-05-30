package com.nuix.tieredreport.aspects;

import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.ItemInfo;
import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on their full item date.
 * @author Jason Wells
 *
 */
public class ItemDateFullAspect extends AbstractItemAspect {

	private static String formatString = "yyyy/MM/dd";
	public static String getFormatString(){ return formatString; }
	public static void setFormatString(String dateFormat){
		formatString = dateFormat;
	}
	
	private DateTimeZone investigationZone;
	
	@Override
	public String getAspectName() {
		return "Item Date";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		DateTime itemDate = item.getDate();
		if(itemDate == null){
			data.recordItemInfoValue(aspectBitmaps, "No Date", itemInfo);
		} else {
			DateTime zonedDateTime = itemDate.withZone(investigationZone);
			data.recordItemInfoValue(aspectBitmaps, zonedDateTime.toString(formatString), itemInfo);
		}
	}
	
	@Override
	public void setup(Case nuixCase, Utilities utilities) {
		investigationZone = DateTimeZone.forID(nuixCase.getInvestigationTimeZone());
		super.setup(nuixCase, utilities);
	}

}
