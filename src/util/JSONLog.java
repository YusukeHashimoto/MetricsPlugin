package util;

import java.util.Map;

import codeanalyzer.ClassInfo;

public class JSONLog {
	private static String log;

	public static void log(ClassInfo cls) {
		Map<String, Object> metricsMap = cls.getMetricsMap();
		// List<String> methodNames = cls.getMethodNames();

		for (String key : metricsMap.keySet()) {
			Object metrics = metricsMap.get(key);
			if (metrics instanceof Integer) {
				print(cls.getClassName());
				print(key + ": " + metrics);
			} else if (metrics instanceof Map<?, ?>) {
				Map<String, Integer> methodMetrics = (Map<String, Integer>) metrics;
				for (String methodName : methodMetrics.keySet()) {
					print(cls.getClassName() + "#" + methodName);
					print(key + ": " + methodMetrics.get(methodName));
				}
			}
		}
	}

	private static void print(String str) {
		// System.err.println(str);
	}
}
