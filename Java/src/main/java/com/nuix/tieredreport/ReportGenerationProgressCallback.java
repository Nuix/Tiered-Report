package com.nuix.tieredreport;

public interface ReportGenerationProgressCallback {
	public void progressUpdated(String sheetName, String tierPath);
}
