package util;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

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
		varList = ((List<VariableDeclarationFragment>)(method.getProperty(MyVisitor.LOCAL_VARS))).stream().map(v -> new VariableMetrics(v)).collect(Collectors.toList());
	}
	
	void addLocalVar(VariableDeclarationFragment var) {
		
	}
}
