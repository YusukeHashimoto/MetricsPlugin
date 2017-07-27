package codeanalyzer;

import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import util.Log;

public class ClassInfo {
	private String fileName;
	private List<String> methodNames;
	private String superClass;
	private boolean isAbstractClass = false;
	private MyVisitor visitor;
	private List<MethodInvocation> methodInvocations;
	private Set<String> recievers = new HashSet<>();
	
	public ClassInfo(MyVisitor visitor, String filename) {
		this.visitor = visitor;
		this.fileName = filename;
		methodNames = visitor.getMethodList().stream().map(MethodDeclaration::toString).collect(Collectors.toList());
		superClass = visitor.getSuperClass();
		isAbstractClass = visitor.isAbstract();
		methodInvocations = visitor.getMethodInvocations();
		
		try {
			recievers = methodInvocations.stream().map(
					m -> m.resolveMethodBinding().getDeclaringClass().getErasure().getQualifiedName()).collect(Collectors.toSet());
		} catch(NullPointerException e) {
			Log.error("Cannot resolve IMethod Binding because of " + e.toString());
		}
		recievers.stream().forEach(r -> Log.info("Reciever: " + r));
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
	
}
