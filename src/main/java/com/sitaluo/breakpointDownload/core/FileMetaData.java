package com.sitaluo.breakpointDownload.core; 
/** 
 * @ClassName: FileMetaData
 * @description: 
 * @author: sitaluo
 * @Date: 2019年10月12日
 */
class FileMetaData {

	private long lastModified;
	private long fileSize;
	
	public FileMetaData(long lastModified, long fileSize) {
		super();
		this.lastModified = lastModified;
		this.fileSize = fileSize;
	}
	public long getLastModified() {
		return lastModified;
	}
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	
	
}
 