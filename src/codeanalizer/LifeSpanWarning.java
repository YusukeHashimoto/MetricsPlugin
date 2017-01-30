package codeanalizer;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class LifeSpanWarning extends Warning {
	private int lifeSpan;

	public LifeSpanWarning(CompilationUnit unit, ASTNode node, int lifeSpan) {
		super(unit, node);
		this.lifeSpan = lifeSpan;
	}

	@Override
	public String getMessage() {
		return "変数の寿命が長い(" + lifeSpan + ")" + node.toString();
	}

}
