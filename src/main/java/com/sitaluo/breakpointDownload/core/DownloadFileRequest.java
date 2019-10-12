package com.sitaluo.breakpointDownload.core; 
/** 
 * @ClassName: DownloadRequest
 * @description: 
 * @author: sitaluo
 * @Date: 2019年10月11日
 */
public class DownloadFileRequest {
	public static final int DEFAULT_PART_SIZE = 500 * 1024;
	public static final int DEFAULT_TASK_NUM = 2;
	public String remoteFileUrl; // to download file url
	private String downloadFile; // local file path
	private Integer partSize; //one part size
	private Integer taskNum; //download thread num
	private String checkpointFile;
	
	public DownloadFileRequest() {
		this.partSize = DEFAULT_PART_SIZE;
		this.taskNum = DEFAULT_TASK_NUM;
	}
	
	public String getTempDownloadFile() {
        return downloadFile + ".tmp";
    }
	
	public String getCheckpointFile() {
		return checkpointFile;
	}
	public void setCheckpointFile(String checkpointFile) {
		this.checkpointFile = checkpointFile;
	}
	public String getDownloadFile() {
		return downloadFile;
	}
	public void setDownloadFile(String downloadFile) {
		this.downloadFile = downloadFile;
	}
	public Integer getPartSize() {
		return partSize;
	}
	public void setPartSize(Integer partSize) {
		this.partSize = partSize;
	}
	public Integer getTaskNum() {
		return taskNum;
	}
	public void setTaskNum(Integer taskNum) {
		this.taskNum = taskNum;
	}

	public String getRemoteFileUrl() {
		return remoteFileUrl;
	}

	public void setRemoteFileUrl(String remoteFileUrl) {
		this.remoteFileUrl = remoteFileUrl;
	}
	
	
	
}
 