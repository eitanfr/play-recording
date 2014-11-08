package controllers;

import actions.CorsComposition;
import com.fasterxml.jackson.databind.JsonNode;
import models.Record;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import sun.net.ftp.FtpClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

@CorsComposition.Cors
public class Application extends Controller {

    private static final String USERNAME = "rakia";
    private static final String PASSWORD = "rakia";

    public static Result index() {

        try {
            Confing confing = Confing.getInstance();

            return ok(views.html.index.render(""));

        } catch (Exception e) {
            Logger.error("Error ", e);
            return internalServerError(
                    " INTERNAL_SERVER_ERROR 500 " + e.getMessage() + "</h3>")
                    .as("text/html");
        }

    }

    public static Result sayHello() {

        List<Record> records = null;
        JsonNode recJson = null;

        try {
            // Get records
            records = RecordManager.getAllRecords(Confing.getInstance());
            recJson = Json.toJson(records);

        } catch (Exception e) {
            Logger.error("Error ", e);
            return internalServerError(
                    " INTERNAL_SERVER_ERROR 500 " + e.getMessage()).as(
                    "text/html");
        }

        return ok(recJson);
    }

    public static Result compress(String files) {
        String convertedFile;
        try {
            convertedFile = getCompressedFiles(files, false);
        } catch (Exception e) {
            Logger.error("Error ", e);
            return internalServerError(
                    " INTERNAL_SERVER_ERROR 500 " + e.getMessage()).as(
                    "text/html");
        }

        return ok(convertedFile);
    }

    /**
     * @param file full path
     * @return
     */
    public static Result download(String file) {
        // TODO: problem with hebrew, not inmportant
        File fileToDownload = null;
        try {
            // Check and validate
            if (file == null || file.isEmpty())
                throw new Exception("Tried to download with an empty file name");

            fileToDownload = new File(file);

        } catch (Exception e) {
            Logger.error("Error: " + e.getMessage(), e);
            return internalServerError(
                    " INTERNAL_SERVER_ERROR 500 " + e.getMessage())
                    .as("text/html");
        }

        return ok(fileToDownload);
    }

    private static String getCompressedFiles(String files, boolean csv) throws Exception {
        String compressedFileName = null;
        if (files != null) {
            String[] filesArray = files.split(",");
            if (filesArray.length == 0) {
                return null;
            } else {
                if (filesArray.length == 1) {
                    compressedFileName = RecordManager.convertToCsv(filesArray[0], csv);
                } else {
                    compressedFileName = RecordManager.compressToZip(filesArray, csv);
                }
            }

        }

        // TODO: remove csv?
        return compressedFileName;
    }

    public static Result ftp(String files) {
        FtpClient ftpClient = FtpClient.create();
        try {
            // connect
            int port = FtpClient.defaultPort(); // TODO: default 21 check if
            // realy it is ...
            InetAddress hostname = InetAddress.getByName(Confing.getInstance()
                    .getFtpIpAdress());
            InetSocketAddress adress = new InetSocketAddress(hostname, port);
            ftpClient.connect(adress);

            // login
            String user = Confing.getInstance().getFTPUsername();
            char[] password = Confing.getInstance().getFTPPass().toCharArray();
            ftpClient.login(user, password);

            // set mode
            ftpClient.enablePassiveMode(true); // TODO: validate
            ftpClient.setBinaryType();

            // put file
            String recordsFile = Application.getCompressedFiles(files, false);
            if (recordsFile != null) {
                ftpClient.putFile(recordsFile, new FileInputStream(recordsFile));
            }

        } catch (ConnectException e) {
            Logger.error("Error ", e);
            return internalServerError(
                    " INTERNAL_SERVER_ERROR 500 " + e.getMessage() +
                            "can't connect to ftp server").as("text/html");
        } catch (Exception e) {
            Logger.error("Error ", e);
            return internalServerError(
                    " INTERNAL_SERVER_ERROR 500 " + e.getMessage())
                    .as("text/html");
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.close();
                } catch (IOException e) {
                    Logger.error(e.getMessage());
                }
            }
        }
        return ok("ftp completed");
    }

    public static Result preflight(String path) {
        return ok("");
    }

    public static Result login() {
        JsonNode userJson = request().body().asJson();
        Logger.debug("Tried to login: " + userJson.toString());
        String username = userJson.findPath("user").textValue();
        String password = userJson.findPath("password").textValue();

        if (username.equals(USERNAME) && password.equals(PASSWORD)) {

            return ok();
        } else {
            return badRequest("Invalid user or password");
        }
    }

}
