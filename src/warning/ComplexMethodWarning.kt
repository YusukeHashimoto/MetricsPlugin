package warning

import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.CompilationUnit
import util.Log

class ComplexMethodWarning(unit: CompilationUnit?, node: ASTNode?, filename: String?, cyclomaticComplexity: Int) : Warning(unit, node, filename) {
	private val cyclomaticComplexity: Int = cyclomaticComplexity
	
	override fun getMessage(): String {
		return "サイクロマチック数が大きい" + cyclomaticComplexity + node.toString()
	}
	
	override fun suggestions(): List<Suggestion> {
		return arrayListOf(Suggestion(Suggestion.EXTRACT_CONDITIONS_AS_METHOD), Suggestion(Suggestion.SPLIT_METHOD))
	}
}