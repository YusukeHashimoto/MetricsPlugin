package codeanalyzer;

import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

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
			//methodInvocations.stream().map(m -> m.resolveMethodBinding()).forEach(m -> System.out.println("\t" + m.getDeclaringClass().getErasure().getQualifiedName()));
			methodInvocations.stream().map(m -> m.resolveMethodBinding()).forEach(m -> recievers.add(m.getDeclaringClass().getErasure().getQualifiedName()));
			recievers.stream().forEach(r -> System.out.println(r));
		} catch(Exception e) {
			System.err.println("Cannot resolve IMethod Binding because of " + e.toString());
		}
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
