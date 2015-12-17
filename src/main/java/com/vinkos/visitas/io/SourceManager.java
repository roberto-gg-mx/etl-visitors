package com.vinkos.visitas.io;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.attribute.*;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

public class SourceManager {

	public static int fileCounter = 0;

	public void lookupFiles() {
		Path path = FileSystems.getDefault().getPath("logs", "access.log");
	    //BufferReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
		try {
			BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
			System.out.println("creationTime: " + attr.creationTime());
			System.out.println("lastAccessTime: " + attr.lastAccessTime());
			System.out.println("lastModifiedTime: " + attr.lastModifiedTime());
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {

		try {
			FileOutputStream fos = new FileOutputStream("atest.zip");
			ZipOutputStream zos = new ZipOutputStream(fos);

			String file1Name = "file1.txt";
			String file2Name = "file2.txt";
			String file3Name = "folder/file3.txt";
			String file4Name = "folder/file4.txt";
			String file5Name = "f1/f2/f3/file5.txt";

			addToZipFile(file1Name, zos);
			addToZipFile(file2Name, zos);
			addToZipFile(file3Name, zos);
			addToZipFile(file4Name, zos);
			addToZipFile(file5Name, zos);

			zos.close();
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void addToZipFile(String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {

		System.out.println("Writing '" + fileName + "' to zip file");

		File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}
}
