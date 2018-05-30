package com.nuix.tieredreport;

/***
 * Callback for when a process logs a message.
 * @author Jason Wells
 *
 */
public interface ProgressMessageCallback {
	public void messageGenerated(String message);
}
