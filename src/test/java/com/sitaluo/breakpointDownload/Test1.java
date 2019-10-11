package com.sitaluo.breakpointDownload;

import org.junit.Test;

/** 
 * @ClassName: Test1
 * @description: 
 * @author: sitaluo
 * @Date: 2019年10月11日
 */
public class Test1 {

	@Test
	public  void syncDownloadTest(String[] args) {
		String sourceUrl ="https://img0.picmeclub.com/companyIndex/imgs/10.jpg";
		String  targetFile ="G:\\test\\1.jpg";
		DownloadClient.syncDownload(sourceUrl, targetFile);
	}
}
 