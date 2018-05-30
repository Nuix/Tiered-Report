package com.nuix.tieredreport;

import java.util.List;

/***
 * Used to track summary data while report is being generated.
 * @author Jason Wells
 *
 */
public class TierEncounterData {
	public List<String> aspectNames;
	public List<Object> values;
	public int itemCount = 0; 
	public long totalAuditedCount = 0;
	public long totalAuditedSize = 0;
	public long totalFileSize = 0;
	public long totalDigestInputSize = 0;
	public long totalCorruptedCount = 0;
	public long totalEncryptedCount = 0;
	public long totalDeletedCount = 0;
}
