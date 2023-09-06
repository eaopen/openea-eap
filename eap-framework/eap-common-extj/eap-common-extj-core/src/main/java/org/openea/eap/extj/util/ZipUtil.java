package org.openea.eap.extj.util;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;

import java.io.File;
import java.io.InputStream;

public class ZipUtil {

	/**
	 * 压缩文件
	 * @param zipFilePath zip文件
	 * @param filePath  文件路径
	 * @param newFileName 在zip中的文件名称
	 * @throws ZipException
	 */
	public static void fileAddToZip(String zipFilePath, String filePath, String newFileName)  throws Exception {
		ZipFile zipFile = new ZipFile(new File(zipFilePath));
		ZipParameters zipParameters = new ZipParameters();
		zipParameters.setCompressionMethod(CompressionMethod.DEFLATE); //设置压缩方法
		zipParameters.setCompressionLevel(CompressionLevel.NORMAL); ////设置压缩级别
		zipParameters.setFileNameInZip(newFileName);
		zipFile.addFile(filePath, zipParameters);
	}

	/**
	 * 压缩文件
	 * @param zipFilePath zip文件
	 * @param inputStream  文件输入流
	 * @param newFileName 在zip中的文件名称
	 * @throws ZipException
	 */
	public static void fileAddToZip(String zipFilePath, InputStream inputStream, String newFileName)  throws Exception {
		ZipFile zipFile = new ZipFile(new File(zipFilePath));
		ZipParameters zipParameters = new ZipParameters();
		zipParameters.setCompressionMethod(CompressionMethod.DEFLATE); //设置压缩方法
		zipParameters.setCompressionLevel(CompressionLevel.NORMAL); ////设置压缩级别
		zipParameters.setFileNameInZip(newFileName);
		zipFile.addStream(inputStream, zipParameters);
	}


	public static void main(String[] args) throws Exception {
		fileAddToZip("D:/xx/xpx.zip", "D:/xx/运行.txt", "运行2222.txt");
		System.out.println("生成成功");
	}

}
