package com.sitaluo.breakpointDownload.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.sitaluo.breakpointDownload.core.event.ProgressEventType;
import com.sitaluo.breakpointDownload.core.event.ProgressListener;
import com.sitaluo.breakpointDownload.core.event.ProgressPublisher;
import com.sitaluo.breakpointDownload.core.util.FileUtils;

/**
 * @ClassName: DownloadUtil
 * @description:
 * @author: sitaluo
 * @Date: 2019年10月11日
 */
public class DownloadClient {

	/**
	 * 断点续传多线程分片下载
	 * @param downloadFileRequest
	 * @return
	 * @throws Throwable
	 */
	public static DownloadFileResult breakpointDownload(DownloadFileRequest downloadFileRequest) throws Throwable {
		if (downloadFileRequest.getCheckpointFile() == null || downloadFileRequest.getCheckpointFile().isEmpty()) {
			downloadFileRequest.setCheckpointFile(downloadFileRequest.getDownloadFile() + ".dcp");
		}
		return downloadFileWithCheckpoint(downloadFileRequest);
	}

	private static DownloadFileResult downloadFileWithCheckpoint(DownloadFileRequest downloadFileRequest)
			throws Throwable {
		DownloadFileResult downloadFileResult = new DownloadFileResult();
		DownloadCheckPoint downloadCheckPoint = new DownloadCheckPoint();
		try {
			downloadCheckPoint.load(downloadFileRequest.getCheckpointFile());
		} catch (Exception e) {
			FileUtils.remove(downloadFileRequest.getCheckpointFile());
		}
		// the download checkpoint is corrupted, download again
		if (!downloadCheckPoint.isValid()) {
			prepare(downloadCheckPoint, downloadFileRequest);
			FileUtils.remove(downloadFileRequest.getCheckpointFile());
		}
		downloadCheckPoint.setRemoteFileUrl(downloadFileRequest.getRemoteFileUrl());
		
		ProgressListener listener = downloadFileRequest.getProgressListener();
		//发布开始下载事件
	    ProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_STARTED_EVENT);
	        
		DownloadResult downloadResult = multThreaddownload(downloadCheckPoint, downloadFileRequest);
		for (PartResult partResult : downloadResult.getPartResults()) {
			if (partResult.isFailed()) {
				 ProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_PART_FAILED_EVENT);
				throw partResult.getException();
			}
		}
		
		//发布下载完成事件
        ProgressPublisher.publishProgress(listener, ProgressEventType.TRANSFER_COMPLETED_EVENT);
        
		// rename the temp file.
		FileUtils.renameTo(downloadFileRequest.getTempDownloadFile(), downloadFileRequest.getDownloadFile());
		// delete the checkpoint file after a successful download.
		FileUtils.remove(downloadFileRequest.getCheckpointFile());
		downloadFileResult.setLocalfilePath(downloadFileRequest.getDownloadFile());
		return downloadFileResult;
	}

	private static DownloadResult multThreaddownload(DownloadCheckPoint downloadCheckPoint,
			DownloadFileRequest downloadFileRequest) throws Throwable{
		
		DownloadResult downloadResult = new DownloadResult();
        ArrayList<PartResult> taskResults = new ArrayList<PartResult>();
        ExecutorService service = Executors.newFixedThreadPool(downloadFileRequest.getTaskNum());
        ArrayList<Future<PartResult>> futures = new ArrayList<Future<PartResult>>();
        List<DownloadTask> tasks = new ArrayList<DownloadTask>();

        ProgressListener listener = downloadFileRequest.getProgressListener();
        downloadFileRequest.setProgressListener(null);
        
        // Compute the size of data pending download.
        long contentLength = 0;
        for (int i = 0; i < downloadCheckPoint.downloadParts.size(); i++) {
            if (!downloadCheckPoint.downloadParts.get(i).isCompleted) {
                long partSize = downloadCheckPoint.downloadParts.get(i).end - downloadCheckPoint.downloadParts.get(i).start + 1;
                contentLength += partSize;
            }
        }
        
        ProgressPublisher.publishResponseContentLength(listener, contentLength);
        
        // Concurrently download parts.
        for (int i = 0; i < downloadCheckPoint.downloadParts.size(); i++) {
        	//System.out.println("downloadParts["+i+"] isCompleted:" + downloadCheckPoint.downloadParts.get(i).isCompleted);
            if (!downloadCheckPoint.downloadParts.get(i).isCompleted) {
            	DownloadTask task = new DownloadTask(i, "download-" + i, downloadCheckPoint, i, downloadFileRequest,listener);
                futures.add(service.submit(task));
                tasks.add(task);
            } else {
                taskResults.add(new PartResult(i + 1, downloadCheckPoint.downloadParts.get(i).start,
                        downloadCheckPoint.downloadParts.get(i).end));
            }
        }
        service.shutdown();
        
        // Waiting for all parts download,
        service.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        for (Future<PartResult> future : futures) {
            try {
                PartResult tr = future.get();
                taskResults.add(tr);
            } catch (ExecutionException e) {
                throw e.getCause();
            }
        }
        
        // Sorts the download result by the part number.
        Collections.sort(taskResults, (p1,p2)-> p1.getNumber() - p2.getNumber());
        // sets the return value.
        downloadResult.setPartResults(taskResults);
        downloadFileRequest.setProgressListener(listener);

        return downloadResult;
	}

	private static void prepare(DownloadCheckPoint downloadCheckPoint, DownloadFileRequest downloadFileRequest)
			throws IOException {
		downloadCheckPoint.downloadFile = downloadFileRequest.getDownloadFile();
		downloadCheckPoint.remoteFileUrl = downloadFileRequest.getRemoteFileUrl();
		FileMetaData fileMetaData = HttpUtils.getRemoteFileSize(downloadFileRequest.getRemoteFileUrl());
		downloadCheckPoint.setFileSize(fileMetaData.getFileSize());
		downloadCheckPoint.setLastModified(fileMetaData.getLastModified());
		downloadCheckPoint.downloadParts = splitFile(fileMetaData.getFileSize(), downloadFileRequest.getPartSize());

		FileUtils.createFixedFile(downloadFileRequest.getTempDownloadFile(), fileMetaData.getFileSize());
	}

	private static ArrayList<DownloadPart> splitFile(long objectSize, long partSize) {
		ArrayList<DownloadPart> parts = new ArrayList<DownloadPart>();

		long partNum = objectSize / partSize;
		if (partNum >= 10000) {
			partSize = objectSize / (10000 - 1);
		}

		long offset = 0L;
		for (int i = 0; offset < objectSize; offset += partSize, i++) {
			DownloadPart part = new DownloadPart();
			part.index = i;
			part.start = offset;
			part.end = getPartEnd(offset, objectSize, partSize);
			parts.add(part);
		}

		return parts;
	}

	private static long getPartEnd(long begin, long total, long per) {
		if (begin + per > total) {
			return total - 1;
		}
		return begin + per - 1;
	}

	
}
