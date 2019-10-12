package com.sitaluo.breakpointDownload;

import org.junit.Test;

import com.sitaluo.breakpointDownload.core.DownloadFileRequest;

/** 
 * @ClassName: Test1
 * @description: 
 * @author: sitaluo
 * @Date: 2019年10月11日
 */
public class Test1 {

	@Test
	public  void syncDownloadTest() {
		String sourceUrl ="https://img0.picmeclub.com/companyIndex/imgs/10.jpg";
		String  targetFile ="G:\\test\\1.jpg";
		DownloadClient.syncDownload(sourceUrl, targetFile);
	}
	
	@Test
	public  void breakpointDownloadTest() throws Throwable {
		String sourceUrl ="https://img1.picmeclub.com/imgs/live/12745/DSC_9512_1570854204000.JPG";
		String  targetFile ="G:\\test\\1.jpg";
		DownloadFileRequest downloadFileRequest = new DownloadFileRequest();
		downloadFileRequest.setRemoteFileUrl(sourceUrl);
		downloadFileRequest.setDownloadFile(targetFile);
		DownloadClient.breakpointDownload(downloadFileRequest);
		System.out.println("分片下载完成");
	}
}
 