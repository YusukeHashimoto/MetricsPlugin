package warning;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import warning.Warning;

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
}
