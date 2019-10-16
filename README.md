# breakpoint-download
#### 断点续传下载工具类

参考了阿里云oss的sdk,阿里云sdk的断点续传下载是针对oss上的文件，修改成了可以下载任意网络文件

##### 实现原理：

HTTP 请求头 Range：

`Range: bytes=start-end
Range: bytes=100- ：第100个字节及最后个字节的数据
Range: bytes=50-100 ：第50个字节到第100个字节之间的数据.`

`HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	conn.setRequestProperty("range", "bytes=" + start + "-" + end);`

先获取文件总长度，然后分片多线程下载，下载过程中保存分片下载信息，如果中途退出后下次下载的时候检查分片文件信息，已下载的分片不再下载，未完成的分片继续下载，直到完成。

**核心类：**

```java
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
 
```



##### 使用示例：

```java
@Test
	public  void breakpointDownloadTest() throws Throwable {
		String sourceUrl ="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570872262725&di=0303e99e77afdaefa2c1b16b4910b4b9&imgtype=0&src=http%3A%2F%2Fpic27.nipic.com%2F20130321%2F9678987_225139671149_2.jpg";
		String  targetFile ="D:\\test\\1.jpg";
		DownloadFileRequest downloadFileRequest = new DownloadFileRequest();
		downloadFileRequest.setRemoteFileUrl(sourceUrl);
		downloadFileRequest.setDownloadFile(targetFile);
        downloadFileRequest.setProgressListener(new SimpleDemoProgressListener());
		DownloadFileResult downloadFileResult = DownloadFacade.breakpointDownload(downloadFileRequest);
		System.out.println("分片下载完成" + downloadFileResult.getLocalfilePath());
	}
```

##### 进度条

```java
public class SimpleDemoProgressListener implements ProgressListener {
    private long bytesRead = 0;
    private long totalBytes = -1;
    private boolean succeed = false;
    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        long bytes = progressEvent.getBytes();
        ProgressEventType eventType = progressEvent.getEventType();
        switch (eventType) {
        case TRANSFER_STARTED_EVENT:
            System.out.println("开始下载......");
            break;
        case RESPONSE_CONTENT_LENGTH_EVENT:
            this.totalBytes = bytes;
            System.out.println("总共"+this.totalBytes + " 字节需要被下载");
            break;
        case RESPONSE_BYTE_TRANSFER_EVENT:
            this.bytesRead += bytes;
            if (this.totalBytes != -1) {
                int percent = (int)(this.bytesRead * 100.0 / this.totalBytes);
                System.out.println("下载了"+bytes + "字节，下载进度: "+ percent+"%(" + this.bytesRead + "/" + this.totalBytes + ")");
            } else {
                System.out.println("下载了"+ bytes + "字节，下载进度未知:%" + "(" + this.bytesRead + "/...)");
            }
            break;
        case TRANSFER_COMPLETED_EVENT:
            this.succeed = true;
            System.out.println("下载成功, 总共" + this.bytesRead + "字节");
            break;
        case TRANSFER_FAILED_EVENT:
            System.out.println("下载失败, 已经下载了" + this.bytesRead + "字节");
            break;
        default:
            break;
        }
    }
    public boolean isSucceed() {
        return succeed;
    }
}
```



##### 待完善的地方

在网络极度差的时候某个分片下载可能会超时，导致下载不成功，做到在超时情况自动减小分片重试