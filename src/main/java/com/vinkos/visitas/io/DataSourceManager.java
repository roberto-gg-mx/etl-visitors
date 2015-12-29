package com.vinkos.visitas.io;
//TODO: control the exceptions and flow of control

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.vinkos.visitas.entity.Error;
import com.vinkos.visitas.entity.ErrorManager;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class DataSourceManager {

	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	final static Logger logger = Logger.getLogger(DataSourceManager.class);

	public static void write(InputStream inputStream, String fileName) {
		OutputStream outputStream = null;

		try {
			// write the inputStream to a FileOutputStream
			outputStream = new FileOutputStream(new File(fileName));

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		} catch (IOException ex) {
			logger.error("Error writing inputstream file.", ex);
		} finally {
			if (outputStream != null) {
				try {
					// outputStream.flush();
					outputStream.close();
				} catch (IOException ex) {
					logger.error("Error closing outputStream.", ex);
				}

			}
		}
	}

	private static boolean sameDay(Calendar date1, Calendar date2) {
		return (date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR))
				&& (date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR));
	}

	@SuppressWarnings("unchecked")
	public static List<String> lookupFiles(String pathResource, String ipv4, String username, String pwd,
			String backupPath, Set<String> backedUp) {

		List<String> list = new ArrayList<>();
		JSch jsch = new JSch();
		Session session = null;
		Date lastmodified;
		Calendar calendar = GregorianCalendar.getInstance();
		Calendar today = GregorianCalendar.getInstance();
		String dst;

		try {
			session = jsch.getSession(username, ipv4, 22);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(pwd);
			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;

			sftpChannel.cd(pathResource);

			Vector<LsEntry> entries = sftpChannel.ls("report_*.txt");
			for (LsEntry entry : entries) {
				lastmodified = new Date(entry.getAttrs().getMTime() * 1000L);
				calendar.setTime(lastmodified);
				today.setTime(new Date());
				if (sameDay(calendar, today)) {
					if (!backedUp.contains(entry.getFilename().toString())) {
						dst = backupPath + sdf.format(today) + entry.getFilename();
						sftpChannel.get(entry.getFilename(), dst);
						// OR
						// InputStream in =
						// sftpChannel.get(entry.getFilename());
						// write(in, backupPath);
						// if (in != null) { try { in.close();} catch
						// (IOException ex) { logger.error("Error reading
						// inputStream with sftp", ex); } }

						list.add(entry.getFilename());
					}
				}
			}

			sftpChannel.exit();
			session.disconnect();
		} catch (JSchException ex) {
			logger.error("Error in jsch process", ex);
		} catch (SftpException ex) {
			logger.error("Error in sftp process", ex);
		}
		return list;
	}

	public static boolean createZipFile(String path, String fileName) {
		String zipFileName = path + fileName + ".zip";
		try {
			FileOutputStream fos = new FileOutputStream(zipFileName);
			ZipOutputStream zos = new ZipOutputStream(fos);

			addToZipFile(fileName, zos);

			zos.close();
			fos.close();
		} catch (IOException ex) {
			logger.error("Error writing backup file", ex);
			ErrorManager.addError(fileName, Error.Type.BACKUP_CREATION);
			return false;
		}
		return true;
	}

	public static void addToZipFile(String zipFileName, List<String> fileNames) {
		try {
			FileOutputStream fos = new FileOutputStream(zipFileName);
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (String fileName : fileNames) {
				addToZipFile(fileName, zos);
			}

			zos.close();
			fos.close();
		} catch (FileNotFoundException ex) {
			logger.error("Error adding file to zip file", ex);
		} catch (IOException ex) {
			logger.error("IO Error adding file to zip file", ex);
		}
	}

	public static void addToZipFile(String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {
		File file = new File(fileName);
		System.out.println(file.getAbsolutePath());
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

	public static Set<String> listFiles(String path, String ext) {
		Set<String> names = new HashSet<>();
		Date today = new Date();
		try {
			List<File> filesInFolder = Files.walk(Paths.get(path)).filter(Files::isRegularFile)
					.filter(p -> p.getFileName().toString().endsWith(ext)).map(Path::toFile)
					.collect(Collectors.toList());
			for (File file : filesInFolder) {
				names.add(file.getName().replace(".zip", "").replace(sdf.format(today), ""));
			}
			System.out.println(names);
		} catch (IOException ex) {
			logger.error("IO Error listing backup files", ex);
		}
		return names;
	}

	public static void main(String args[]) {
		String backupPath = Configuration.get("backupPath").toString();
		String pathResource = Configuration.get("pathResource").toString();
		String username = Configuration.get("username").toString();
		String password = Configuration.get("password").toString();
		String ipv4 = Configuration.get("url").toString();
		Set<String> backedUp = listFiles(backupPath, ".zip");
		for (String file : lookupFiles(pathResource, ipv4, username, password, backupPath, backedUp)) {
			// Process
			createZipFile(backupPath, file);
			dropFile(backupPath, file);// Local
		}
	}

	public static boolean dropFile(String backupPath, String fileName) {
		File file = new File(backupPath + fileName);
		if (file.exists()) {
			return file.delete();
		}

		return false;
	}
}