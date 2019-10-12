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
	public  void breakpointDownloadTest() throws Throwable {
		String sourceUrl ="https://img1.picmeclub.com/imgs/live/12739/1F0A8240_1570616870000.JPG";
		String  targetFile ="G:\\test\\1.jpg";
		DownloadFileRequest downloadFileRequest = new DownloadFileRequest();
		downloadFileRequest.setRemoteFileUrl(sourceUrl);
		downloadFileRequest.setDownloadFile(targetFile);
		DownloadClient.breakpointDownload(downloadFileRequest);
		System.out.println("分片下载完成");
	}
}
 