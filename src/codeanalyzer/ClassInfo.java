package codeanalyzer;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.*;

import util.Log;
import util.ProjectUtil;

public class ClassInfo {
	public static final int COUPLING_LEVEL_CLASS = 0;
	public static final int COUPLING_LEVEL_PACKAGE = 1;
	
	private String fileName;
	private List<String> methodNames;
	private List<MethodDeclaration> methodDeclarations;
	private String superclassName;
	private boolean isAbstractClass = false;
	private List<MethodInvocation> methodInvocations;
	private Set<String> recievers;
	private String packageName;
	private String className;
	private List<String> parameters;
	private List<VariableDeclarationFragment> varDecls;
	private Set<VariableDeclarationFragment> fieldVars;
	private Map<VariableDeclarationFragment, Set<MethodDeclaration>> cohesionMap;
	private Set<VariableDeclarationFragment> localVars;
	
	private ClassInfo superclass;
	private Set<ClassInfo> subclasses = new HashSet<>();
	
	public String getClassName() {
		return className;
	}

	public ClassInfo(MyVisitor visitor, String filename) {
		this.fileName = filename;
		this.methodDeclarations = visitor.getMethodList();
		methodNames = visitor.getMethodList().stream().map(MethodDeclaration::toString).collect(Collectors.toList());
		superclassName = visitor.getSuperClass();
		isAbstractClass = visitor.isAbstract();
		methodInvocations = visitor.getMethodInvocations();
		parameters = visitor.getParameters().stream().map(p -> p.getName().toString()).collect(Collectors.toList());
		varDecls = visitor.getVariableList();
		fieldVars = varDecls.stream().filter(v -> !(v.getProperty(MyVisitor.DEFINITION_PLACE) instanceof MethodDeclaration)).collect(Collectors.toSet());
		cohesionMap = visitor.getCohesionMap();
		
		init();
	}
	
	private void init() {
		extractRecievers();
		localVars = new HashSet<>(varDecls);
		localVars.removeAll(fieldVars);
	}
	
	private void extractRecievers() {
		if(methodInvocations.isEmpty()) return;
		
		if(methodInvocations.get(0).resolveMethodBinding() == null) {
			Log.verbose("Cannot resolve IMethod Binding");
			return;
		}
		
		recievers = methodInvocations.stream().map(
				m -> m.resolveMethodBinding().getDeclaringClass().getErasure().getQualifiedName()).collect(Collectors.toSet());
		recievers.stream().forEach(r -> Log.info("Reciever :" + r));
	}
	
	public String getFileName() {
		return fileName;
	}

	public List<String> getMethodNames() {
		return methodNames;
	}

	public String getSuperclassName() {
		return superclassName;
	}

	public boolean isAbstractClass() {
		return isAbstractClass;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public ClassInfo getSuperclass() {
		return superclass;
	}

	public void setSuperclass(ClassInfo superclass) {
		this.superclass = superclass;
	}

	public Set<ClassInfo> getSubclasses() {
		return subclasses;
	}
	
	public void addSubclass(ClassInfo subclass) {
		subclasses.add(subclass);
	}

	public Set<String> efficientCouplings(int couplingLevel) {
		if(recievers != null && !recievers.isEmpty()) {
			if(couplingLevel == COUPLING_LEVEL_CLASS) {
				return recievers.stream().filter(p -> !p.equals(packageName + '.' + className)).collect(Collectors.toSet());
			} else {
				return recievers.stream().filter(p -> !ProjectUtil.packageOf(p).equals(packageName)).collect(Collectors.toSet());
			}
		} else {
			return new HashSet<String>();
		}
	}
	
	public Set<Response> responsesForClass() {
		final Set<Response> s = methodInvocations.stream().map(mi -> new Response(mi.resolveMethodBinding().getDeclaringClass().getName(), mi.getName().toString()))
		.filter(r -> !r.getClassname().equals(className)).collect(Collectors.toSet());
		methodNames.stream().forEach(m -> s.add(new Response(className, m)));
		return s;
	}
	
	public int weightedMethodsPerClass() {
		int wmc = 0;
		for(MethodDeclaration m : methodDeclarations) {
			wmc += (int)(m.getProperty(MyVisitor.CYCLOMATIC_COMPLEXITY));
		}
		return wmc;
	}
	
	public int depthOfInheritanceTree(Map<String, ClassInfo> classList) {
		if(superclassName == null || superclassName.equals("Object") || superclassName.equalsIgnoreCase("")) return 2;

		for(Entry<String, ClassInfo> e : classList.entrySet()) {
			ClassInfo ci = e.getValue();
			if(ci.getClassName().equals(superclassName)) return ci.depthOfInheritanceTree(classList) + 1;
		}
		return 3;
	}
		
	public int numberOfChildren(Map<String, ClassInfo> classList) {
		int noc = subclasses.size();
		for(ClassInfo subclass : subclasses) {
			noc += subclass.numberOfChildren(classList);
		}
		return noc;
	}
	
	public int lackOfCohesionInMethods() {
		System.out.println("\t class " + className);
		
		for(VariableDeclarationFragment var : fieldVars) {
			Set<MethodDeclaration> set = cohesionMap.get(var);
			if(set != null)
				System.out.println(var.getName().getFullyQualifiedName() + " is accessed in " + cohesionMap.get(var).size() + " methods");
			else
				System.err.println("Cannot calcurate LCOM of " + var.getName().getIdentifier());
		}
		return 0;
	}
	
	@Override
	public String toString() {
		return className;
	}
	
	static class Builder {
		private String filename;
		private String packagename;
		private List<MethodDeclaration> methodDeclarations;
		private List<MethodInvocation> methodInvocations;
		private boolean isAbstract;
		private String superClass;
		private String className;
		private List<VariableDeclarationFragment> varList;
		private Map<VariableDeclarationFragment, Set<MethodDeclaration>> cohesionMap;
		private Set<VariableDeclarationFragment> fieldVars;
		
		public Builder(String filename, String packagename) {
			this.filename = filename;
			this.packagename = packagename;
		}
		
		public Builder methodDeclarations(List<MethodDeclaration> methodDeclarations) {
			this.methodDeclarations = methodDeclarations;
			return this;
		}
		
		public Builder methodInvocations(List<MethodInvocation> methodInvocations) {
			this.methodInvocations = methodInvocations;
			return this;
		}
		
		public Builder isAbstract(boolean isAbstract) {
			this.isAbstract = isAbstract;
			return this;
		}

		public Builder superClass(String superClass) {
			this.superClass = superClass;
			return this;
		}
		
		public Builder className(String className) {
			this.className = className;
			return this;
		}
		
		public Builder varList(List<VariableDeclarationFragment> varList) {
			this.varList = varList;
			return this;
		}
		
		public Builder cohesionMap(Map<VariableDeclarationFragment, Set<MethodDeclaration>> cohesionMap) {
			this.cohesionMap = cohesionMap;
			return this;
		}
		
		public Builder fieldVars(Set<VariableDeclarationFragment> fieldVars) {
			this.fieldVars = fieldVars;
			return this;
		}
		
		public ClassInfo build() {
			return new ClassInfo(this);
		}
	}
	
	private ClassInfo(Builder builder) {
		this.fileName = builder.filename;
		this.packageName = builder.packagename;
		this.methodInvocations = builder.methodInvocations;
		this.methodDeclarations = builder.methodDeclarations;
		this.isAbstractClass = builder.isAbstract;
		this.superclassName = builder.superClass;
		this.className = builder.className;
		this.varDecls = builder.varList;
		this.cohesionMap = builder.cohesionMap;
		this.fieldVars = builder.fieldVars;
		
		init();
	}
}
