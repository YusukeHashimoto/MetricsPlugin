package warning;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import sample03.MetricsCategory;
import warning.suggestion.SplitMethodSuggestion;
import warning.suggestion.Suggestion;

public class LargeMethodWarning extends Warning {
	private int line;

	public LargeMethodWarning(CompilationUnit unit, ASTNode node, String filename, int line) {
		super(unit, node, filename);
		this.line = line;
	}

	@Override
	public String getMessage() {
		return "メソッドの行数が長い(" + line + ") " + node.toString();
	}

	@Override
	public List<Suggestion> suggestions() {
		List<Suggestion> suggestions = new ArrayList<>();
		suggestions.add(new SplitMethodSuggestion(this));
		return suggestions;
	}

	@Override
	public MetricsCategory getParent() {
		return MetricsCategory.SIMPLE_METRICS;
	}
}
