package warning

import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.CompilationUnit
import warning.*
import warning.suggestion.*
import metricsplugin.views.metricstreeview.MetricsCategory;

public class LargeScopeWarning(unit: CompilationUnit?, node: ASTNode?, filename: String?, lifeSpan: Int?) : Warning(unit, node, filename) {
	val lifeSpan = lifeSpan

	override fun getMessage(): String {
		return "変数の寿命が長い(" + lifeSpan + ")" + node.toString()
	}
	
	override fun suggestions(): List<Suggestion> {
		return arrayListOf(InliningVariableSuggestion(this))
	}
	
	override fun getParent(): MetricsCategory {
		return MetricsCategory.SIMPLE_METRICS;
	}
}