package warning.ckmetrics;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import codeanalyzer.ASTUtil;
import warning.suggestion.*;

public class LCOMWarning extends CKMetricsWarning {
	private double value;

	public LCOMWarning(CompilationUnit unit, List<MethodDeclaration> methods, String filename, double value) {
		super(unit, methods.get(0), filename);
		this.value = value;
	}

	@Override
	public String getMessage() {
		return "LCOMが大きい(" + value + "): " + ASTUtil.definedClassOf(getNode()).getName().getFullyQualifiedName();
	}
	
	@Override
	public int getPriority() {
		return (int)(value * 20);
	}

	@Override
	public List<Suggestion> suggestions() {
		List<Suggestion> list = new ArrayList<>();
		list.add(new SplitClassSuggestion(this));
		list.add(new MoveMethodSuggestion(this));
		return list;
	}
}
