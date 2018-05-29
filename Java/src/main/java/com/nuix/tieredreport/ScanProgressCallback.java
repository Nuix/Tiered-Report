package com.nuix.tieredreport;

public interface ScanProgressCallback {
	public void progressUpdated(int itemsCurrent, int itemsTotal);
}
