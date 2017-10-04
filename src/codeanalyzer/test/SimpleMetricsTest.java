package codeanalyzer.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import codeanalyzer.ClassInfo;
import codeanalyzer.CodeAnalyzer;

public class SimpleMetricsTest {

	@Test
	public void test() {
		//fail("Not yet implemented");
	}

	@Test
	public void testMethodAnalyzer() {
		CodeAnalyzer analyzer = new CodeAnalyzer();
		analyzer.run("src/codeanalyzer/test/");
		List<ClassInfo> infoList = analyzer.getClassInfo();
		
		assertEquals(infoList.size(), 2);
		for(ClassInfo ci : infoList) {
			/*
			if(ci.getFileName().equals("Animal.java")) {
				assertEquals(ci.weightedMethodsPerClass(), 3);
			}
			*/
			assertEquals(ci.getPackageName(), "codeanalyzer.test");
		}
	}
}
