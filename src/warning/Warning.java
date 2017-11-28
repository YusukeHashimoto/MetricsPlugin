package warning;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import codeanalyzer.MyVisitor;
import sample03.MetricsCategory;
import sample03.Node;
import warning.suggestion.Suggestion;

public abstract class Warning implements Node<MetricsCategory, Suggestion> {
	private String filename;
	protected ASTNode node;
	// protected String message;
	private int line;
	CompilationUnit unit;

	public static final int LARGE_METHOD = 0;
	public static final int COMPLEX_METHOD = 1;
	public static final int LONG_LIFE_SPAN = 2;

	public Warning(CompilationUnit unit, ASTNode node, String filename) {
		this.filename = filename;
		this.node = node;
		if (node != null) {
			this.line = (int) node.getProperty(MyVisitor.DECLARED_LINE);
		}
		this.unit = unit;
	}

	@Override
	public List<Suggestion> getChildren() {
		return suggestions();
	}

	@Override
	public boolean hasChildren() {
		return !suggestions().isEmpty();
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

	public CompilationUnit getCompilationUnit() {
		return unit;
	}

	public List<Suggestion> suggestions() {
		return new ArrayList<>();
	}

	@Override
	public String getLabel() {
		return getMessage();
	}
}
