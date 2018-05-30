package com.nuix.tieredreport;

/***
 * Callback invoked when progress is being made during report generation.
 * @author Jason Wells
 *
 */
public interface ReportGenerationProgressCallback {
	public void progressUpdated(String sheetName, String tierPath);
}
