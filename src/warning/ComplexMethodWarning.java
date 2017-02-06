package warning;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class ComplexMethodWarning extends Warning {
	private int cyclomaticComplexity;

	public ComplexMethodWarning(CompilationUnit unit, ASTNode node, String filename, int cyclomaticComplexity) {
		super(unit, node, filename);
		this.cyclomaticComplexity = cyclomaticComplexity;
	}

	@Override
	public String getMessage() {
		return "サイクロマチック数が大きい(" + cyclomaticComplexity + ") " + node.toString();
	}

}
