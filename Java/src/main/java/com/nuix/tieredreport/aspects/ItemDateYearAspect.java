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
 * Item aspect which categorizes items based upon their item date's year.
 * @author Jason Wells
 *
 */
public class ItemDateYearAspect extends AbstractItemAspect {

	private DateTimeZone investigationZone;
	
	@Override
	public String getAspectName() {
		return "Item Year";
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		DateTime itemDate = item.getDate();
		if(itemDate == null){
			data.recordItemInfoValue(aspectBitmaps, "No Date", itemInfo);
		} else {
			DateTime zonedDateTime = itemDate.withZone(investigationZone);
			data.recordItemInfoValue(aspectBitmaps, zonedDateTime.getYear(), itemInfo);
		}
	}
	
	@Override
	public void setup(Case nuixCase, Utilities utilities) {
		investigationZone = DateTimeZone.forID(nuixCase.getInvestigationTimeZone());
		super.setup(nuixCase, utilities);
	}
}
