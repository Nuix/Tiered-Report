package com.nuix.tieredreport;

/***
 * Represents enough information about an item in a case that the item may be reported on even
 * after that case has been closed (since the script supports iteratively reporting multiple cases).
 * @author Jason Wells
 *
 */
public class ItemInfo {
	public long auditedSize = 0;
	public long fileSize = 0;
	public long digestInputSize = 0;
	public int offset = 0;
	public boolean isAudited = false;
	public boolean isCorrupted = false;
	public boolean isEncrypted = false;
	public boolean isDeleted = false;
}
