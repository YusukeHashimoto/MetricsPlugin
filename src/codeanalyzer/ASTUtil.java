package codeanalyzer;

import org.eclipse.jdt.core.dom.*;

public class ASTUtil {
	
	static MethodDeclaration parentMethodOf(ASTNode node) {
		ASTNode parent = node.getParent();
		while(!(parent instanceof MethodDeclaration)) {
			parent = parent.getParent();
			if(parent == null) return null;
		}
		return (MethodDeclaration) parent;
	}

	static ASTNode definitionPlaceOf(ASTNode node) {
		ASTNode parent = node.getParent();
		if(parent == null) return node;
		while(!(parent instanceof MethodDeclaration) && !(parent instanceof CompilationUnit)) {
			parent = parent.getParent();
		}
		return parent;
	}

	static AbstractTypeDeclaration definedClassOf(ASTNode node) {
		if(node instanceof AbstractTypeDeclaration) return (AbstractTypeDeclaration)node;
		ASTNode parent = node.getParent();
		if(parent == null) return null;
		while(!(parent instanceof AbstractTypeDeclaration)) {
			parent = parent.getParent();
			if(parent == null) return null;
		}
		return (AbstractTypeDeclaration)parent;
	}

	static boolean isField(ASTNode node) {
		if(node instanceof FieldDeclaration) return true;
		ASTNode parent = node.getParent();
		if(parent == null) return false;
		while(!(parent instanceof FieldDeclaration)) {
			parent = parent.getParent();
			if(parent == null) return false;
		}
		if(parent instanceof FieldDeclaration) 
			return true;
		else return false;
	}
	
	static String definedClassnameOf(ASTNode node) {
		return definedClassOf(node).getName().toString();
	}
	
	public static String methodNameOf(ASTNode node) {
		return methodNameOf((MethodDeclaration)node);
	}
	
	public static String methodNameOf(MethodDeclaration md) {
		StringBuilder sb = new StringBuilder();
		sb.append(md.getName().toString());
		sb.append('(');
		for(Object obj : md.parameters()) {
			sb.append(((SingleVariableDeclaration)obj).getType().toString());
			sb.append(',');
		}
		if(sb.charAt(sb.length()-1) != '(') {
			sb.deleteCharAt(sb.length()-1);	
		}
		
		sb.append(')');
		
		return sb.toString();
	}
}
