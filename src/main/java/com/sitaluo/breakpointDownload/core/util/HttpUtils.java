package com.sitaluo.breakpointDownload.core.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/** 
 * @ClassName: HttpUtils
 * @description: 
 * @author: sitaluo
 * @Date: 2019年10月11日
 */
public class HttpUtils {

	public static int getRemoteFileSize(String fileUrl) throws IOException{
		try {
			URL url = new URL(fileUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			int code = conn.getResponseCode();
			if (code == 200) 
			{
				int length = conn.getContentLength();
				return length;
			}else {
				throw new Exception("获取文件长度失败,code:"+code);
			}
		} catch (Exception e) {
			throw new IOException("获取文件长度失败",e);
		}
	}
}
 