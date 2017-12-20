package log;

import java.io.*;
import java.nio.file.*;

import org.eclipse.core.resources.IProject;

import util.ProjectUtil;

public class OpeLog {
	private static OpeLog opeLog;
	private String dir;
	private static final String FILENAME = "opelog.json";
	
	private OpeLog() {
		dir = assureDirectory() + FILENAME;
	}
	
	public static OpeLog getInstance() {
		if(opeLog == null) {
			opeLog = new OpeLog();
		}
		return opeLog;
	}
	
	public void log(String message) {
		String line = DateUtil.currentTimeInString2() + " " + message;
		
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(dir, true)));
			pw.println(line);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String assureDirectory() {
		IProject project = ProjectUtil.currentProject();
		String pathToPackage = ProjectUtil.pathToPackage();
		pathToPackage = pathToPackage.substring(pathToPackage.indexOf("/src/") + 5).replaceAll("/", ".");
		pathToPackage = pathToPackage.substring(0, pathToPackage.length() - 1);

		String dir = project.getFolder("log").getLocationURI().getPath() + "/";
		if(dir.charAt(2) == ':') 
			dir = dir.substring(1);
		Path path = Paths.get(dir);
		if (Files.notExists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return dir;
	}
}
