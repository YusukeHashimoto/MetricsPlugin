package metricsplugin.test

import codeanalyzer.ClassInfo
import codeanalyzer.CodeAnalyzer
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.eclipse.core.resources.*;

class MetricsTest {

	@Test
	fun testCBO() {
		infoMap.forEach {
			System.out.println(it.value.efficientCouplings(ClassInfo.COUPLING_LEVEL_CLASS))
			assertEquals(CBOMap[it.value.className], it.value.efficientCouplings(ClassInfo.COUPLING_LEVEL_CLASS).size)
		}
	}
	
	@Ignore
	@Test
	fun testRFC() {
		
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
	
	@Test
	fun testWMC() {
		infoMap.forEach {
			assertEquals(WMCMap[it.value.className], it.value.weightedMethodsPerClass())
		}
	}

	companion object {
		private lateinit var infoMap: Map<String, ClassInfo>
		private val WMCMap = mapOf("Inst2" to 12, "Line" to 5, "Operand" to 9, "Program" to 8)
		private val CBOMap = mapOf("Inst2" to 10, "Line" to 4, "Operand" to 4, "Program" to 12)
		private val DITMap = mapOf("Inst2" to 3, "Line" to 3, "Operand" to 3, "Program" to 3)
		private val NOCMap = mapOf("Inst2" to 0, "Line" to 0, "Operand" to 0, "Program" to 0)

		@BeforeClass @JvmStatic
		fun init() {
			val analyzer = CodeAnalyzer()
			//analyzer.run("src/codeanalyzer/test/")
			//analyzer.analyzeCodes(null, "C:/Users/Hashimoto/runtime-EclipseApplication/sep3asm/src/lang/sep3asm/parse/")
			val workspace = ResourcesPlugin.getWorkspace();
			val root = workspace.root;
			val projects = root.projects;
			analyzer.analyzeCodes(null, "/Users/yusuke/Downloads/sep3asm/src/lang/sep3asm/parse/", projects[1])
			infoMap = analyzer.getClassInfo()
		}
	}
}