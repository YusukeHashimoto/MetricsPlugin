package warning;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import codeanalyzer.MyVisitor;
import metricsplugin.views.metricstreeview.MetricsCategory;
import metricsplugin.views.metricstreeview.Node;
import warning.suggestion.Suggestion;

public abstract class Warning implements Node<MetricsCategory, Suggestion>, Comparable<Warning> {
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
		this.line = (node == null) ? 0 : (int) node.getProperty(MyVisitor.DECLARED_LINE);
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

	public abstract List<Suggestion> suggestions();

	@Override
	public String getLabel() {
		return getMessage();
	}
	
	public abstract int getPriority();
	
	@Override
	public int compareTo(Warning other) {
		return getPriority() - other.getPriority();
	}
}
