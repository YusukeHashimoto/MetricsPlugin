package codeanalyzer.test;

import org.junit.Assert.*;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import codeanalyzer.ClassInfo;
import codeanalyzer.CodeAnalyzer;

public class SimpleTest {
	static CodeAnalyzer analyzer = new CodeAnalyzer();

	@BeforeClass
	public static void in() {
		analyzer.run("src/codeanalyzer/test/");
	}
	
	@Test
	public void testMethodAnalyzer() {
		List<ClassInfo> infoList = analyzer.getClassInfo();
		assertEquals(infoList.size(), 3);
		for(ClassInfo ci : infoList) {
			assertEquals(ci.getPackageName(), "codeanalyzer.test");

			int wmc = 0;
			int rfc = 0;
			switch(ci.getClassName()) {
			case "Animal":
				wmc = 5;
				rfc = 1;
				break;
			case "Food":
				wmc = 0;
				rfc = 0;
				break;
			}
			if(ci.getClassName().contains("Test")) continue;
			assertEquals(wmc, ci.weightedMethodsPerClass());
			assertEquals(rfc, ci.responsesForClass());
		}
	}
}