package com.nuix.tieredreport;

/***
 * Callback invoked during reporting when a new tier is being processed.
 * Used to report progress while report is being generated.
 * @author Jason Wells
 *
 */
public interface ReportDataCallback {
	public void tierEncountered(TierEncounterData data);
}
