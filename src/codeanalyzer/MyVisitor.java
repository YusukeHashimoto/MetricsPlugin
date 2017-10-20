package codeanalyzer;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.*;

public class MyVisitor extends ASTVisitor {
	private List<MethodDeclaration> methodList;
	private List<VariableDeclarationFragment> variableList;
	private List<Block> blockList;
	static final String LINE_COUNT = "line_count";
	static final String LIFE_SPAN = "life";
	public static final String DECLARED_LINE = "declared_line";
	static final String CYCLOMATIC_COMPLEXITY = "mccabe";
	//static final String LOCAL_VARIABLE = "local";
	static final String DEFINITION_PLACE = "def_place";
	private MyParser parser;
	private int cyclomaticComplexity = 1;
	private boolean isAbstract = false;
	private String superClass = null;
	private List<MethodInvocation> methodInvocations = new ArrayList<MethodInvocation>();
	private String packagename;
	private String filename;
	private String className;
	private List<SingleVariableDeclaration> parameters = new ArrayList<>();
	private Map<VariableDeclarationFragment, Set<MethodDeclaration>> cohesionMap = new HashMap<>();
	private List<SimpleName> names = new ArrayList<>();

	public Map<VariableDeclarationFragment, Set<MethodDeclaration>> getCohesionMap() {
		return cohesionMap;
	}

	public List<SingleVariableDeclaration> getParameters() {
		return parameters;
	}

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
	
	MyVisitor(String code, String filename) {
		this(code);
		this.filename = filename;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		node.setProperty(LINE_COUNT, Integer.valueOf(countLines(node.getBody().toString())));
		node.setProperty(CYCLOMATIC_COMPLEXITY, 1);
		methodList.add(node);
		
		parameters.addAll(node.parameters());
		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		node.setProperty(LIFE_SPAN, parser.lifeSpanOf(node));
		node.setProperty(DEFINITION_PLACE, ASTUtil.definitionPlaceOf(node));
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
		incrementCC(ASTUtil.parentMethodOf(node));
		return super.visit(node);
	}

	@Override
	public boolean visit(SwitchCase node) {
		cyclomaticComplexity++;
		incrementCC(ASTUtil.parentMethodOf(node));
		return super.visit(node);
	}

	@Override
	public boolean visit(WhileStatement node) {
		cyclomaticComplexity++;
		incrementCC(ASTUtil.parentMethodOf(node));
		return super.visit(node);
	}

	@Override
	public boolean visit(ForStatement node) {
		cyclomaticComplexity++;
		incrementCC(ASTUtil.parentMethodOf(node));
		return super.visit(node);

	}

	@Override
	public boolean visit(CatchClause node) {
		cyclomaticComplexity++;
		incrementCC(ASTUtil.parentMethodOf(node));
		return super.visit(node);
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		cyclomaticComplexity++;
		incrementCC(ASTUtil.parentMethodOf(node));
		return super.visit(node);
	}

	@Override
	public boolean visit(ConditionalExpression node) {
		cyclomaticComplexity++;
		incrementCC(ASTUtil.parentMethodOf(node));
		return super.visit(node);
	}

	@Override
	public boolean visit(InfixExpression node) {
		InfixExpression.Operator operator = node.getOperator();
		if(operator == InfixExpression.Operator.CONDITIONAL_AND
				|| operator == InfixExpression.Operator.CONDITIONAL_OR) {
			cyclomaticComplexity++;
			incrementCC(ASTUtil.parentMethodOf(node));
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(DoStatement node) {
		cyclomaticComplexity++;
		incrementCC(ASTUtil.parentMethodOf(node));
		return super.visit(node);
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		className = node.getName().toString();
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
	
	@Override
	public boolean visit(PackageDeclaration node) {
		packagename = node.getName().toString();
		return super.visit(node);
	}
	
	@Override
	public boolean visit(SimpleName node) {
		names.add(node);
		/*
		if(ASTUtil.parentMethodOf(node) == null) return super.visit(node);
		
		//List<VariableDeclarationFragment> 
		fieldVars = variableList.stream().filter(v -> !(v.getProperty(MyVisitor.DEFINITION_PLACE) instanceof MethodDeclaration)).collect(Collectors.toList());
		List<VariableDeclarationFragment> localVars = new ArrayList<VariableDeclarationFragment>(variableList); // contains parameters
		localVars.removeAll(fieldVars);
		
		//Map<String, VariableDeclarationFragment>
		fieldVarMap = variableList.stream().filter(v -> ASTUtil.isField(v) && ASTUtil.definedClassOf(node).getName().toString().equals(className))
				.collect(Collectors.toMap(VariableDeclarationFragment::toString, v -> v));
		
		for(VariableDeclarationFragment var : fieldVars) {
			if(!fieldVarMap.containsValue(var)) {
				System.out.println(var + " is not a field");
			}
			if(!ASTUtil.isField(var)) {
				System.out.println(var + " is not a field");
			}
			if(var.getName().getFullyQualifiedName().equals(node.getFullyQualifiedName()) && ASTUtil.definedClassOf(var).equals(ASTUtil.definedClassOf(node))) {
				//if(fieldVarMap.containsKey(var.toString())) continue;
				if(cohesionMap.get(var) == null) {
					cohesionMap.put(var, new HashSet<MethodDeclaration>());
				}
				cohesionMap.get(var).add(ASTUtil.parentMethodOf(node));
			}
		}*/
		return super.visit(node);
	}
	
	int totalCyclomaticComplexity() {
		return cyclomaticComplexity;
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
	
	private Set<VariableDeclarationFragment> fieldVars;

	public ClassInfo newClassInfo() {
		generateCohesionMap();
		//Set<VariableDeclarationFragment> 
		//fieldVars = variableList.stream().filter(v -> !(v.getProperty(MyVisitor.DEFINITION_PLACE) instanceof MethodDeclaration)).collect(Collectors.toSet());
		
		return new ClassInfo.Builder(filename, packagename).isAbstract(isAbstract)
				.methodInvocations(methodInvocations).methodDeclarations(methodList).superClass(superClass)
				.className(className).varList(variableList).cohesionMap(cohesionMap).fieldVars(fieldVars).build();
	}
	
	private void generateCohesionMap() {
		Map<String, VariableDeclarationFragment> fieldVars = variableList.stream().filter(v -> ASTUtil.isField(v) && ASTUtil.definedClassOf(v).getName().toString().equals(className))
				.collect(Collectors.toMap(VariableDeclarationFragment::toString, v -> v));
		List<VariableDeclarationFragment> nonFieldVars = variableList.stream().filter(v -> !fieldVars.containsValue(v)).collect(Collectors.toList());

		for(SimpleName node : names) {
			if(ASTUtil.parentMethodOf(node) == null) continue;
			
			flag: for(Entry<String, VariableDeclarationFragment> e : fieldVars.entrySet()) {
				VariableDeclarationFragment var = (VariableDeclarationFragment) e.getValue();
				if(var.getName().getFullyQualifiedName().equals(node.getFullyQualifiedName()) && ASTUtil.definedClassOf(var).equals(ASTUtil.definedClassOf(node))) {
					for(VariableDeclarationFragment nf : nonFieldVars) {
						if(nf.getName().toString().equals(var.getName().toString())) {
							//System.out.println("dublicaple name " + nf.getName().toString());
							if(ASTUtil.parentMethodOf(node).equals(ASTUtil.parentMethodOf(nf)) && !ASTUtil.parentMethodOf(node).toString().contains("this." + var)) {
								//System.out.println("The name " + var.getName() + " is not a field in " + ASTUtil.parentMethodOf(node));
								continue flag;
							}
						}
					}
					if(cohesionMap.get(var) == null) {
						cohesionMap.put(var, new HashSet<MethodDeclaration>());
					}
					cohesionMap.get(var).add(ASTUtil.parentMethodOf(node));
				
				}
			}
		}
		this.fieldVars = new HashSet<VariableDeclarationFragment>(fieldVars.values());
		System.out.println();
	}
}
