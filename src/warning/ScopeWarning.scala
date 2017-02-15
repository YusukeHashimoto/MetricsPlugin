package warning

import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.CompilationUnit

class ScopeWarning(unit: CompilationUnit, node: ASTNode, filename: String, lifeSpan: Int) extends Warning(unit, node, filename) {
  val lifeSapn = lifeSpan
  
  def getMessage() = "変数の寿命が長い(" + lifeSpan + ")"
  
}