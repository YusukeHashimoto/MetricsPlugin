package warning;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

import codeanalyzer.ASTUtil;
import metricsplugin.views.metricstreeview.MetricsCategory;
import warning.suggestion.*;

public class LargeMethodWarning extends Warning {
	private int line;

	public LargeMethodWarning(CompilationUnit unit, ASTNode node, String filename, int line) {
		super(unit, node, filename);
		this.line = line;
	}

	@Override
	public String getMessage() {
		return "メソッドの行数が大きい(" + line + ") " + ASTUtil.methodNameOf((MethodDeclaration)node);
	}

	@Override
	public List<Suggestion> suggestions() {
		List<Suggestion> suggestions = new ArrayList<>();
		suggestions.add(new SplitMethodSuggestion(this));
		suggestions.add(new ExtractConditionsSuggestion(this));
		return suggestions;
	}

	@Override
	public MetricsCategory getParent() {
		return MetricsCategory.SIMPLE_METRICS;
	}
	
	@Override
	public int getPriority() {
		return line * 2;
	}
}
