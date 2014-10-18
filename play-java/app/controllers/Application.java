package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

import models.Record;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import sun.net.ftp.FtpClient;
import actions.CorsComposition;

import com.fasterxml.jackson.databind.JsonNode;

@CorsComposition.Cors
public class Application extends Controller {

	public static Result index() {

		try {
			Confing confing = Confing.getInstance();
		
			return ok(views.html.index.render(""));

		} catch (Exception e) {
			Logger.error("Error ", e);
			return internalServerError(
					" INTERNAL_SERVER_ERROR 500 "
							+ e.getMessage() + "</h3>").as("text/html");
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
					" INTERNAL_SERVER_ERROR 500 "
							+ e.getMessage() ).as("text/html");
		}

		return ok(recJson);
	}

	public static Result download(String file) {
		// TODO: problem with hebrew, not inmportant
		File fileToDownload = null;
		try {
			fileToDownload = getFilesToDownload(file);
			// if empty do nothing
			if (fileToDownload == null)
				return ok();

		} catch (Exception e) {
			Logger.error("Error " + e.getMessage(), e);
			return internalServerError(
					" INTERNAL_SERVER_ERROR 500 "
							+ e.getMessage() + "</h3>").as("text/html");
		}

		return ok(fileToDownload);
	}

	private static File getFilesToDownload(String file) throws Exception {
		File fileToDownload;
		String[] files = file.split(",");

		// TODO: check if csv
		if (files.length == 0) {
			return null;
		}
		if (files.length == 1) {
			fileToDownload = new File(RecordManager.getRecFileNames().get(
					files[0]));
		} else {
			fileToDownload = RecordManager.compressToZip(files);
		}
		return fileToDownload;
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
			File recordsFile = getFilesToDownload(files);
			if (recordsFile != null) {
				String fileName = recordsFile.getName();
				ftpClient.putFile(fileName, new FileInputStream(recordsFile));
			}

		} catch (ConnectException e) {
			Logger.error("Error ", e);
			return internalServerError(
					" INTERNAL_SERVER_ERROR 500 "
							+ e.getMessage() + "</h3>" + "can't connect to ftp server").as("text/html");
		} catch (Exception e) {
			Logger.error("Error ", e);
			return internalServerError(
					" INTERNAL_SERVER_ERROR 500 "
							+ e.getMessage() + "</h3>").as("text/html");
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

}
