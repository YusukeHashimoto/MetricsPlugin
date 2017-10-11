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
		assertEquals(infoMap.size, 5)
	}

	@Test
	fun testWMC() {
		infoMap.forEach {
			assertEquals(it.value.weightedMethodsPerClass(), WMCMap[it.value.className])
		}
	}

	@Ignore
	@Test
	fun testRFC() {

	}

	@Ignore
	@Test
	fun testCBO() {

	}
	
	@Test
	fun testDIT() {
		infoMap.forEach {
			assertEquals(DITMap[it.value.className], it.value.depthOfInheritanceTree(infoMap))
		}
	}
	
	
	@Test
	fun testNOC() {
		infoMap.forEach {
			assertEquals(NOCMap[it.value.className], it.value.numberOfChildren(infoMap))
		}
	}

	companion object {
		private lateinit var infoMap: Map<String, ClassInfo>
		private val WMCMap = mapOf("Animal" to 5, "Food" to 0, "Dog" to 0, "Cat" to 0, "ToyPoodle" to 0)
		private val CBOMap = mapOf("Animal" to 1, "Food" to 0, "Dog" to 0, "Cat" to 0, "ToyPoodle" to 0)
		private val DITMap = mapOf("Animal" to 2, "Food" to 2, "Dog" to 3, "Cat" to 3, "ToyPoodle" to 4)
		private val NOCMap = mapOf("Animal" to 3, "Food" to 0, "Dog" to 1, "Cat" to 0, "ToyPoodle" to 0)

		@BeforeClass @JvmStatic
		fun init() {
			val analyzer = CodeAnalyzer()
			analyzer.run("src/codeanalyzer/test/")
			infoMap = analyzer.getClassInfo()
		}
	}
}