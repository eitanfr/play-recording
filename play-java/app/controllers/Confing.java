package controllers;

import java.io.FileInputStream;
import java.util.Properties;

public class Confing {

    private static final String CONF_PATH = "cfg";
    private static final String confingName = "recServer.conf";
    private static final String RECORD_DIR = "recordDir";
    private static final String USERNAME = "ftpUsername";
    private static final String PASSWORD = "ftpPassword";
    private static final String FTP_IP = "ftpIp";
    private static final String TEMP_FILE_PATH = "tempFilesPath";
    private Properties properties = new Properties();

    private static Confing instace;

    public static Confing getInstance() throws Exception {
        // I know it is wrong
        if (instace == null) {
            instace = new Confing();
        }
        return instace;
    }

    private Confing() throws Exception {
        String path = System.getenv(CONF_PATH);

        if (path == null) {
            throw new Exception("could not found configuration path environment variable. \n"
                    + " environment variable should be: " + CONF_PATH);
        }

        properties.load(new FileInputStream(path.concat(confingName)));
    }


    public String[] getRecordsDir() throws Exception {
        // Split by ","
        String[] recordDirs = properties.getProperty(RECORD_DIR).split(",");
        if (recordDirs == null)
            throw new Exception("could not found " + RECORD_DIR + " filed in configuration file\n"
                    + System.getenv(CONF_PATH) + confingName);

        return recordDirs;
    }

    public String getFTPUsername() throws Exception {
        String username = properties.getProperty(USERNAME);
        if (username == null)
            throw new Exception("could not found " + USERNAME + " filed in configuration file\n"
                    + System.getenv(CONF_PATH) + confingName);

        return username;
    }

    public String getFTPPass() throws Exception {
        String pass = properties.getProperty(PASSWORD);
        if (pass == null)
            throw new Exception("could not found " + USERNAME + " filed in configuration file\n "
                    + System.getenv(CONF_PATH) + confingName);
        return pass;
    }

    public String getFtpIpAdress() throws Exception {
        String pass = properties.getProperty(FTP_IP);
        if (pass == null)
            throw new Exception("could not found " + FTP_IP + " filed in configuration file: \n"
                    + System.getenv(CONF_PATH) + confingName);
        return pass;
    }

    public String getTempFilesPath() throws Exception {
        String pass = properties.getProperty(TEMP_FILE_PATH);
        if (pass == null)
            throw new Exception("could not found " + TEMP_FILE_PATH + " filed in configuration file: \n"
                    + System.getenv(CONF_PATH) + confingName);
        return pass;
    }


}
