package warning.ckmetrics;

import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import codeanalyzer.ASTUtil;

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
}
