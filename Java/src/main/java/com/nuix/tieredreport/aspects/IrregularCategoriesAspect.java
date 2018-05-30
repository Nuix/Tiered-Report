package com.nuix.tieredreport.aspects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.TieredReportData;

import nuix.Case;
import nuix.Item;
import nuix.Utilities;

/***
 * Item aspect which categorizes items based on irregular items categories they belong to.  
 * @author Jason Wells
 *
 */
public class IrregularCategoriesAspect extends AbstractItemAspect {
	private static Map<String,String> irregularCategories = new HashMap<String,String>();
	
	{
		irregularCategories.put("Corrupted Container","properties:FailureDetail AND encrypted:0 AND has-text:0 AND ( has-embedded-data:1 OR kind:container OR kind:database )");
		irregularCategories.put("Unsupported Container","kind:( container OR database ) AND encrypted:0 AND has-embedded-data:0 AND NOT flag:partially_processed AND NOT flag:not_processed AND NOT properties:FailureDetail");
		irregularCategories.put("Non-searchable PDFs","mime-type:application/pdf AND contains-text:0");
		irregularCategories.put("Text Updated","previous-version-docid:*");
		irregularCategories.put("Bad Extension","flag:irregular_file_extension");
		irregularCategories.put("Unrecognised","kind:unrecognised");
		irregularCategories.put("Unsupported Items","encrypted:0 AND has-embedded-data:0 AND ( ( has-text:0 AND has-image:0 AND NOT flag:not_processed AND NOT kind:multimedia AND NOT mime-type:application/vnd.ms-shortcut AND NOT mime-type:application/x-contact AND NOT kind:system AND NOT mime-type:( application/vnd.logstash-log-entry OR application/vnd.ms-iis-log-entry OR application/vnd.ms-windows-event-log-record OR application/vnd.ms-windows-event-logx-record OR application/vnd.tcpdump.record OR filesystem/x-ntfs-logfile-record OR server/dropbox-log-event OR text/x-common-log-entry OR text/x-log-entry ) AND NOT mime-type:( application/vnd.logstash-log OR application/vnd.logstash-log-entry OR application/vnd.ms-iis-log OR application/vnd.ms-iis-log-entry OR application/vnd.ms-windows-event-log OR application/vnd.ms-windows-event-log-record OR application/vnd.ms-windows-event-logx OR application/vnd.ms-windows-event-logx-chunk OR application/vnd.ms-windows-event-logx-record OR application/vnd.tcpdump.pcap OR application/vnd.tcpdump.record OR application/x-pcapng OR server/dropbox-log OR server/dropbox-log-event OR text/x-common-log OR text/x-common-log-entry OR text/x-log-entry OR text/x-nuix-log ) AND NOT mime-type:application/vnd.ms-exchange-stm ) OR mime-type:application/vnd.lotus-notes )");
		irregularCategories.put("Empty","mime-type:application/x-empty");
		irregularCategories.put("Encrypted","encrypted:1");
		irregularCategories.put("Decrypted","flag:decrypted");
		irregularCategories.put("Deleted","deleted:1");
		irregularCategories.put("Corrupted","properties:FailureDetail AND NOT encrypted:1");
		irregularCategories.put("Text Stripped","flag:text_stripped");
		irregularCategories.put("Licence Restricted","flag:licence_restricted");
		irregularCategories.put("Not Processed","flag:not_processed");
		irregularCategories.put("Partially Processed","flag:partially_processed");
		irregularCategories.put("Text Not Processed","flag:text_not_processed");
		irregularCategories.put("Images Not Processed","flag:images_not_processed");
		irregularCategories.put("Reloaded","flag:reloaded");
		irregularCategories.put("Poisoned","flag:poison");
		irregularCategories.put("Slack Space","flag:slack_space");
		irregularCategories.put("Unallocated Space","flag:unallocated_space");
		irregularCategories.put("Carved","flag:carved");
		irregularCategories.put("Deleted File - All Blocks Available","flag:fully_recovered");
		irregularCategories.put("Deleted File - Some Blocks Available","flag:partially_recovered");
		irregularCategories.put("Deleted File - Metadata Recovered","flag:metadata_recovered");
		irregularCategories.put("Hidden Stream","flag:hidden_stream");
		irregularCategories.put("Text Not Indexed","flag:text_not_indexed");
	}
	
	@Override
	public String getAspectName() {
		return "Irregular Categories";
	}

	@Override
	public void recordValues(Case nuixCase, Utilities utilities, Map<Object, RoaringBitmap> aspectBitmaps, TieredReportData data, Collection<Item> inputItems) {
		Map<String,List<Item>> categorizedItems = new HashMap<String,List<Item>>();
		for(Map.Entry<String,String> entry : irregularCategories.entrySet()){
			categorizedItems.computeIfAbsent(entry.getKey(), new Function<String, List<Item>>(){

				@Override
				public List<Item> apply(String t) {
					return new ArrayList<Item>();
				}
				
			});
			
			try {
				categorizedItems.get(entry.getKey()).addAll(nuixCase.search(entry.getValue()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for(Map.Entry<String,List<Item>> entry : categorizedItems.entrySet()){
			Set<Item> intersectedItems = utilities.getItemUtility().intersection(entry.getValue(),inputItems);
			if(intersectedItems.size() > 0){
				data.recordItemsValue(aspectBitmaps, entry.getKey(), intersectedItems);	
			}
		}
	}

	@Override
	public boolean isPerItem() {
		return false;
	}
}
