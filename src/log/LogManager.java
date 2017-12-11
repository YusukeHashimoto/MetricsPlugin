package log;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;

import util.ClassMetrics;
import util.ProjectUtil;

public class LogManager {

	private String pathOfNewLog() {
		return assureDirectory() + "/" + DateUtil.currentTimeInString() + ".json";
	}

	public void write(List<ClassMetrics> cm) {
		String dir = assureDirectory();
		String path = pathOfNewLog();
		File file = new File(path);
		StringBuilder sb = new StringBuilder();
		for (ClassMetrics c : cm) {
			sb.append(c.toJson());
		}
		if (sb.toString().equals(latestLog(dir))) {
			System.err.println("no need to write log");
			return;
		}

		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			pw.println(sb.toString());
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String latestLog(String dir) {
		List<String> logs = Arrays.asList(new File(dir).list()).stream().filter(s -> s.contains(".json"))
				.collect(Collectors.toList());
		if (logs.isEmpty())
			return null;
		String filename = dir + "/" + logs.get(logs.size() - 1);
		try {
			return Files.lines(Paths.get(filename)).collect(Collectors.joining("\n"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String assureDirectory() {
		IProject project = ProjectUtil.currentProject();
		String pathToPackage = ProjectUtil.pathToPackage();
		pathToPackage = pathToPackage.substring(pathToPackage.indexOf("/src/") + 5).replaceAll("/", ".");
		pathToPackage = pathToPackage.substring(0, pathToPackage.length() - 1);

		String dir = project.getFolder("log").getLocationURI().getPath() + "/" + pathToPackage;
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
