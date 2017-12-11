package log;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;

import util.ClassMetrics;

public class LogManager {
	private static final String DIRECTORY_OF_LOG = "/log/";
	private IProject project;
	private String logDir;

	public LogManager(IProject project) {
		this.project = project;
		logDir = project.getFolder("log").getLocationURI().getPath() + "/";
		Path p = Paths.get(logDir);

		if (Files.notExists(p)) {
			System.err.println("no exsits");
			try {
				Files.createDirectories(p);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private String pathOfNewLog() {
		// String path = project.getFolder("log").getLocationURI().getPath() +
		// "/";
		// path += DateUtil.currentTimeInString();
		// System.err.println(path);
		// return path;
		return logDir + DateUtil.currentTimeInString() + ".json";
	}

	public void write(List<ClassMetrics> cm) {
		String path = pathOfNewLog();
		File file = new File(path);
		StringBuilder sb = new StringBuilder();
		for (ClassMetrics c : cm) {
			sb.append(c.toJson());
		}
		if (sb.toString().equals(latestLog())) {
			System.err.println("no need to write log");
			return;
		}

		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			// pw.println(c.toJson());
			pw.println(sb.toString());
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}

	private String latestLog() {
		List<String> logs = Arrays.asList(new File(logDir).list()).stream().filter(s -> s.contains(".json"))
				.collect(Collectors.toList());
		String filename = logDir + logs.get(logs.size() - 1);
		try {
			return Files.lines(Paths.get(filename)).collect(Collectors.joining("\n"));
		} catch (IOException e) {
			e.printStackTrace();
			return "error";
		}
	}
}
