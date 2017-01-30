package codeanalizer;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import codeanalizer.Warning;

public class LargeMethodWarning extends Warning {
	private int line;

	public LargeMethodWarning(CompilationUnit unit, ASTNode node, int line) {
		super(unit, node);
		this.line = line;
	}
	
	@Override
	public String getMessage() {
		return "メソッドの行数が長い(" + line + ") " + node.toString();
	}
}
