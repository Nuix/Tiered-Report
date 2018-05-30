package com.nuix.tieredreport;

import java.util.List;

import org.roaringbitmap.RoaringBitmap;

import com.nuix.tieredreport.aspects.ItemAspect;

/***
 * Represents the various settings associated to generating a workbook sheet in the
 * generated report workbook.
 * @author Jason Wells
 *
 */
public class ReportSheetInfo {
	private List<ItemAspect> reportedAspects;
	private String sheetName = "Report";
	
	private boolean reportAuditedSize = true;
	private boolean reportFileSize = true;
	private boolean reportDigestInputSize = true;
	private boolean reportCorruptedCount = false;
	private boolean reportEncryptedCount = false;
	private boolean reportDeletedCount = false;
	private boolean reportAuditedCount = false;
	private boolean sortLastTierByItemCount = false;
	private boolean includeCategoryHeaderRows = true;
	private boolean useSparseColumns = false;
	private FileSizeUnit sizeUnit = FileSizeUnit.GIGABYTES;
	
	private String subScopeQuery = null;
	
	// Package internal
	transient RoaringBitmap subScopeBitmap = new RoaringBitmap();
	
	public String getSubScopeQuery() {
		return subScopeQuery;
	}
	
	public void setSubScopeQuery(String subScopeQuery) {
		this.subScopeQuery = subScopeQuery;
	}
	
	public boolean getIncludeCategoryHeaderRows(){ return includeCategoryHeaderRows; }
	public void setIncludeCategoryHeaderRows(boolean value){ includeCategoryHeaderRows = value; }
	
	public ReportSheetInfo(String sheetName, List<ItemAspect> reportedAspects){
		this.sheetName = sheetName;
		this.reportedAspects = reportedAspects;
	}

	public List<ItemAspect> getReportedAspects() {
		return reportedAspects;
	}

	public void setReportedAspects(List<ItemAspect> reportedAspects) {
		this.reportedAspects = reportedAspects;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public boolean getReportAuditedSize() {
		return reportAuditedSize;
	}

	public void setReportAuditedSize(boolean reportAuditedSize) {
		this.reportAuditedSize = reportAuditedSize;
	}

	public boolean getReportFileSize() {
		return reportFileSize;
	}

	public void setReportFileSize(boolean reportFileSize) {
		this.reportFileSize = reportFileSize;
	}

	public boolean getReportDigestInputSize() {
		return reportDigestInputSize;
	}

	public void setReportDigestInputSize(boolean reportDigestInputSize) {
		this.reportDigestInputSize = reportDigestInputSize;
	}

	public boolean getReportCorruptedCount() {
		return reportCorruptedCount;
	}

	public void setReportCorruptedCount(boolean reportCorruptedCount) {
		this.reportCorruptedCount = reportCorruptedCount;
	}

	public boolean getReportEncryptedCount() {
		return reportEncryptedCount;
	}

	public void setReportEncryptedCount(boolean reportEncryptedCount) {
		this.reportEncryptedCount = reportEncryptedCount;
	}

	public boolean getReportDeletedCount() {
		return reportDeletedCount;
	}

	public void setReportDeletedCount(boolean reportDeletedCount) {
		this.reportDeletedCount = reportDeletedCount;
	}

	public boolean getSortLastTierByItemCount() {
		return sortLastTierByItemCount;
	}

	public void setSortLastTierByItemCount(boolean sortLastTierByItemCount) {
		this.sortLastTierByItemCount = sortLastTierByItemCount;
	}

	public boolean getReportAuditedCount() {
		return reportAuditedCount;
	}

	public void setReportAuditedCount(boolean reportAuditedCount) {
		this.reportAuditedCount = reportAuditedCount;
	}
	
	public boolean getUseSparseColumns() {
		return useSparseColumns;
	}
	
	public void setUseSparseColumns(boolean useSparseColumns) {
		this.useSparseColumns = useSparseColumns;
	}
	
	public FileSizeUnit getSizeUnit() {
		return sizeUnit;
	}
	
	public void setSizeUnit(FileSizeUnit sizeUnit) {
		this.sizeUnit = sizeUnit;
	}
}
