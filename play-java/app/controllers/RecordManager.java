package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import models.Record;

public class RecordManager {

	private static final String RECORDS_ZIP = "\\records.zip";
	private static Map<String, String> recFileNames = new HashMap<String, String>();

	public static Map<String, String> getRecFileNames() {
		return recFileNames;
	}

	public static List<Record> getAllRecords(Confing confing) throws Exception {
		String[] recordDirs = confing.getRecordsDir();

		List<Record> recordList = new ArrayList<Record>();
		for (String recDirPath : recordDirs) {
			File dir = new File(recDirPath);

			// TODO: error msg?
			if (dir.exists() && dir.isDirectory()) {
				for (File recordFile : dir.listFiles()) {
					Record rec = new Record();
					rec.setName(recordFile.getName());
					rec.setDate(recordFile.lastModified());
					rec.setSize(recordFile.length());

					recordList.add(rec);

					// Add to map
					recFileNames.put(recordFile.getName(),
							recordFile.getAbsolutePath());
				}
			}
			else{
				throw new Exception("config rec dir: " +recDirPath + "not exists or is not a directory");
			}
		}
		return recordList;
	}

	public static File compressToZip(String[] fileNames) throws Exception {
		FileOutputStream fileZip = new FileOutputStream(Confing.getInstance()
				.getRecordsDir()[0] + RECORDS_ZIP);

		ZipOutputStream zipOutputStream = new ZipOutputStream(fileZip);

		// TODO: change size?
		byte[] buf = new byte[1024];

		for (String fileToDownload : fileNames) {
			// Open rec file
			FileInputStream in = new FileInputStream(
					recFileNames.get(fileToDownload));

			// Add ZIP entry to output stream.
			zipOutputStream.putNextEntry(new ZipEntry(fileToDownload));

			// Transfer bytes from the file to the ZIP file
			int len;

			while ((len = in.read(buf)) > 0) {
				zipOutputStream.write(buf, 0, len);
			}

			// Complete the entry
			zipOutputStream.closeEntry();

			in.close();
		}
		zipOutputStream.close();

		return new File(Confing.getInstance().getRecordsDir()[0] + RECORDS_ZIP);
	}

	

}
