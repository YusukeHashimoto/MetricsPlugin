package codeanalyzer.test;

import org.junit.Assert.*;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.BeforeClass;
import codeanalyzer.ClassInfo;
import codeanalyzer.CodeAnalyzer;

public class SimpleTest {
	static List<ClassInfo> infoList;
	private static final Map<String, Integer> WMCMap = new HashMap<>();
	private static final Map<String, Integer> CBOMap = new HashMap<>();

	@BeforeClass
	public static void init() {
		CodeAnalyzer analyzer = new CodeAnalyzer();
		analyzer.run("src/codeanalyzer/test/");
		infoList = analyzer.getClassInfo();
		
		WMCMap.put("Animal", 5);
		WMCMap.put("Food", 0);
		
		CBOMap.put("Animal", 1);
		CBOMap.put("Food", 0);
	}
	
	@Test
	public void testNumberOfClass() {
		assertEquals(infoList.size(), 3);
	}
	
	@Test
	public void testWMC() {
		for(ClassInfo ci : infoList) {
			if(ci.getClassName().contains("Test")) continue;
			assertEquals(ci.weightedMethodsPerClass(), (int)WMCMap.get(ci.getClassName()));
		}
	}
	
	@Test
	public void testRFC() {
		
	}
	
	@Test
	public void testCBO() {
		for(ClassInfo ci : infoList) {
			if(ci.getClassName().contains("Test")) continue;
			assertEquals(ci.efficientCouplings(ClassInfo.COUPLING_LEVEL_CLASS).size(), (int)CBOMap.get(ci.getClassName()));
		}
	}
}