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
 * Item aspect which categorizes items based on the full item date of their top level item.
 * @author Jason Wells
 *
 */
public class TopLevelItemDateFullAspect extends AbstractItemAspect {

	private static String formatString = "yyyy/MM/dd";
	public static String getFormatString(){ return formatString; }
	public static void setFormatString(String dateFormat){
		formatString = dateFormat;
	}
	
	private DateTimeZone investigationZone;
	
	@Override
	public String getAspectName() {
		return "Top Level Item Date";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		DateTime topLevelItemDate = item.getTopLevelItemDate();
		if(topLevelItemDate == null){
			data.recordItemInfoValue(aspectBitmaps, "No Top Level Item Date", itemInfo);
		} else {
			DateTime zonedDateTime = topLevelItemDate.withZone(investigationZone);
			data.recordItemInfoValue(aspectBitmaps, zonedDateTime.toString(formatString), itemInfo);
		}
	}
	
	@Override
	public void setup(Case nuixCase, Utilities utilities) {
		investigationZone = DateTimeZone.forID(nuixCase.getInvestigationTimeZone());
		super.setup(nuixCase, utilities);
	}

}
