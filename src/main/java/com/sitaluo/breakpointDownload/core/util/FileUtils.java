package com.sitaluo.breakpointDownload.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/** 
 * @ClassName: FileUtils
 * @description: 
 * @author: sitaluo
 * @Date: 2019年10月12日
 */
public class FileUtils {
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

	public static boolean remove(String filePath) {
		boolean flag = false;
		File file = new File(filePath);

		if (file.isFile() && file.exists()) {
			flag = file.delete();
		}

		return flag;
	}

	public static void renameTo(String srcFilePath, String destFilePath) throws IOException {
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
 