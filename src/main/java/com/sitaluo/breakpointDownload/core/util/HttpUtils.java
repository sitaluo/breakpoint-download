package com.sitaluo.breakpointDownload.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.sitaluo.breakpointDownload.core.FileMetaData;

/** 
 * @ClassName: HttpUtils
 * @description: 
 * @author: sitaluo
 * @Date: 2019年10月11日
 */
public class HttpUtils {

	/**
	 * 获取远程文件的大小
	 * @param fileUrl
	 * @return 下载文件工具类
	 * @throws IOException
	 */
	public static FileMetaData getRemoteFileSize(String fileUrl) throws IOException{
		try {
			URL url = new URL(fileUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			int code = conn.getResponseCode();
			if (code == 200) 
			{
				int length = conn.getContentLength();
				long lastModified = conn.getLastModified();
				return new FileMetaData(lastModified,length);
			}else {
				throw new Exception("获取文件长度失败,code:"+code);
			}
		} catch (Exception e) {
			throw new IOException("获取文件长度失败",e);
		}
	}
	
	/**
	 * 下载远程文件某部分的内容
	 * @param fileUrl 远程文件url
	 * @param start 要下载的开始位置
	 * @param end 要下载的内容结束为止
	 * @return
	 * @throws IOException
	 */
	public static InputStream getFileRangeInputStream(String fileUrl,long start, long end) throws IOException {
		try {
			URL url = new URL(fileUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("range", "bytes=" + start + "-" + end);
			int code = conn.getResponseCode();
			if (code == 206) 
			{
				InputStream in = conn.getInputStream();
				return in;
			}else {
				throw new IOException("范围下载失败,code:"+code + ",fileUrl:"+fileUrl);
			}
		} catch (Exception e) {
			throw new IOException("范围下载失败:"+fileUrl,e);
		}
	}
}
 