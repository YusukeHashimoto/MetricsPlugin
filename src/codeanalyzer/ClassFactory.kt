package codeanalyzer

import java.util.*
import org.eclipse.jdt.core.dom.*
import java.lang.AssertionError
import java.lang.UnsupportedOperationException

class ClassFactory {
	private class Prototype {
		internal val methodList = arrayListOf<MethodDeclaration>()
		internal val variableList = ArrayList<VariableDeclarationFragment>()
		internal val blockList = ArrayList<Block>()
		internal var cyclomaticComplexity = 1
		internal var isAbstract = false
		internal var superClass: String? = null
		internal val methodInvocations = ArrayList<MethodInvocation>()
		internal var packagename: String? = null
		internal var filename: String? = null
		internal var className: String? = null
		internal val parameters = arrayListOf<SingleVariableDeclaration>()
		internal val cohesionMap: MutableMap<VariableDeclarationFragment, MutableSet<MethodDeclaration>> = mutableMapOf()//HashMap()
		internal var names = arrayListOf<SimpleName>()
		internal val exceptions = hashSetOf<Type>()
		internal val fieldVars = hashSetOf<VariableDeclarationFragment>()

		fun toClassInfo(): ClassInfo {
			val builder = ClassInfo.Builder(filename, packagename)
			builder.methodDeclarations(methodList)
			builder.varList(variableList)
			builder.methodInvocations(methodInvocations)
			builder.isAbstract(isAbstract)
			builder.superClass(superClass)
			builder.className(className)
			builder.parameters(parameters)
			builder.exceptions(exceptions)
			builder.fieldVars(fieldVars)
			generateCohesionMap()
			builder.cohesionMap(cohesionMap)
			return builder.build()
		}
		
		fun generateCohesionMap() {
			val localVars: List<VariableDeclarationFragment> = variableList.filter{v -> !fieldVars.contains(v)}.toList();
			
			names.filter { node -> ASTUtil.parentMethodOf(node) != null}.forEach{
				node ->
				fieldVars.filter { v -> v.name.fullyQualifiedName.equals(node.fullyQualifiedName) && ASTUtil.definedClassOf(v).equals(ASTUtil.definedClassOf(node))}.forEach{
						v ->
				//v.filter { v ->
					if(localVars.filter { lv -> lv.name.toString().equals(v.name.toString()) && ASTUtil.parentMethodOf(lv).equals(ASTUtil.parentMethodOf(v))}.any {
						lv -> !ASTUtil.parentMethodOf(lv).toString().contains(".this")
						
					}) {
						// do nothing
					} else {
						if(cohesionMap[v] == null)
							cohesionMap.put(v, mutableSetOf())
						cohesionMap[v]!!.add(ASTUtil.parentMethodOf(node))
					}
				}
			}
		}
	}

	private val classMap = HashMap<String, Prototype>()
	private fun prototypeOf(classname: String): Prototype {
		if(classMap[classname] == null) {
			val x = 0;
		}
		return classMap[classname]!!
	}
	fun addNode(node: ASTNode) {
		if(ASTUtil.definedClassOf(node) != null)
			addNode(ASTUtil.definedClassOf(node).name.toString(), node)
	}

	internal fun addNode(classname: String, node: ASTNode) {
		if(node is TypeDeclaration) {
			classMap.put(classname, Prototype())
			classMap.get(classname)?.className = classname
			try {
				prototypeOf(classname).superClass = node.superclassType?.toString()
			} catch(e: UnsupportedOperationException) {
				
			}
		} else if(node is MethodDeclaration) {
			prototypeOf(classname).methodList.add(node)
		} else if(node is Block) {
			prototypeOf(classname).blockList.add(node)
		} else if(node is MethodInvocation) {
			prototypeOf(classname).methodInvocations.add(node)
		} else if(node is SimpleName) {
			prototypeOf(classname).names.add(node)
		} else if(node is VariableDeclarationFragment) {
			prototypeOf(classname).variableList.add(node)
			if(ASTUtil.isField(node)) {
				prototypeOf(classname).fieldVars.add(node)
			} 
		} else if(node is SingleVariableDeclaration) {
			prototypeOf(classname).parameters.add(node)
		} else {
			assert(false)
		}
	}
	
	fun addParameters(classname: String, parameters: List<SingleVariableDeclaration>) {
		prototypeOf(classname).parameters.addAll(parameters)
	}
	
	fun incrementCC(node: ASTNode) {
		classMap.filter{it.value.methodList.contains(ASTUtil.parentMethodOf(node))}.forEach{it.value.cyclomaticComplexity++}
	}
	
	fun setAbstract(classname: String, isAbstract: Boolean) {
		prototypeOf(classname).isAbstract = isAbstract
	}
	
	fun addExceptions(classname: String, exceptions: List<Type>) {
		prototypeOf(classname).exceptions.addAll(exceptions)
	}
	
	fun setPackagename(packagename: String) {
		classMap.forEach { it.value.packagename = packagename }
	}
	
	fun setFilename(filename: String) {
		classMap.forEach { it.value.filename = filename }
	}
	
	fun fieldVars(classname: String, fieldVars: List<VariableDeclarationFragment>) {
		prototypeOf(classname).fieldVars.addAll(fieldVars)
	}
	
	fun toClassInfo(): List<ClassInfo> = classMap.map { it.value.toClassInfo() }

}