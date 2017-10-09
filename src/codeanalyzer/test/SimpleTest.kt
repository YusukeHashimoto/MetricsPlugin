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
		assertEquals(infoList.size, 3)
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
	
	@Test
	fun testDIT() {
		for(ci in infoList) {
			assertEquals(DITMap[ci.className], ci.depthOfInheritanceTree(infoList))
		}
	}
	
	@Test
	fun testNOC() {
		for(ci in infoList) {
			assertEquals(NOCMap[ci.className], ci.numberOfChildren(infoList))
		}
	}

	companion object {
		private lateinit var infoList: List<ClassInfo>
		private val WMCMap = mapOf("Animal" to 5, "Food" to 0, "Dog" to 0)
		private val CBOMap = mapOf("Animal" to 1, "Food" to 0, "Dog" to 0)
		private val DITMap = mapOf("Animal" to 2, "Food" to 2, "Dog" to 3)
		private val NOCMap = mapOf("Animal" to 1, "Food" to 0, "Dog" to 0)

		@BeforeClass @JvmStatic
		fun init() {
			val analyzer = CodeAnalyzer()
			analyzer.run("src/codeanalyzer/test/")
			infoList = analyzer.getClassInfo()
		}
	}
}