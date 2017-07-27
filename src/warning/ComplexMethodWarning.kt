package warning

import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.CompilationUnit
import util.Log

class ComplexMethodWarning(unit: CompilationUnit?, node: ASTNode?, filename: String?, cyclomaticComplexity: Int) : Warning(unit, node, filename) {
	private val cyclomaticComplexity: Int = cyclomaticComplexity
	
	override fun getMessage(): String {
		return "サイクロマチック数が大きい" + cyclomaticComplexity + node.toString()
	}
}