package com.sitaluo.breakpointDownload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import com.sitaluo.breakpointDownload.core.DownloadCheckPoint;
import com.sitaluo.breakpointDownload.core.DownloadFileRequest;
import com.sitaluo.breakpointDownload.core.DownloadFileResult;
import com.sitaluo.breakpointDownload.core.DownloadPart;
import com.sitaluo.breakpointDownload.core.DownloadResult;
import com.sitaluo.breakpointDownload.core.PartResult;
import com.sitaluo.breakpointDownload.core.util.HttpUtils;

/**
 * @ClassName: DownloadUtil
 * @description:
 * @author: sitaluo
 * @Date: 2019年10月11日
 */
public class DownloadClient {

	public static boolean syncDownload(String sourceUrl, String targetFile) {
		return SyncDownload.download(sourceUrl, targetFile);
	}

	public static DownloadFileResult breakpointDownload(DownloadFileRequest downloadFileRequest) throws Exception {
		if (downloadFileRequest.getCheckpointFile() == null || downloadFileRequest.getCheckpointFile().isEmpty()) {
			downloadFileRequest.setCheckpointFile(downloadFileRequest.getDownloadFile() + ".dcp");
		}
		return downloadFileWithCheckpoint(downloadFileRequest);
	}

	private static DownloadFileResult downloadFileWithCheckpoint(DownloadFileRequest downloadFileRequest)
			throws Exception {
		DownloadFileResult downloadFileResult = new DownloadFileResult();
		DownloadCheckPoint downloadCheckPoint = new DownloadCheckPoint();
		try {
			downloadCheckPoint.load(downloadFileRequest.getCheckpointFile());
		} catch (Exception e) {
			remove(downloadFileRequest.getCheckpointFile());
		}
		// the download checkpoint is corrupted, download again
		if (!downloadCheckPoint.isValid()) {
			prepare(downloadCheckPoint, downloadFileRequest);
			remove(downloadFileRequest.getCheckpointFile());
		}

		DownloadResult downloadResult = multThreaddownload(downloadCheckPoint, downloadFileRequest);
		for (PartResult partResult : downloadResult.getPartResults()) {
			if (partResult.isFailed()) {
				throw partResult.getException();
			}
		}
		// rename the temp file.
		renameTo(downloadFileRequest.getTempDownloadFile(), downloadFileRequest.getDownloadFile());
		// delete the checkpoint file after a successful download.
		remove(downloadFileRequest.getCheckpointFile());
		downloadFileResult.setLocalfilePath(downloadFileRequest.getDownloadFile());
		return downloadFileResult;
	}

	private static DownloadResult multThreaddownload(DownloadCheckPoint downloadCheckPoint,
			DownloadFileRequest downloadFileRequest) {
			
		return null;
	}

	private static void prepare(DownloadCheckPoint downloadCheckPoint, DownloadFileRequest downloadFileRequest)
			throws IOException {
		downloadCheckPoint.downloadFile = downloadFileRequest.getDownloadFile();
		long fileSize = HttpUtils.getRemoteFileSize(downloadFileRequest.getDownloadFile());
		downloadCheckPoint.downloadParts = splitFile(fileSize, downloadFileRequest.getPartSize());

		createFixedFile(downloadFileRequest.getTempDownloadFile(), fileSize);
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

	public static void createFixedFile(String filePath, long length) throws IOException {
		File file = new File(filePath);
		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(file, "rw");
			rf.setLength(length);
		} finally {
			if (rf != null) {
				rf.close();
			}
		}
	}

	private static boolean remove(String filePath) {
		boolean flag = false;
		File file = new File(filePath);

		if (file.isFile() && file.exists()) {
			flag = file.delete();
		}

		return flag;
	}

	private static void renameTo(String srcFilePath, String destFilePath) throws IOException {
		File srcfile = new File(srcFilePath);
		File destfile = new File(destFilePath);
		moveFile(srcfile, destfile);
	}

	private static void moveFile(final File srcFile, final File destFile) throws IOException {
		if (srcFile == null) {
			throw new NullPointerException("Source must not be null");
		}
		if (destFile == null) {
			throw new NullPointerException("Destination must not be null");
		}
		if (!srcFile.exists()) {
			throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
		}
		if (srcFile.isDirectory()) {
			throw new IOException("Source '" + srcFile + "' is a directory");
		}
		if (destFile.isDirectory()) {
			throw new IOException("Destination '" + destFile + "' is a directory");
		}
		if (destFile.exists()) {
			if (!destFile.delete()) {
				throw new IOException("Failed to delete original file '" + srcFile + "'");
			}
		}

		final boolean rename = srcFile.renameTo(destFile);
		if (!rename) {
			copyFile(srcFile, destFile);
			if (!srcFile.delete()) {
				throw new IOException(
						"Failed to delete original file '" + srcFile + "' after copy to '" + destFile + "'");
			}
		}
	}

	private static void copyFile(File source, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[4096];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			is.close();
			os.close();
		}
	}
}
