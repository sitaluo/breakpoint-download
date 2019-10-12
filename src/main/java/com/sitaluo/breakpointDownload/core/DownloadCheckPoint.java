package com.sitaluo.breakpointDownload.core;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import com.sitaluo.breakpointDownload.core.util.HttpUtils;

/**
 * @ClassName: DownloadCheckPoint
 * @description:
 * @author: sitaluo
 * @Date: 2019年10月11日
 */
public class DownloadCheckPoint implements Serializable {

	private static final long serialVersionUID = -8444259366601277270L;
	public String downloadFile; // local path for the download.
	public String remoteFileUrl; // to download file url
	public ArrayList<DownloadPart> downloadParts; // download parts list.
	public int md5; // the md5 of checkpoint data.
	public long fileSize;
	public long lastModified;

	public void detailePrint() {
		System.out.println("md5:" + md5 + ",fileSize:" + fileSize);
		for (DownloadPart downloadPart : downloadParts) {
			System.out.println(downloadPart);
		}
	}

	/**
	 * Loads the checkpoint data from the checkpoint file.
	 */
	public synchronized void load(String cpFile) throws IOException, ClassNotFoundException {
		FileInputStream fileIn = new FileInputStream(cpFile);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		DownloadCheckPoint dcp = (DownloadCheckPoint) in.readObject();
		assign(dcp);
		in.close();
		fileIn.close();
	}

	/**
	 * Writes the checkpoint data to the checkpoint file.
	 */
	public synchronized void dump(String cpFile) throws IOException {
		this.md5 = hashCode();
		FileOutputStream fileOut = new FileOutputStream(cpFile);
		ObjectOutputStream outStream = new ObjectOutputStream(fileOut);
		outStream.writeObject(this);
		outStream.close();
		fileOut.close();
	}

	/**
	 * Updates the part's download status.
	 * 
	 * @throws IOException
	 */
	public synchronized void update(int index, boolean completed) throws IOException {
		downloadParts.get(index).isCompleted = completed;
	}

	private void assign(DownloadCheckPoint dcp) {
		this.md5 = dcp.md5;
		this.downloadFile = dcp.downloadFile;
		this.downloadParts = dcp.downloadParts;
		this.remoteFileUrl = dcp.remoteFileUrl;
		this.fileSize = dcp.fileSize;
		this.lastModified = dcp.lastModified;
	}

	/**
	 * Check if the object matches the checkpoint information.
	 */
	public synchronized boolean isValid() {
		if (this.md5 != hashCode()) {
			return false;
		}
		try {
			FileMetaData fileMetaData = HttpUtils.getRemoteFileSize(remoteFileUrl);
			if (fileMetaData.getFileSize() != fileSize || fileMetaData.getLastModified() != lastModified) {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((downloadFile == null) ? 0 : downloadFile.hashCode());
		result = prime * result + ((remoteFileUrl == null) ? 0 : remoteFileUrl.hashCode());
		result = prime * result + ((downloadParts == null) ? 0 : downloadParts.hashCode());
		result = (int) (prime * result + ((fileSize == 0) ? 0 : fileSize));
		result = (int) (prime * result + ((lastModified == 0) ? 0 : lastModified));
		return result;
	}

	public String getDownloadFile() {
		return downloadFile;
	}

	public void setDownloadFile(String downloadFile) {
		this.downloadFile = downloadFile;
	}

	public ArrayList<DownloadPart> getDownloadParts() {
		return downloadParts;
	}

	public void setDownloadParts(ArrayList<DownloadPart> downloadParts) {
		this.downloadParts = downloadParts;
	}

	public int getMd5() {
		return md5;
	}

	public void setMd5(int md5) {
		this.md5 = md5;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public String getRemoteFileUrl() {
		return remoteFileUrl;
	}

	public void setRemoteFileUrl(String remoteFileUrl) {
		this.remoteFileUrl = remoteFileUrl;
	}

}
