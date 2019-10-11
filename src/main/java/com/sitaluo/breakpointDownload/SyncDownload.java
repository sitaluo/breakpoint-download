package com.sitaluo.breakpointDownload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/** 
 * @ClassName: SyncDownload
 * @description: 同步下载文件
 * @author: sitaluo
 * @Date: 2019年10月11日
 */
public class SyncDownload {

		public static boolean download(String sourceUrl,String targetFile) {
	        //定义输入流和输出流
	        InputStream inputStream=null;
	        OutputStream outputStream=null;
	        //定义文件对象
	        File file = null;

	        try {
	            URL url=new URL(sourceUrl);
	            HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
	            if (httpURLConnection.getResponseCode()==200) {
	                inputStream = httpURLConnection.getInputStream();//获取到下载输入流

	            }
	            file = new File(targetFile);
	            outputStream=new FileOutputStream(file);
	            //创建缓存区
	            byte buffer[]=new byte[4*1024];
	            int length=0;
	            int sum=0;
	            while ((length=inputStream.read(buffer))!=-1){
	                outputStream.write(buffer,0,length);
	                sum+=length;
	                //publishProgress(0,sum);

	            }
	              outputStream.flush();
	              outputStream.close();
	              inputStream.close();

	              return true;
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        return false;
	    }
}
 