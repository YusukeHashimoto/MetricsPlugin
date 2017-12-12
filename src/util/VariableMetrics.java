package util;

import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import codeanalyzer.MyVisitor;

public class VariableMetrics {
	private String varname;

	private int lifespan;
	
	public VariableMetrics(VariableDeclarationFragment var) {
		lifespan = (int)var.getProperty(MyVisitor.LIFE_SPAN);
		varname = var.getName().getIdentifier();
	}
}
