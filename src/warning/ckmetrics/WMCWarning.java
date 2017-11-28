package warning.ckmetrics;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class WMCWarning extends CKMetricsWarning {

	public WMCWarning(CompilationUnit unit, ASTNode node, String filename) {
		super(unit, node, filename);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
