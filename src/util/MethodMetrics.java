package util;

import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import codeanalyzer.ASTUtil;
import codeanalyzer.MyVisitor;

public class MethodMetrics {
	private String methodname;
	private List<VariableMetrics> varList;

	private int cc;
	private int loc;
	
	public MethodMetrics(MethodDeclaration method) {
		methodname = ASTUtil.methodNameOf(method);
		cc = (Integer)method.getProperty(MyVisitor.CYCLOMATIC_COMPLEXITY);
		loc = (Integer)method.getProperty(MyVisitor.LINE_COUNT);
	}
}
