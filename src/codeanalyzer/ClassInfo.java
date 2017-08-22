package codeanalyzer;

import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import util.Log;
import util.ProjectUtil;

public class ClassInfo {
	public static final int COUPLING_LEVEL_CLASS = 0;
	public static final int COUPLING_LEVEL_PACKAGE = 1;
	
	private String fileName;
	private List<String> methodNames;
	private List<MethodDeclaration> methodDeclarations;
	private String superClass;
	private boolean isAbstractClass = false;
	private List<MethodInvocation> methodInvocations;
	private Set<String> recievers;
	private String packageName;
	private String className;
	
	public ClassInfo(MyVisitor visitor, String filename) {
		this.fileName = filename;
		this.methodDeclarations = visitor.getMethodList();
		methodNames = visitor.getMethodList().stream().map(MethodDeclaration::toString).collect(Collectors.toList());
		superClass = visitor.getSuperClass();
		isAbstractClass = visitor.isAbstract();
		methodInvocations = visitor.getMethodInvocations();
		
		init();
	}
	
	private void init() {
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

	public String getSuperClass() {
		return superClass;
	}

	public boolean isAbstractClass() {
		return isAbstractClass;
	}
	
	public String getPackageName() {
		return packageName;
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
	
	static class Builder {
		private String filename;
		private String packagename;
		private List<MethodDeclaration> methodDeclarations;
		private List<MethodInvocation> methodInvocations;
		private boolean isAbstract;
		private String superClass;
		private String className;
		
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
		this.superClass = builder.superClass;
		this.className = builder.className;
		
		init();
	}
}
