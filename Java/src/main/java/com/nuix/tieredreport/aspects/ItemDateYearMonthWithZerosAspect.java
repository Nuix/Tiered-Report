package com.nuix.tieredreport.aspects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
 * Item aspect which categorizes items based upon their item date's year and month.
 * This version reports year/month combinations which have no items as well as those
 * with items.
 * @author Jason Wells
 *
 */
public class ItemDateYearMonthWithZerosAspect extends AbstractItemAspect {

	private DateTimeZone investigationZone;
	
	@Override
	public String getAspectName() {
		return "Item Date Year/Month with Zeros";
	}

	@Override
	public boolean reportsZeroItems() {
		return true;
	}

	@Override
	public void recordItemValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Item item, ItemInfo itemInfo) {
		DateTime itemDate = item.getDate();
		if(itemDate == null){
			data.recordItemInfoValue(aspectBitmaps, "No Date", itemInfo);
		} else {
			DateTime zonedDateTime = itemDate.withZone(investigationZone);
			data.recordItemInfoValue(aspectBitmaps, zonedDateTime.toString("yyyy/MM"), itemInfo);
		}
	}
	
	@Override
	public void setup(Case nuixCase, Utilities utilities) {
		investigationZone = DateTimeZone.forID(nuixCase.getInvestigationTimeZone());
		super.setup(nuixCase, utilities);
	}

	@Override
	public void postProcess(Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data) {
		List<String> keys = new ArrayList<String>();
		for(Object key : aspectBitmaps.keySet()){
			String keyString = (String)key;
			if(!keyString.equalsIgnoreCase("No Date")){
				keys.add(keyString);	
			}
		}
		Collections.sort(keys);
		
		int previousYear = -1;
		int previousMonth = -1;
		List<String> noItemKeys = new ArrayList<String>();
		
		for(String key : keys){
			String[] pieces = key.split("/");
			int year = Integer.parseInt(pieces[0]);
			int month = Integer.parseInt(pieces[1]);
			
			if(previousYear != -1){
				int py = previousYear;
				int pm = previousMonth;
				int y = year;
				int m = month;
				// The math here probably looks like "what the heck?"
				// I want to compare a date like 2018/06 as an integer, like 201806, but
				// I can't just add them together, so I multiple 2018 * 100 so it become 201800, to which
				// I can then add the month 6 and get 201806
				while((y*100+m) - (py*100+pm) > 1){
					pm++;
					if(pm == 13){
						py++;
						pm = 1;
					}
					noItemKeys.add(String.format("%04d/%02d", py,pm));
				}
			}
			
			previousYear = year;
			previousMonth = month;
		}
		
		for(String key : noItemKeys){
			data.recordValueWithoutItems(aspectBitmaps, key);	
		}
	}
}
