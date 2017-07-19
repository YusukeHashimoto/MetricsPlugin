package warning;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import codeanalyzer.MyVisitor;

public abstract class Warning {
	private String filename;
	protected ASTNode node;
	//protected String message;
	private int line;
	
	public static final int LARGE_METHOD = 0;
	public static final int COMPLEX_METHOD = 1;
	public static final int LONG_LIFE_SPAN = 2;
	
	public Warning(CompilationUnit unit, ASTNode node, String filename) {
		this.filename = filename;
		this.node = node;
		this.line = (int)node.getProperty(MyVisitor.DECLARED_LINE);
	}

	public ASTNode getNode() {
		return node;
	}

	public int getLine() {
		return line;
	}

	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public abstract String getMessage();
}
