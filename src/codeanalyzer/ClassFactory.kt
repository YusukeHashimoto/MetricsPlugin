package codeanalyzer

import java.util.*
import org.eclipse.jdt.core.dom.*
import java.lang.AssertionError

class Factory {
	private class Prototype {
		internal val methodList = arrayListOf<MethodDeclaration>()
		internal val variableList = ArrayList<VariableDeclarationFragment>()
		internal val blockList = ArrayList<Block>()
		internal var parser: MyParser? = null
		internal var cyclomaticComplexity = 1
		internal var isAbstract = false
		internal var superClass: String? = null
		internal val methodInvocations = ArrayList<MethodInvocation>()
		internal var packagename: String? = null
		internal var filename: String? = null
		internal var className: String? = null
		internal val parameters = arrayListOf<SingleVariableDeclaration>()
		internal val cohesionMap: Map<VariableDeclarationFragment, Set<MethodDeclaration>>? = HashMap()
		internal var names = arrayListOf<SimpleName>()
		internal val exceptions = hashSetOf<Type>()

		companion object {
			internal val LINE_COUNT: String? = "line_count"
			internal val LIFE_SPAN: String? = "life"
			val DECLARED_LINE: String? = "declared_line"
			internal val CYCLOMATIC_COMPLEXITY: String? = "mccabe"
			//static final String LOCAL_VARIABLE = "local";
			internal val DEFINITION_PLACE: String? = "def_place"
		}
	}

	private val classMap = HashMap<String, Prototype>()
	private fun prototypeOf(classname: String): Prototype {
		return classMap[classname]!!
	}
	internal fun addNode(node: ASTNode?) {
		addNode(ASTUtil.definedClassOf(node).name.toString(), node!!)
	}

	internal fun addNode(classname: String, node: ASTNode) {
		if(node is TypeDeclaration) {
			classMap.put(classname, Prototype())
			classMap.get(classname)?.className = classname
			prototypeOf(classname).superClass = node.superclassType.toString()
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
		} else if(node is SingleVariableDeclaration) {
			prototypeOf(classname).parameters.add(node)
		} else {
			assert(false)
		}
	}
	
	fun incrementCC(classname: String) {
		prototypeOf(classname).cyclomaticComplexity++
	}
	
	fun setAbstract(classname: String, isAbstract: Boolean) {
		prototypeOf(classname).isAbstract = isAbstract
	}
	
	fun addExceptions(classname: String, exceptions: List<Type>) {
		prototypeOf(classname).exceptions.addAll(exceptions)
	}
	
	fun setPackagename(classname: String, packagename: String) {
		prototypeOf(classname).packagename = packagename
	}
	
	fun setFilename(filename: String) {
		classMap.forEach {
			it.value.filename = filename
		}
	}
}