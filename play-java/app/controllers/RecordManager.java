package controllers;

import models.Record;
import play.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class RecordManager {

    private static final java.lang.String TINE_FORMAT = "dd-MM-yy_HH-mm";
    private static Map<String, String> recFileNames = new HashMap<String, String>();

    public static Map<String, String> getRecFileNames() {
        return recFileNames;
    }

    public static List<Record> getAllRecords(Confing confing) throws Exception {
        String[] recordDirs = confing.getRecordsDir();

        List<Record> recordList = new ArrayList<Record>();
        for (String recDirPath : recordDirs) {
            File dir = new File(recDirPath);

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
            } else {
                throw new Exception("config rec dir: " + recDirPath + "not exists or is not a directory");
            }
        }
        return recordList;
    }

    public static String compressToZip(String[] fileNames, boolean csv) throws Exception {
        // Create zip file name
        SimpleDateFormat format = new SimpleDateFormat(TINE_FORMAT);
        String currentDateString = format.format(new Date());
        String recordsZipFileName = "//Records-" + currentDateString + ".zip";

        FileOutputStream fileZip = new FileOutputStream(Confing.getInstance()
                .getTempFilesPath() + recordsZipFileName);

        ZipOutputStream zipOutputStream = new ZipOutputStream(fileZip);

        byte[] buf = new byte[1024];

        for (String fileName : fileNames) {
            // Convert to csv if needed
            String filePathToZip = convertToCsv(fileName, csv);
            Logger.debug(filePathToZip + " before: " + fileName);
            File fileToZip = new File(filePathToZip);

            // Open rec/csv file
            FileInputStream in = new FileInputStream(fileToZip);

            // Add ZIP entry to output stream.
            zipOutputStream.putNextEntry(new ZipEntry(fileName));

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

        return Confing.getInstance().getTempFilesPath() + recordsZipFileName;
    }

    public static String convertToCsv(String fileName, boolean isCsv) {
        if (isCsv) {
            // TODO this...
            // TODO save in temp files path
            return getRecFileNames().get(fileName);
        } else {
            return getRecFileNames().get(fileName);
        }
    }


}
