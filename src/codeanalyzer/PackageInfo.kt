package codeanalyzer

class PackageInfo(packagename: String?, classMap: Map<String, ClassInfo>?) {

	init {
		classMap?.forEach {
			val superclass = classMap[it.value.superclassName]
			it.value.superclass = superclass
			superclass?.addSubclass(it.value)
		}
	}
}
