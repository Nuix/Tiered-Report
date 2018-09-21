package com.nuix.tieredreport;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

import org.roaringbitmap.IntIterator;
import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.aspects.ItemAspect;

import nuix.Case;
import nuix.Item;
import nuix.ItemUtility;
import nuix.Utilities;

/***
 * This class represents and collect data about items used to later generate the actual
 * workbook report.
 * @author Jason Wells
 *
 */
public class TieredReportData {
	private Map<ByteBuffer,ItemInfo> itemInfoByGuid = new HashMap<ByteBuffer,ItemInfo>();
	private Map<String,Map<Object,RoaringBitmap>> bitmaps = new HashMap<String,Map<Object,RoaringBitmap>>();
	private List<ItemInfo> infoByOffset = new ArrayList<ItemInfo>();
	
	private boolean collectAuditedCount = true;
	private boolean collectAuditedSize = true;
	private boolean collectFileSize = true;
	private boolean collectDigestInputSize = true;
	private boolean collectIsCorrupted = true;
	private boolean collectIsEncrypted = true;
	private boolean collectIsDeleted = true;
	
	private boolean abortWasRequested = false;
	
	private ScanProgressCallback scanProgressCallback;
	private ProgressMessageCallback progressMessageCallback;
	
	private void fireProgressUpdate(int count, int total){
		if(scanProgressCallback != null)
			scanProgressCallback.progressUpdated(count,total);
	}
	
	private void fireProgressMessage(String message){
		if(progressMessageCallback != null)
			progressMessageCallback.messageGenerated(message);
	}
	
	public void onProgressUpdated(ScanProgressCallback callback){
		scanProgressCallback = callback;
	}
	
	public void onProgressMessage(ProgressMessageCallback callback){
		progressMessageCallback = callback;
	}
	
	private ItemInfo getItemInfo(Item item){
		String guid = item.getGuid().replaceAll("[^a-zA-Z0-9]", "");
		ByteBuffer guidBytes = ByteBuffer.wrap(DatatypeConverter.parseHexBinary(guid));
		ItemInfo info = itemInfoByGuid.computeIfAbsent(guidBytes, new Function<ByteBuffer, ItemInfo>() {
			@Override
			public ItemInfo apply(ByteBuffer t) {
				ItemInfo info = new ItemInfo();
				
				if(collectAuditedCount){
					info.isAudited = item.isAudited();
				}
				
				if(collectAuditedSize){
					info.auditedSize = item.getAuditedSize();	
				}
				
				if(collectFileSize){
					Long fileSize = item.getFileSize();
					if(fileSize == null)
						fileSize = (long) 0;
					info.fileSize = fileSize;	
				}
				
				if(collectDigestInputSize){
					info.digestInputSize = item.getDigests().getInputSize();
				}
				
				if(collectIsEncrypted){
					info.isEncrypted = item.isEncrypted();	
				}
				
				if(collectIsCorrupted){
					info.isCorrupted = !item.isEncrypted() && item.getProperties().containsKey("FailureDetail");	
				}
				
				if(collectIsDeleted){
					info.isDeleted = item.isDeleted();
				}
				
				infoByOffset.add(info);
				info.offset = infoByOffset.size()-1;
				itemInfoByGuid.put(guidBytes,info);
				
				return info;
			}
		});
		return info;
	}
	
	private RoaringBitmap getBitmap(String propertyName, Object value){
		return bitmaps.get(propertyName).get(value);
	}
	
	private Set<Object> getPropertyValues(String propertyName){
		Map<Object,RoaringBitmap> aspectBitmaps = bitmaps.get(propertyName);
		return aspectBitmaps.keySet();
	}
	
	private List<ItemInfo> getInfos(RoaringBitmap bitmap){
		List<ItemInfo> result = new ArrayList<ItemInfo>();
		IntIterator iter = bitmap.getIntIterator();
		while(iter.hasNext()){
			int offset = iter.next();
			result.add(infoByOffset.get(offset));
		}
		return result;
	}
	
	//http://stackoverflow.com/questions/2270910/how-to-convert-sequence-of-numbers-in-an-array-to-range-of-numbers
	public static ArrayList<int[]> getRanges(int[] indicies)
    {
        ArrayList<int[]> ranges = new ArrayList<int[]>();
        int rstart, rend;   
        int lastnum = indicies[indicies.length-1];
        for (int i = 0; i < indicies.length-1; i++) 
        {     
            rstart = indicies[i];
            rend = rstart;     
            while (indicies[i + 1] - indicies[i] == 1) 
            {       
                rend = indicies[i + 1];
                // increment the index if the numbers sequential       
                if(rend>=lastnum)
                {
                    break;
                }
                else
                {
                    i++;
                }  
            }  
            if(rstart==rend)
            {
                ranges.add(new int[]{rend});
            }
            else
            {
            	ranges.add(new int[]{rstart,rend});
            }
        } 
        return ranges; 
    }
	
	private Function<Object, RoaringBitmap> aspectComputeIfAbsent = new Function<Object, RoaringBitmap>() {
		@Override
		public RoaringBitmap apply(Object t) {
			return new RoaringBitmap();
		}
	};
	
	public void recordItemInfoValue(Map<Object,RoaringBitmap> aspectBitmaps, Object value, ItemInfo itemInfo){
		RoaringBitmap bitmap = aspectBitmaps.computeIfAbsent(value,aspectComputeIfAbsent);
		bitmap.add(itemInfo.offset);
	}
	
	public void recordItemValue(Map<Object,RoaringBitmap> aspectBitmaps, Object value, Item item){
		recordItemInfoValue(aspectBitmaps,value,getItemInfo(item));
	}
	
	public void recordValueWithoutItems(Map<Object,RoaringBitmap> aspectBitmaps, Object value){
		aspectBitmaps.computeIfAbsent(value,aspectComputeIfAbsent);
	}
	
	public void recordItemInfosValue(Map<Object,RoaringBitmap> aspectBitmaps, Object value, Collection<ItemInfo> infos){
		aspectBitmaps.putIfAbsent(value,new RoaringBitmap());
		RoaringBitmap bitmap = aspectBitmaps.get(value);
		recordItemInfos(bitmap,infos);
	}
	
	public void recordItemsValue(Map<Object,RoaringBitmap> aspectBitmaps, Object value, Collection<Item> items){
		recordItemInfosValue(aspectBitmaps,value,items.stream().map(i -> getItemInfo(i)).collect(Collectors.toList()));
	}
	
	public int[] itemsToBitmapIndices(Collection<Item> items){
		int[] result = new int[items.size()];
		int resultIndex = 0;
		for(Item item : items){
			result[resultIndex] = getItemInfo(item).offset;
			resultIndex++;
		}
		return result;
	}
	
	public void recordItemInfos(RoaringBitmap aspectValueBitmap, Collection<ItemInfo> infos){
		if(infos.size() < 1) return;
		int[] offsets = new int[infos.size()];
		int j = 0;
		for(ItemInfo itemInfo : infos){
			offsets[j] = itemInfo.offset;
			j++;
		}
		if(offsets.length < 2){
			aspectValueBitmap.add(offsets[0]);
		} else {
			Arrays.sort(offsets);
			
			List<int[]> ranges = getRanges(offsets);
			//fireProgressMessage("Adding "+ranges.size()+" ranges from "+offsets.size()+" offsets...");
			for(int[] range : ranges){
				if(range.length == 1){
					aspectValueBitmap.add(range[0]);
				}
				else{
					long min = range[0];
					long max = range[1]+1;
					aspectValueBitmap.add(min,max);
				}
			}
		}
	}
		
	private List<ItemAspect> aspects;
	
	public TieredReportData(List<ItemAspect> aspects){
		this.aspects = aspects;
	}
	
	public void scanItems(Case nuixCase, Utilities utilities, Collection<Item> items, List<ReportSheetInfo> sheetInfos) throws Exception{
		abortWasRequested = false;
		for(ItemAspect aspect : aspects){
			aspect.setup(nuixCase,utilities);
			bitmaps.computeIfAbsent(aspect.getAspectName(),new Function<String, Map<Object,RoaringBitmap>>() {
				@Override
				public Map<Object, RoaringBitmap> apply(String t) {
					return new TreeMap<Object, RoaringBitmap>(new Comparator<Object>(){
						@Override
						public int compare(Object a, Object b) {
							return a.toString().compareTo(b.toString());
						}
					});
				}
			});
			if(abortWasRequested){ return; }
		}
		
		int itemIndex = 0;
		int totalItems = items.size();
		
		fireProgressMessage("Performing per item scanning...");
		for(Item item : items){
			itemIndex++;
			ItemInfo currentItemInfo = getItemInfo(item);
			for(ItemAspect aspect : aspects){
				if(!aspect.isPerItem()) continue;
				
				Map<Object, RoaringBitmap> aspectBitmaps = bitmaps.get(aspect.getAspectName());
				aspect.recordItemValues(nuixCase, utilities, aspectBitmaps, this, item, currentItemInfo);
			}
			
			fireProgressUpdate(itemIndex,totalItems);
			if(abortWasRequested){ return; }
		}
		fireProgressUpdate(itemIndex,totalItems);
		
		for(ItemAspect aspect : aspects){
			if(aspect.isPerItem()) continue;
			fireProgressMessage("Performing query scanning: "+aspect.getAspectName());
			Map<Object, RoaringBitmap> aspectBitmaps = bitmaps.get(aspect.getAspectName());
			aspect.recordValues(nuixCase, utilities, aspectBitmaps, this, items);
			if(abortWasRequested){ return; }
		}
		
		for(ItemAspect aspect : aspects){
			fireProgressMessage("Post processing '"+aspect.getAspectName()+"'...");
			aspect.postProcess(bitmaps.get(aspect.getAspectName()), this);
			
			fireProgressMessage("Optimizing '"+aspect.getAspectName()+"' bitmaps...");
			for(RoaringBitmap bitmap : bitmaps.get(aspect.getAspectName()).values()){
				bitmap.runOptimize();
			}
			
			aspect.cleanup();
			if(abortWasRequested){ return; }
		}
		
		// Build bitmap representing sub scope query for each sheet
		ItemUtility iutil = utilities.getItemUtility();
		for(ReportSheetInfo sheetInfo : sheetInfos){
			fireProgressMessage("Performing Sheet Precalculations: "+sheetInfo.getSheetName());
			if(sheetInfo.getSubScopeQuery() != null && sheetInfo.getSubScopeQuery().trim().isEmpty() != true){
				// Get items responsive to sub scope query for this sheet
				fireProgressMessage("  Sheet Sub Scope Query: "+sheetInfo.getSubScopeQuery());
				Set<Item> subScopeItems = nuixCase.searchUnsorted(sheetInfo.getSubScopeQuery());
				fireProgressMessage("  Sheet Sub Scope Items: "+sheetInfo.getSubScopeQuery());
				// Intersect these items with the overall scope queries items from this case
				Set<Item> intersected = iutil.intersection(subScopeItems, items);
				int[] indices = itemsToBitmapIndices(intersected);
				fireProgressMessage("  Sheet Sub Scope Item Infos: "+indices.length);
				for (int i = 0; i < indices.length; i++) {
					sheetInfo.subScopeBitmap.add(indices[i]);
				}
			} else {
				// if there was no sub scope query, then we just take all items
				int[] indices = itemsToBitmapIndices(items);
				fireProgressMessage("  Sheet Sub Scope Item Infos: "+indices.length);
				for (int i = 0; i < indices.length; i++) {
					sheetInfo.subScopeBitmap.add(indices[i]);
				}
			}
		}
		
		fireProgressMessage("Total Infos: "+infoByOffset.size());
	}
	
	public void requestAbort(){
		abortWasRequested = true;
	}
	
	public void iterateData(ReportSheetInfo sheetInfo, List<ItemAspect> reportedAspects, ReportDataCallback callback){
		Stack<ItemAspect> stack = new Stack<ItemAspect>();
		Stack<Object> valueStack = new Stack<Object>();
		for (int i = reportedAspects.size()-1; i >= 0; i--) {
			stack.add(reportedAspects.get(i));
		}
		RoaringBitmap starterBitmap = new RoaringBitmap();
		long min = 0;
		long max = infoByOffset.size();
		starterBitmap.add(min,max);
		
		// Initial bitmap is all items, then whittled down as iteration deeper through each tier occurs
		// to allow for per sheet sub scoping we just whittle down the initial bitmap a bit before we
		// even begin iterating the tiers to just items found in each case to match both the overall
		// query and the sheet's sub scope query
		starterBitmap.and(sheetInfo.subScopeBitmap);
		
		iterateData(sheetInfo,callback,stack,valueStack,starterBitmap);
	}
	
	private void iterateData(ReportSheetInfo sheetInfo, ReportDataCallback callback, Stack<ItemAspect> aspectStack,Stack<Object> valueStack, RoaringBitmap previousTiers){
		if(aspectStack.size() > 1){
			ItemAspect currentAspect = aspectStack.pop();
			for(Object value : currentAspect.customizeValueSort(getPropertyValues(currentAspect.getAspectName()))){
				valueStack.push(value);
				//Report this tier's total
				List<Object> values = new ArrayList<Object>(valueStack);
				RoaringBitmap aspectValueBitmap = getBitmap(currentAspect.getAspectName(),value);
				List<ItemInfo> infos = getInfos(RoaringBitmap.and(previousTiers,aspectValueBitmap));
				if(infos.size() > 0 || currentAspect.reportsZeroItems()){
					TierEncounterData tierData = new TierEncounterData();
					if(collectAuditedCount){
						tierData.totalAuditedCount = infos.stream().filter(i -> i.isAudited).count();
					}
					if(collectAuditedSize){
						tierData.totalAuditedSize = infos.stream().mapToLong(i -> i.auditedSize).sum();	
					}
					if(collectFileSize){
						tierData.totalFileSize = infos.stream().mapToLong(i -> i.fileSize).sum();	
					}
					if(collectDigestInputSize){
						tierData.totalDigestInputSize = infos.stream().mapToLong(i -> i.digestInputSize).sum();
						if(tierData.totalDigestInputSize < 0){ tierData.totalDigestInputSize = 0; }
					}
					if(collectIsCorrupted){
						tierData.totalCorruptedCount = infos.stream().filter(i -> i.isCorrupted).count();
					}
					if(collectIsEncrypted){
						tierData.totalEncryptedCount = infos.stream().filter(i -> i.isEncrypted).count();
					}
					if(collectIsDeleted){
						tierData.totalDeletedCount = infos.stream().filter(i -> i.isDeleted).count();
					}
					
					tierData.aspectNames = aspectStack.stream().map(a -> a.getAspectName()).collect(Collectors.toList());
					tierData.values = values;
					tierData.itemCount = infos.size();
					
					callback.tierEncountered(tierData);
				}
				
				//Continue recursing
				iterateData(sheetInfo,callback,aspectStack,valueStack,RoaringBitmap.and(previousTiers,getBitmap(currentAspect.getAspectName(),value)));
				
				valueStack.pop();
			}
			aspectStack.push(currentAspect);
		} else {
			ItemAspect currentAspect = aspectStack.pop();
			List<TierEncounterData> tierDatas = new ArrayList<TierEncounterData>();
			for(Object value : currentAspect.customizeValueSort(getPropertyValues(currentAspect.getAspectName()))){
				valueStack.push(value);
				List<Object> values = new ArrayList<Object>(valueStack);
				RoaringBitmap aspectValueBitmap = getBitmap(currentAspect.getAspectName(),value);
				List<ItemInfo> infos = getInfos(RoaringBitmap.and(previousTiers,aspectValueBitmap));
				if(infos.size() > 0  || currentAspect.reportsZeroItems()){
					TierEncounterData tierData = new TierEncounterData();
					if(collectAuditedCount){
						tierData.totalAuditedCount = infos.stream().filter(i -> i.isAudited).count();
					}
					if(collectAuditedSize){
						tierData.totalAuditedSize = infos.stream().mapToLong(i -> i.auditedSize).sum();	
					}
					if(collectFileSize){
						tierData.totalFileSize = infos.stream().mapToLong(i -> i.fileSize).sum();	
					}
					if(collectDigestInputSize){
						tierData.totalDigestInputSize = infos.stream().mapToLong(i -> i.digestInputSize).sum();
						if(tierData.totalDigestInputSize < 0){ tierData.totalDigestInputSize = 0; }
					}
					if(collectIsCorrupted){
						tierData.totalCorruptedCount = infos.stream().filter(i -> i.isCorrupted).count();
					}
					if(collectIsEncrypted){
						tierData.totalEncryptedCount = infos.stream().filter(i -> i.isEncrypted).count();
					}
					if(collectIsDeleted){
						tierData.totalDeletedCount = infos.stream().filter(i -> i.isDeleted).count();
					}
					
					tierData.aspectNames = aspectStack.stream().map(a -> a.getAspectName()).collect(Collectors.toList());
					tierData.values = values;
					tierData.itemCount = infos.size();
					
					tierDatas.add(tierData);
				}
				valueStack.pop();
			}
			
			if(sheetInfo.getSortLastTierByItemCount()){
				tierDatas.sort((a,b) -> {
					return Integer.compare(a.itemCount * -1, b.itemCount * -1);
				});
			}
			
			for(TierEncounterData tierData : tierDatas){
				callback.tierEncountered(tierData);	
			}
			
			aspectStack.push(currentAspect);
		}
	}
	
	public boolean getCollectAuditedCount() {
		return collectAuditedCount;
	}

	public void setCollectAuditedCount(boolean collectAuditedCount) {
		this.collectAuditedCount = collectAuditedCount;
	}

	public boolean getCollectAuditedSize() {
		return collectAuditedSize;
	}

	public void setCollectAuditedSize(boolean collectAuditedSize) {
		this.collectAuditedSize = collectAuditedSize;
	}

	public boolean getCollectFileSize() {
		return collectFileSize;
	}

	public void setCollectFileSize(boolean collectFileSize) {
		this.collectFileSize = collectFileSize;
	}

	public boolean getCollectDigestInputSize() {
		return collectDigestInputSize;
	}

	public void setCollectDigestInputSize(boolean collectDigestInputSize) {
		this.collectDigestInputSize = collectDigestInputSize;
	}
	
	public boolean getCollectIsCorrupted() {
		return collectIsCorrupted;
	}

	public void setCollectIsCorrupted(boolean collectIsCorrupted) {
		this.collectIsCorrupted = collectIsCorrupted;
	}

	public boolean getCollectIsEncrypted() {
		return collectIsEncrypted;
	}

	public void setCollectIsEncrypted(boolean collectIsEncrypted) {
		this.collectIsEncrypted = collectIsEncrypted;
	}

	public boolean getCollectIsDeleted() {
		return collectIsDeleted;
	}

	public void setCollectIsDeleted(boolean collectIsDeleted) {
		this.collectIsDeleted = collectIsDeleted;
	}

	public boolean dataWasCollected(){
		return infoByOffset.size() > 0; 
	}
}
