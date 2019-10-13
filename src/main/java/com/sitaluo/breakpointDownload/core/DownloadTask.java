package com.sitaluo.breakpointDownload.core;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.Callable;

class DownloadTask implements Callable<PartResult> {
	public static final int KB = 1024;
    public static final int DEFAULT_BUFFER_SIZE = 8 * KB;
        public DownloadTask(int id, String name, DownloadCheckPoint downloadCheckPoint, int partIndex,
                DownloadFileRequest downloadFileRequest) {
            this.id = id;
            this.name = name;
            this.downloadCheckPoint = downloadCheckPoint;
            this.partIndex = partIndex;
            this.downloadFileRequest = downloadFileRequest;
        }
        
        public PartResult call() throws Exception {
            PartResult tr = null;
            RandomAccessFile output = null;
            InputStream content = null;
            
            try {
                DownloadPart downloadPart = downloadCheckPoint.downloadParts.get(partIndex);
                tr = new PartResult(partIndex + 1, downloadPart.start, downloadPart.end);
                
                output = new RandomAccessFile(downloadFileRequest.getTempDownloadFile(), "rw");  
                output.seek(downloadPart.start);
                
                content = HttpUtils.getFileRangeInputStream(downloadFileRequest.getRemoteFileUrl(), downloadPart.start, downloadPart.end);
                
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int bytesRead = 0;
                while ((bytesRead = content.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                
                //System.out.println("分片下载"+ partIndex + ",startIndex:"+downloadPart.start + ",endIndex:"+ downloadPart.end);
                downloadCheckPoint.update(partIndex, true);
                downloadCheckPoint.dump(downloadFileRequest.getCheckpointFile()); 
                //ProgressPublisher.publishResponseBytesTransferred(progressListener, (downloadPart.end - downloadPart.start + 1));
            } catch (Exception e) {
                tr.setFailed(true);
                tr.setException(e);
                System.out.println(String.format("Task %d:%s upload part %d failed: ", id, name, partIndex));
            } finally {
                if (output != null) {
                    output.close();
                }
                
                if (content != null) {
                    content.close();
                }
            }
                                    
            return tr;
        }
        

        private int id;
        private String name;
        private DownloadCheckPoint downloadCheckPoint;
        private int partIndex;
        private DownloadFileRequest downloadFileRequest;
		
    }