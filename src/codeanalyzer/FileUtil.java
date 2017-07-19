package codeanalyzer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtil {

	static String readSourceCode(String path) {
		try {
			return Files.lines(Paths.get(path)).collect(Collectors.joining("\n"));
		} catch(IOException e) {
			System.err.println(e);
			return null;
		}
	}
	
	static List<String> getSourceCodeList(String pathToPackage) {
		return Arrays.asList((new File(pathToPackage).list())).stream().filter(s -> s.contains(".java")).collect(Collectors.toList());
	}
}
