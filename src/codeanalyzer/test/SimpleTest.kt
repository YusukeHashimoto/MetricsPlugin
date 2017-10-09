package codeanalyzer.test

import codeanalyzer.ClassInfo
import codeanalyzer.CodeAnalyzer
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.BeforeClass
import org.junit.Ignore

class SimpleTest {
	@Test
	fun testNumberOfClass() {
		assertEquals(infoList.size, 2)
	}

	@Test
	fun testWMC() {
		for (ci in infoList) {
			if (ci.getClassName().contains("Test")) continue
			assertEquals(ci.weightedMethodsPerClass(), WMCMap.get(ci.getClassName()) as Int)
		}
	}

	@Ignore
	@Test
	fun testRFC() {
		for (ci in infoList) {
			System.out.println(ci.responsesForClass())
		}
	}

	@Ignore
	@Test
	fun testCBO() {
		for(ci in infoList) {
			if(ci.getClassName().contains("Test")) continue;
			assertEquals(ci.efficientCouplings(ClassInfo.COUPLING_LEVEL_CLASS).size, CBOMap.get(ci.getClassName()));
		}
	}

	companion object {
		private lateinit var infoList: List<ClassInfo>
		private val WMCMap = mapOf("Animal" to 5, "Food" to 0)
		private val CBOMap = mapOf("Animal" to 1, "Food" to 0)

		@BeforeClass @JvmStatic
		fun init() {
			val analyzer = CodeAnalyzer()
			analyzer.run("src/codeanalyzer/test/")
			infoList = analyzer.getClassInfo()
		}
	}
}