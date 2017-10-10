package codeanalyzer

class Node(name: String) {
	val name = name
	var parent = "Object"
	var children: List<Node> = listOf()
	
	fun addChild(node: Node) {
		children += node
	}
}