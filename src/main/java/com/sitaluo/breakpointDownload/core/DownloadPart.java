package com.sitaluo.breakpointDownload.core;

import java.io.Serializable;

/**
 * @ClassName: DownloadPart
 * @description:
 * @author: sitaluo
 * @Date: 2019年10月11日
 */
public class DownloadPart implements Serializable {

	private static final long serialVersionUID = 7768600538642378362L;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + (isCompleted ? 1231 : 1237);
		result = prime * result + (int) (end ^ (end >>> 32));
		result = prime * result + (int) (start ^ (start >>> 32));
		return result;
	}

	public int index; // part index (starting from 0).
	public long start; // start index;
	public long end; // end index;
	public boolean isCompleted; // flag of part download finished or not.

	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	public boolean isCompleted() {
		return isCompleted;
	}
	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
	
	@Override
	public String toString() {
		return "DownloadPart [index=" + index + ", start=" + start + ", end=" + end + ", isCompleted=" + isCompleted
				+ "]";
	}
	
}
