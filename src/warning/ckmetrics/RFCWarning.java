package warning.ckmetrics;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import codeanalyzer.ASTUtil;
import warning.suggestion.SplitClassSuggestion;
import warning.suggestion.Suggestion;

public class RFCWarning extends CKMetricsWarning {
	private int value;

	public RFCWarning(CompilationUnit unit, List<MethodDeclaration> methods, String filename, int value) {
		super(unit, methods.get(0), filename);
		this.value = value;
	}

	@Override
	public String getMessage() {
		return "RFCが大きい(" + value + "): " + ASTUtil.definedClassOf(getNode()).getName().getFullyQualifiedName();
	}
	
	@Override
	public int getPriority() {
		return value / 2;
	}

	@Override
	public List<Suggestion> suggestions() {
		List<Suggestion> list = new ArrayList<>();
		list.add(new SplitClassSuggestion(this));
		return list;
	}
}
