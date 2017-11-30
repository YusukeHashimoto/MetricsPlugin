package warning.ckmetrics;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class WMCWarning extends CKMetricsWarning {
	private int value;

	public WMCWarning(CompilationUnit unit, ASTNode node, String filename, int value) {
		super(unit, node, filename);
		this.value = value;
	}

	@Override
	public String getMessage() {
		return "WMCが大きい(" + value + "): " + getFilename();
	}

}
