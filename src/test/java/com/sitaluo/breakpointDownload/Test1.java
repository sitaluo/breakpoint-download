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
		String sourceUrl ="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570872262725&di=0303e99e77afdaefa2c1b16b4910b4b9&imgtype=0&src=http%3A%2F%2Fpic27.nipic.com%2F20130321%2F9678987_225139671149_2.jpg";
		String  targetFile ="G:\\test\\1.jpg";
		DownloadClient.syncDownload(sourceUrl, targetFile);
	}
	
	@Test
	public  void breakpointDownloadTest() throws Throwable {
		String sourceUrl ="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570872262725&di=0303e99e77afdaefa2c1b16b4910b4b9&imgtype=0&src=http%3A%2F%2Fpic27.nipic.com%2F20130321%2F9678987_225139671149_2.jpg";
		String  targetFile ="G:\\test\\1.jpg";
		DownloadFileRequest downloadFileRequest = new DownloadFileRequest();
		downloadFileRequest.setRemoteFileUrl(sourceUrl);
		downloadFileRequest.setDownloadFile(targetFile);
		DownloadClient.breakpointDownload(downloadFileRequest);
		System.out.println("分片下载完成");
	}
}
 