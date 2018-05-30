package com.nuix.tieredreport;

/***
 * Callback used to update progress percentage while TieredReportData is collecting
 * information about items.
 * @author Jason Wells
 *
 */
public interface ScanProgressCallback {
	public void progressUpdated(int itemsCurrent, int itemsTotal);
}
