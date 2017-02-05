package codeanalizer;

import static java.util.Comparator.comparing;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.*;

import codeanalizer.FileUtil;
import warning.*;
import kotlinwarning.LargeScopeWarning;

public class CodeAnalizer {
	private static final int THRESHOLD_OF_LINE_COUNT_OF_METHOD = 10;
	private static final int THRESHOLD_OF_CYCLOMATIC_CONPLEXITY = 10;
	private static final int THRESHOLD_OF_LIFE_SPAN = 15;
	
	private List<String> messages = new ArrayList<>(); //fix later
	private List<Warning> warnings = new ArrayList<>();

	public static void main(String args[]) {
		// new CodeAnalizer().run("res/");
		new CodeAnalizer().run("src/codeanalizer/");
	}

	public void run(String pathToPackage) {
		List<String> classList = FileUtil.getSourceCodeList(pathToPackage);
		System.out.println("number of classes in the package: " + classList.size());

		for(String className : classList) {
			System.out.println("\n" + className + "\n");
			run(pathToPackage, className);
		}
	}

	public void run(String pathToPackage, String className) {
		String rawCode = FileUtil.readSourceCode(pathToPackage + className);

		if(rawCode == null) return;

		String formattedCode = MyParser.format(rawCode);

		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(formattedCode.toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());

		MyVisitor visitor = new MyVisitor(formattedCode);
		unit.accept(visitor);

		setLineNum(visitor, rawCode);
		setLineNum2(visitor, rawCode);

		// printMethodDetail(unit, formattedCode);
		// printVariableDetail(unit, formattedCode);

		showWarning(unit, formattedCode, pathToPackage + className);
		System.out.println(messages);

		// System.out.println("Cyclomatic complexity: " +
		// visitor.totalCyclomaticComplexity());
	}

	private static void printMethodDetail(CompilationUnit unit, String code) {
		MyVisitor visitor = new MyVisitor(code);
		unit.accept(visitor);
		for(MethodDeclaration method : visitor.getMethodList()) {
			System.out.printf("可視性等  =%s%n", method.modifiers());
			System.out.printf("戻り型    =%s%n", method.getReturnType2());
			System.out.printf("メソッド名=%s%n", method.getName().getIdentifier());
			System.out.printf("引数      =%s%n", method.parameters());
			// System.out.printf("本体 =%s%n", method.getBody());
			System.out.printf("行数 =%s%n", method.properties().get(MyVisitor.LINE_COUNT));
			System.out.printf("McCabe=%s%n", method.properties().get(MyVisitor.CYCLOMATIC_COMPLEXITY));
			System.out.println();
		}
	}

	private static void printVariableDetail(CompilationUnit unit, String code) {
		MyVisitor visitor = new MyVisitor(code);
		unit.accept(visitor);
		for(VariableDeclarationFragment variable : visitor.getVariableList()) {
			System.out.printf("変数名   =%s%n", variable.getName().getIdentifier());
			System.out.printf("開始行 =%s%n", variable.getProperty(MyVisitor.DECLARED_LINE));
			System.out.printf("初期化子  =%s%n", variable.getInitializer());

			if(variable.getProperty(MyVisitor.DEFINITION_PLACE) instanceof MethodDeclaration) {
				System.out.println("ローカル変数");
				System.out.printf("寿命=%s%n", variable.getProperty(MyVisitor.LIFE_SPAN));
			} else {
				System.out.println("フィールド変数");
			}

			System.out.println();
		}
	}

	private void setLineNum(MyVisitor formattedVisitor, String rawCode) {
		MyVisitor visitor = new MyVisitor(rawCode);
		List<VariableDeclarationFragment> varList = formattedVisitor.getVariableList();

		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(rawCode.toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
		unit.accept(visitor);

		for(int i = 0; i < varList.size(); i++) {
			varList.get(i).setProperty(MyVisitor.DECLARED_LINE,
					unit.getLineNumber(visitor.getVariableList().get(i).getStartPosition()));
		}
	}
	private void setLineNum2(MyVisitor formattedVisitor, String rawCode) {
		MyVisitor visitor = new MyVisitor(rawCode);
		List<MethodDeclaration> methList = formattedVisitor.getMethodList();

		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(rawCode.toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
		unit.accept(visitor);

		for(int i = 0; i < methList.size(); i++) {
			methList.get(i).setProperty(MyVisitor.DECLARED_LINE,
					unit.getLineNumber(visitor.getMethodList().get(i).getStartPosition()));
		}
	}

	void showWarning(CompilationUnit unit, String code, String filename) {
		MyVisitor visitor = new MyVisitor(code);
		unit.accept(visitor);

		visitor.getVariableList().stream()
				.filter(v -> (v.getProperty(MyVisitor.DEFINITION_PLACE) instanceof MethodDeclaration)
						&& lifeSpanOf(v) > THRESHOLD_OF_LIFE_SPAN)
				.sorted(comparing(CodeAnalizer::lifeSpanOf).reversed())
				//.forEach(variable -> messages.add(variable.getName() + "の寿命が長い(" + lifeSpanOf(variable) + "行)"));
				//.forEach(node -> warnings.add(new LifeSpanWarning(unit, node, lifeSpanOf(node))));
				.forEach(node -> warnings.add(new LargeScopeWarning(unit, node, filename, lifeSpanOf(node))));

		//new KotlinSample().sayHello();
		System.out.println();

		visitor.getMethodList().stream().filter(m -> lineCountOf(m) > THRESHOLD_OF_LINE_COUNT_OF_METHOD)
				.sorted(comparing(CodeAnalizer::lineCountOf).reversed())
				//.forEach(m -> messages.add(m.getName() + "の行数が長い(" + lineCountOf(m) + "行)"));
				.forEach(node -> warnings.add(new LargeMethodWarning(unit, node, filename, lineCountOf(node))));

		System.out.println();

		visitor.getMethodList().stream().filter(m -> cyclomaticComplexityOf(m) > THRESHOLD_OF_CYCLOMATIC_CONPLEXITY)
				.sorted(comparing(CodeAnalizer::cyclomaticComplexityOf).reversed())
				//.forEach(m -> messages.add(m.getName() + "のサイクロマチック数が大きい(" + cyclomaticComplexityOf(m) + ")"));
				.forEach(node -> warnings.add(new ComplexMethodWarning(unit, node, filename, cyclomaticComplexityOf(node))));
	}

	private static int lineCountOf(MethodDeclaration method) {
		return (int) method.getProperty(MyVisitor.LINE_COUNT);
	}

	private static int cyclomaticComplexityOf(MethodDeclaration method) {
		return (int) method.getProperty(MyVisitor.CYCLOMATIC_COMPLEXITY);
	}

	private static int lifeSpanOf(VariableDeclarationFragment variable) {
		return (int) variable.getProperty(MyVisitor.LIFE_SPAN);
	}
	
	public List<Warning> getWarnings() {
		return warnings;
	}

}
