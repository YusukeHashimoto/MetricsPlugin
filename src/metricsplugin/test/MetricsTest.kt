package metricsplugin.test

import codeanalyzer.ClassInfo
import codeanalyzer.CodeAnalyzer
import org.junit.BeforeClass
import org.junit.Test
import org.junit.Assert.assertEquals

class MetricsTest {
	/*
	@Test
	fun test() {
		//val workspace = ResourcesPlugin.getWorkspace()
		//val root = workspace!!.getRoot()
		//val projects = root!!.getProjects()
		val ca = CodeAnalyzer()
		ca.analyzeCodes(null, "C:/Users/Hashimoto/runtime-EclipseApplication/sep3asm/src/lang/")
		val animal = ca.getClassInfo().get("Compiler")
		System.out.println(animal!!.efficientCouplings(ClassInfo.COUPLING_LEVEL_CLASS))
	}*/

	@Test
	fun testCBO() {
		infoMap.forEach {
			System.out.println(it.value.efficientCouplings(ClassInfo.COUPLING_LEVEL_CLASS))
			assertEquals(CBOMap[it.value.className], it.value.efficientCouplings(ClassInfo.COUPLING_LEVEL_CLASS).size)
		}
	}

	companion object {
		private lateinit var infoMap: Map<String, ClassInfo>
		//private val WMCMap = mapOf("Animal" to 5, "Food" to 0, "Dog" to 0, "Cat" to 0, "ToyPoodle" to 0)
		private val CBOMap = mapOf("Inst2" to 10, "Line" to 4, "Operand" to 4, "Program" to 12)
		//private val DITMap = mapOf("Animal" to 2, "Food" to 2, "Dog" to 3, "Cat" to 3, "ToyPoodle" to 4)
		//private val NOCMap = mapOf("Animal" to 3, "Food" to 0, "Dog" to 1, "Cat" to 0, "ToyPoodle" to 0)

		@BeforeClass @JvmStatic
		fun init() {
			val analyzer = CodeAnalyzer()
			//analyzer.run("src/codeanalyzer/test/")
			analyzer.analyzeCodes(null, "C:/Users/Hashimoto/runtime-EclipseApplication/sep3asm/src/lang/sep3asm/parse/")
			infoMap = analyzer.getClassInfo()
		}
	}
}