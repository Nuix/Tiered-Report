package com.nuix.tieredreport;

import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.aspose.cells.BackgroundType;
import com.aspose.cells.Cell;
import com.aspose.cells.Color;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.Style;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.nuix.tieredreport.aspects.ItemAspect;

public class ReportGenerator {
	private static int currentMetricColumns = 0;
	private static int currentAdditionalItemCountColumns = 0;
	private static boolean useSIUnits = true;
	
	public static void generateReport(TieredReportData data, File destination, List<ReportSheetInfo> reportSheets, ReportGenerationProgressCallback callback) throws Exception{
		// Try to figure the right package for this call since it changes
		// with almost every major release <sigh>
		
		String[] potentialAsposeLocations = new String[]{
				"com.nuix.util.AsposeCells",
				"com.nuix.util.aspose.AsposeCells",
				"com.nuix.data.util.aspose.AsposeCells",
		};
		boolean foundAspose = false;
		
		for(String packageToTry : potentialAsposeLocations){
			if(foundAspose){ break; }
			try {
				Class<?> clazz = Class.forName(packageToTry);
				Method method = clazz.getMethod("ensureInitialised");
				method.invoke(null);
				foundAspose = true;
			} catch (ClassNotFoundException e) {}
		}
		
		if(!foundAspose){
			throw new Exception("Couldn't initialize Aspose, this version of the script may not be compatible with current version of Nuix");
		}
		
		Workbook workbook = new Workbook();
		//Workbook starts with empty "Sheet1" so we will remove it
		workbook.getWorksheets().removeAt("Sheet1");
		
		Style headersStyle = workbook.createStyle();
		headersStyle.getFont().setSize(12);
		headersStyle.getFont().setBold(true);
		headersStyle.setForegroundColor(Color.fromArgb(8, 73, 128));
		headersStyle.setPattern(BackgroundType.SOLID);
		headersStyle.getFont().setColor(Color.getWhite());
		
		for(ReportSheetInfo sheetInfo : reportSheets){
			//Create our sheet
			Worksheet sheet = workbook.getWorksheets().add(sheetInfo.getSheetName());
			List<ItemAspect> aspects = sheetInfo.getReportedAspects();
			
			currentMetricColumns = 0;
			currentAdditionalItemCountColumns = 0;
			
			//Write out the headers row
			List<Object> headers = aspects.stream().map(a -> a.getAspectReportLabel()).collect(Collectors.toList());
			headers.add("Item Count");
			if(sheetInfo.getReportCorruptedCount()){
				headers.add("Total Corrupted Count");
				currentMetricColumns++;
				currentAdditionalItemCountColumns++;
			}
			
			if(sheetInfo.getReportEncryptedCount()){
				headers.add("Total Encrypted Count");
				currentMetricColumns++;
				currentAdditionalItemCountColumns++;
			}
			
			if(sheetInfo.getReportDeletedCount()){
				headers.add("Total Deleted Count");
				currentMetricColumns++;
				currentAdditionalItemCountColumns++;
			}
			
			if(sheetInfo.getReportAuditedCount()){
				headers.add("Total Audited Count");
				currentMetricColumns++;
				currentAdditionalItemCountColumns++;
			}
			
			if(sheetInfo.getReportAuditedSize()){
				switch (sheetInfo.getSizeUnit()) {
					case BYTES:
						headers.add("Total Audited Size Bytes");
						break;
					case DYNAMIC:
						headers.add("Total Audited Size");
						break;
					default:
						headers.add("Total Audited Size GB");
						break;
				}
				currentMetricColumns++;
			}
			
			if(sheetInfo.getReportFileSize()){
				switch (sheetInfo.getSizeUnit()) {
					case BYTES:
						headers.add("Total File Size Bytes");
						break;
					case DYNAMIC:
						headers.add("Total File Size");
						break;
					default:
						headers.add("Total File Size GB");
						break;
				}
				currentMetricColumns++;
			}
			
			if(sheetInfo.getReportDigestInputSize()){
				switch (sheetInfo.getSizeUnit()) {
					case BYTES:
						headers.add("Total Digest Input Size Bytes");
						break;
					case DYNAMIC:
						headers.add("Total Digest Input Size");
						break;
					default:
						headers.add("Total Digest Input Size GB");
						break;
				}
				currentMetricColumns++;
			}
			
			writeRow(sheet,headers,0);
			
			//Set header styles
			applyStyle(sheet,headersStyle,0,0,0,aspects.size()+currentMetricColumns);
			
			//Iterate report data and generate rest of sheet
			data.iterateData(sheetInfo,aspects, new ReportDataCallback(){
				List<Object> rowValues = new ArrayList<Object>();
				List<Object> previousRowValues = new ArrayList<Object>();
				int currentRowNumber = 1;
				Style[] categoryHeaderStyles = null;
				@Override
				public void tierEncountered(TierEncounterData tierData) {
					if(callback != null){
						callback.progressUpdated(sheetInfo.getSheetName(), String.join("/",tierData.aspectNames));
					}
					
					// Skip category header rows if settings state to
					if(tierData.values.size()-1 != aspects.size()-1 && !sheetInfo.getIncludeCategoryHeaderRows()){
						return;
					}
					
					if(categoryHeaderStyles == null){
						categoryHeaderStyles = new Style[aspects.size()];
						for(int i = 0; i < aspects.size();i++){
							Style categoryHeaderStyle = workbook.createStyle();
							categoryHeaderStyle.setForegroundColor(getTint(0,153,255,(float)i/2.0f));
							categoryHeaderStyle.setPattern(BackgroundType.SOLID);
							categoryHeaderStyle.getFont().setBold(true);
							categoryHeaderStyles[i] = categoryHeaderStyle;
						}
					}
					
					rowValues.clear();
					
					if (sheetInfo.getUseSparseColumns()){
						for (int i = 0; i < aspects.size(); i++) {
							if(i <= tierData.values.size()-1){
								Object tierDatum = tierData.values.get(i);
								if(previousRowValues.size()-1 >= i && tierDatum == previousRowValues.get(i)){
									rowValues.add("");
								} else {
									rowValues.add(tierDatum);
								}
							} else {
								rowValues.add("");
							}
						}
						previousRowValues = tierData.values;
					} else {
						for (int i = 0; i < aspects.size(); i++) {
							if(i <= tierData.values.size()-1){
								rowValues.add(tierData.values.get(i));
							} else {
								rowValues.add("");
							}
						}	
					}
					
					rowValues.add(tierData.itemCount);
					if(sheetInfo.getReportCorruptedCount()){
						rowValues.add(tierData.totalCorruptedCount);
					}
					if(sheetInfo.getReportEncryptedCount()){
						rowValues.add(tierData.totalEncryptedCount);
					}
					if(sheetInfo.getReportDeletedCount()){
						rowValues.add(tierData.totalDeletedCount);
					}
					if(sheetInfo.getReportAuditedCount()){
						rowValues.add(tierData.totalAuditedCount);
					}
					if(sheetInfo.getReportAuditedSize()){
						switch (sheetInfo.getSizeUnit()) {
							case BYTES:
								rowValues.add(tierData.totalAuditedSize);
								break;
							case DYNAMIC:
								rowValues.add(bytesToDynamicSize(tierData.totalAuditedSize,2));
								break;
							default:
								rowValues.add(bytesToGb(tierData.totalAuditedSize,2));
								break;
						}
					}
					if(sheetInfo.getReportFileSize()){
						switch (sheetInfo.getSizeUnit()) {
							case BYTES:
								rowValues.add(tierData.totalFileSize);
								break;
							case DYNAMIC:
								rowValues.add(bytesToDynamicSize(tierData.totalFileSize,2));
								break;
							default:
								rowValues.add(bytesToGb(tierData.totalFileSize,2));
								break;
						}
					}
					if(sheetInfo.getReportDigestInputSize()){
						switch (sheetInfo.getSizeUnit()) {
							case BYTES:
								rowValues.add(tierData.totalDigestInputSize);
								break;
							case DYNAMIC:
								rowValues.add(bytesToDynamicSize(tierData.totalDigestInputSize,2));
								break;
							default:
								rowValues.add(bytesToGb(tierData.totalDigestInputSize,2));
								break;
						}
					}
					
					writeRow(sheet,rowValues,currentRowNumber);
					if(tierData.values.size()-1 != aspects.size()-1){
						applyStyle(sheet,categoryHeaderStyles[tierData.values.size()-1],currentRowNumber,currentRowNumber,tierData.values.size()-1,aspects.size()+currentMetricColumns);
					}
					currentRowNumber++;
				}
			});
			
			int minCol = 0;
			int maxCol = 0;
			
			minCol = aspects.size();
			maxCol = aspects.size()+currentAdditionalItemCountColumns;
			System.out.println("Applying '#,##0' format to cols "+minCol+"-"+maxCol);
			applyFormat(sheet, "#,##0", 1, sheet.getCells().getRows().getCount(), minCol, maxCol);
			
			if(sheetInfo.getSizeUnit() == FileSizeUnit.GIGABYTES){
				minCol = aspects.size()+1+currentAdditionalItemCountColumns;
				maxCol = aspects.size()+currentMetricColumns;
				System.out.println("Applying '0.000' format to cols "+minCol+"-"+maxCol);
				applyFormat(sheet, "0.000", 1, sheet.getCells().getRows().getCount(), minCol, maxCol);	
			} else if (sheetInfo.getSizeUnit() == FileSizeUnit.BYTES){
				minCol = aspects.size()+1+currentAdditionalItemCountColumns;
				maxCol = aspects.size()+currentMetricColumns;
				System.out.println("Applying '0.000' format to cols "+minCol+"-"+maxCol);
				applyFormat(sheet, "#,##0", 1, sheet.getCells().getRows().getCount(), minCol, maxCol);
			}
			
			sheet.autoFitColumns();
			sheet.freezePanes(1, 0, 1, 0);
		}
		
		workbook.save(destination.getPath(),FileFormatType.XLSX);
		workbook.dispose();
	}
	
	public static double bytesToGb(long bytes,int decimalPlaces){
		double gb = 0.0d;
		double gb_in_bytes = 0.0d;
		
		if(useSIUnits)
			gb_in_bytes = Math.pow(1000d,3d);
		else
			gb_in_bytes = Math.pow(1024d,3d);
		
		gb = ((double)bytes) / gb_in_bytes;
		return round(gb,decimalPlaces);
	}
	
	public static String bytesToDynamicSize(long bytes, int decimalPlaces){
		double unit_base = 0.0d;
		
		if(useSIUnits)
			unit_base = 1000d;
		else
			unit_base = 1024d;
		
		double kb_in_bytes = unit_base;
		double mb_in_bytes = kb_in_bytes * unit_base;
		double gb_in_bytes = mb_in_bytes * unit_base;
		
		if (bytes >= gb_in_bytes){
			double gb = ((double)bytes) / gb_in_bytes;
			gb = round(gb,decimalPlaces);
			return Double.toString(gb)+" GB";
		} else if (bytes >= mb_in_bytes){
			double mb = ((double)bytes) / mb_in_bytes;
			mb = round(mb,decimalPlaces);
			return Double.toString(mb)+" MB";
		} else if (bytes >= kb_in_bytes){
			double kb = ((double)bytes) / kb_in_bytes;
			kb = round(kb,decimalPlaces);
			return Double.toString(kb)+" KB";
		} else{
			return Long.toString(bytes)+" Bytes";
		}
			 
	}
	
	public static double round(double value, int numberOfDigitsAfterDecimalPoint) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(numberOfDigitsAfterDecimalPoint,
                BigDecimal.ROUND_HALF_UP);
        return bigDecimal.doubleValue();
    }
	
	private static void writeRow(Worksheet sheet, List<Object> values, int rowNumber){
		for (int i = 0; i <values.size(); i++) {
			Cell cell = sheet.getCells().get(rowNumber, i);
			cell.setValue(values.get(i));
		}
	}
	
	private static void applyStyle(Worksheet sheet,Style style, int rowBeg, int rowEnd, int colBeg, int colEnd){
		for(int r = rowBeg;r <= rowEnd;r++){
			for(int c = colBeg;c <= colEnd;c++){
				Cell cell = sheet.getCells().get(r, c);
				cell.setStyle(style);
			}
		}
	}
	
	protected static void applyFormat(Worksheet sheet, String format, int rowBeg, int rowEnd, int colBeg, int colEnd){
		for(int r = rowBeg;r <= rowEnd;r++){
			for(int c = colBeg;c <= colEnd;c++){
				Cell cell = sheet.getCells().get(r, c);
				Style style = cell.getStyle();
				style.setCustom(format);
				cell.setStyle(style);
			}
		}
	}
	
	private static int tintChannel(int colorChannelValue, float degree){
		if(degree == 0)
			return colorChannelValue;
		
		int tint = (int) (colorChannelValue + (0.25 * degree * (255 - colorChannelValue)));
		if(tint < 0)
			return 0;
		else if(tint > 255)
			return 255;
		else
			return tint;
	}
	
	private static Color getTint(int r, int g, int b, float degree){
		return Color.fromArgb(
				tintChannel(r,degree),
				tintChannel(g,degree),
				tintChannel(b,degree)
			);
	}

	public static boolean getUseSIUnits() {
		return useSIUnits;
	}

	public static void setUseSIUnits(boolean useSIUnits) {
		ReportGenerator.useSIUnits = useSIUnits;
	}
}
