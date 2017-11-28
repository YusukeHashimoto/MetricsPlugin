package warning

import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.CompilationUnit
import util.Log
import warning.suggestion.*
import sample03.MetricsCategory

class ComplexMethodWarning(unit: CompilationUnit?, node: ASTNode?, filename: String?, cyclomaticComplexity: Int) : Warning(unit, node, filename) {
	private val cyclomaticComplexity: Int = cyclomaticComplexity
	
	override fun getMessage(): String {
		return "サイクロマチック数が大きい" + cyclomaticComplexity + node.toString()
	}
	
	override fun suggestions(): List<Suggestion> {
		return arrayListOf(ExtractConditionsSuggestion(this), SplitMethodSuggestion(this))
	}
	
	override fun getParent(): MetricsCategory {
		return MetricsCategory.SIMPLE_METRICS;
	}
	
	override fun hasChildren(): Boolean {
		return !suggestions().isEmpty();
	}
	
	override fun getChildren(): List<Suggestion> {
		return suggestions();
	}
}