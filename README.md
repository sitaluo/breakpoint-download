# breakpoint-download
#### 断点续传下载工具类

##### 实现原理：

HTTP 请求头 Range：

`Range: bytes=start-end
Range: bytes=100- ：第100个字节及最后个字节的数据
Range: bytes=50-100 ：第50个字节到第100个字节之间的数据.`

`HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	conn.setRequestProperty("range", "bytes=" + start + "-" + end);`

先获取文件总长度，然后分片多线程下载，下载过程中保存分片下载信息，如果中途退出后下次下载的时候检查分片文件信息，已下载的分片不再下载，未完成的分片继续下载，直到完成。

##### 使用示例：

```java
@Test
	public  void breakpointDownloadTest() throws Throwable {
		String sourceUrl ="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570872262725&di=0303e99e77afdaefa2c1b16b4910b4b9&imgtype=0&src=http%3A%2F%2Fpic27.nipic.com%2F20130321%2F9678987_225139671149_2.jpg";
		String  targetFile ="D:\\test\\1.jpg";
		DownloadFileRequest downloadFileRequest = new DownloadFileRequest();
		downloadFileRequest.setRemoteFileUrl(sourceUrl);
		downloadFileRequest.setDownloadFile(targetFile);
		DownloadClient.breakpointDownload(downloadFileRequest);
		System.out.println("分片下载完成");
	}
```



##### 待完善的地方

下载进度监听处理；

在网络极度差的时候某个分片下载可能会超时，导致下载不成功，做到在超时情况自动减小分片重试