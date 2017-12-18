package warning

import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.CompilationUnit
import util.Log
import warning.suggestion.*
import metricsplugin.views.metricstreeview.MetricsCategory
import codeanalyzer.ASTUtil

class ComplexMethodWarning(unit: CompilationUnit?, node: ASTNode?, filename: String?, cyclomaticComplexity: Int) : Warning(unit, node, filename) {
	private val cyclomaticComplexity: Int = cyclomaticComplexity
	
	override fun getMessage(): String {
		return "サイクロマチック数が大きい(" + cyclomaticComplexity  + ") " + ASTUtil.methodNameOf(node);
	}
	
	override fun suggestions(): List<Suggestion> {
		return arrayListOf(ExtractConditionsSuggestion(this), SplitMethodSuggestion(this), UsePolymorphismSuggestion(this))
	}
	
	override fun getParent(): MetricsCategory {
		return MetricsCategory.SIMPLE_METRICS;
	}
	
	override fun getPriority() = cyclomaticComplexity
}