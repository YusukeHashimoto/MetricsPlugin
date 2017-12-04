package warning.ckmetrics;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import metricsplugin.views.metricstreeview.MetricsCategory;
import warning.Warning;

public abstract class CKMetricsWarning extends Warning {

	public CKMetricsWarning(CompilationUnit unit, ASTNode node, String filename) {
		super(unit, node, filename);
	}

	@Override
	public MetricsCategory getParent() {
		return MetricsCategory.CK_METRICS;
	}
}
