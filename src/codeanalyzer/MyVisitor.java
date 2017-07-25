package codeanalyzer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

public class MyVisitor extends ASTVisitor {
	private List<MethodDeclaration> methodList;
	private List<VariableDeclarationFragment> variableList;
	private List<Block> blockList;
	static final String LINE_COUNT = "line_count";
	static final String LIFE_SPAN = "life";
	public static final String DECLARED_LINE = "declared_line";
	static final String CYCLOMATIC_COMPLEXITY = "mccabe";
	static final String LOCAL_VARIABLE = "local";
	static final String DEFINITION_PLACE = "def_place";
	private MyParser parser;
	private int cyclomaticComplexity = 1;
	private boolean isAbstract = false;
	private String superClass = null;
	private List<MethodInvocation> methodInvocations = new ArrayList<MethodInvocation>();

	public List<MethodDeclaration> getMethodList() {
		return methodList;
	}

	public List<VariableDeclarationFragment> getVariableList() {
		return variableList;
	}

	public List<Block> getBlockList() {
		return blockList;
	}
	
	public boolean isAbstract() {
		return isAbstract;
	}
	
	public List<MethodInvocation> getMethodInvocations() {
		return methodInvocations;
	}
	
	public String getSuperClass() {
		return superClass;
	}

	MyVisitor(String code) {
		methodList = new ArrayList<>();
		variableList = new ArrayList<>();
		blockList = new ArrayList<>();
		parser = new MyParser(code);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		node.setProperty(LINE_COUNT, Integer.valueOf(countLines(node.getBody().toString())));
		node.setProperty(CYCLOMATIC_COMPLEXITY, 1);
		methodList.add(node);
		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		node.setProperty(LIFE_SPAN, parser.lifeSpanOf(node));
		node.setProperty(DEFINITION_PLACE, definitionPlace(node));
		variableList.add(node);
		return super.visit(node);
	}

	@Override
	public boolean visit(Block node) {
		blockList.add(node);
		return super.visit(node);
	}

	@Override
	public boolean visit(IfStatement node) {
		cyclomaticComplexity++;
		incrementCC(parentMethodOf(node));
		return super.visit(node);
	}

	@Override
	public boolean visit(SwitchCase node) {
		cyclomaticComplexity++;
		incrementCC(parentMethodOf(node));
		return super.visit(node);
	}

	@Override
	public boolean visit(WhileStatement node) {
		cyclomaticComplexity++;
		incrementCC(parentMethodOf(node));
		return super.visit(node);
	}

	@Override
	public boolean visit(ForStatement node) {
		cyclomaticComplexity++;
		incrementCC(parentMethodOf(node));
		return super.visit(node);

	}

	@Override
	public boolean visit(CatchClause node) {
		cyclomaticComplexity++;
		incrementCC(parentMethodOf(node));
		return super.visit(node);
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		cyclomaticComplexity++;
		incrementCC(parentMethodOf(node));
		return super.visit(node);
	}

	@Override
	public boolean visit(ConditionalExpression node) {
		cyclomaticComplexity++;
		incrementCC(parentMethodOf(node));
		return super.visit(node);
	}

	@Override
	public boolean visit(InfixExpression node) {
		InfixExpression.Operator operator = node.getOperator();
		if(operator == InfixExpression.Operator.CONDITIONAL_AND
				|| operator == InfixExpression.Operator.CONDITIONAL_OR) {
			cyclomaticComplexity++;
			incrementCC(parentMethodOf(node));
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(DoStatement node) {
		cyclomaticComplexity++;
		incrementCC(parentMethodOf(node));
		return super.visit(node);
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		isAbstract = (node.getModifiers() == 1024);
		try {
			if(node.getSuperclassType() != null) {
				superClass = node.getSuperclassType().toString();
			}
		} catch(UnsupportedOperationException e) {
			
		}
		//if(isAbstract) System.out.println("This is abstract class.");
		
		return super.visit(node);
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		methodInvocations.add(node);
		//System.out.println("\t" + node.toString());
		return super.visit(node);
	}

	int totalCyclomaticComplexity() {
		return cyclomaticComplexity;
	}

	private MethodDeclaration parentMethodOf(ASTNode node) {
		ASTNode parent = node.getParent();
		while(!(parent instanceof MethodDeclaration)) {
			parent = parent.getParent();
			if(parent == null) return null;
		}
		return (MethodDeclaration) parent;
	}

	private void incrementCC(MethodDeclaration node) {
		node.setProperty(CYCLOMATIC_COMPLEXITY, (Integer) node.getProperty(CYCLOMATIC_COMPLEXITY) + 1);
	}

	private static int countLines(String code) {
		int n = 0;
		for(char c : code.toCharArray()) {
			if(c == '\n') n++;
		}
		return n;
	}

	private ASTNode definitionPlace(ASTNode node) {
		ASTNode parent = node.getParent();
		if(parent == null) return node;
		while(!(parent instanceof MethodDeclaration) && !(parent instanceof CompilationUnit)) {
			parent = parent.getParent();
		}
		return parent;
	}
}
