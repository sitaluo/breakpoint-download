package com.sitaluo.breakpointDownload;

import com.sitaluo.breakpointDownload.core.DownloadClient;
import com.sitaluo.breakpointDownload.core.DownloadFileRequest;
import com.sitaluo.breakpointDownload.core.DownloadFileResult;
/**
 * @ClassName: DownloadFacade
 * @description: 对外提供下载功能门面类
 * @author: sitaluo
 * @Date: 2019年10月13日
 */
public class DownloadFacade {
	
	/**
	 * 断点续传多线程分片下载
	 * @param downloadFileRequest
	 * @return
	 * @throws Throwable
	 */
	public static DownloadFileResult breakpointDownload(DownloadFileRequest downloadFileRequest) throws Throwable {
		return DownloadClient.breakpointDownload(downloadFileRequest);
	}
}
