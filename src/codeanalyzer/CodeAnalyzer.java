package codeanalyzer;

import static java.util.Comparator.comparing;

import java.util.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;

import util.Log;
import util.ProjectUtil;
import warning.*;

public class CodeAnalyzer {
	private static final int THRESHOLD_OF_LINE_COUNT_OF_METHOD = 10;
	private static final int THRESHOLD_OF_CYCLOMATIC_CONPLEXITY = 10;
	private static final int THRESHOLD_OF_LIFE_SPAN = 15;
	
	private double abstractness = 0;
	private List<Warning> warnings = new ArrayList<>();
	private Map<String, ClassInfo> ci = new HashMap<>();

	public static void main(String args[]) {
		//new CodeAnalyzer().run("src/codeanalizer/");
		new CodeAnalyzer().run("src/codeanalyzer/", "src/metricsplugin/editor/");
	}
	
	public void run(String... pathsToPackage) {
		
		for(String path: pathsToPackage) {
			run(path);
		}
		Log.print(Log.INFO);
	}

	private void run(String pathToPackage) {
		List<String> classList = FileUtil.getSourceCodeList(pathToPackage);
		Log.info("number of classes in the package: " + classList.size());

		for(String className : classList) {
			Log.info("\n" + className + "\n");
			analyze(pathToPackage, className);
		}
		abstractness /= classList.size();
		Log.info("Abstractness: " + abstractness);
		//Log.print();
		//new PackageInfo(pathToPackage, ci);
	}

	/**
	 * Analyze with level 1
	 * This method does not use ICompilationUnit
	 * 
	 * @param pathToPackage
	 * @param fileName Filename contains ".java"
	 */
	public void analyze(String pathToPackage, String fileName) {
		String rawCode = Objects.requireNonNull(FileUtil.readSourceCode(pathToPackage + fileName));

		String formattedCode = MyParser.format(rawCode);

		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setResolveBindings(true);
		parser.setSource(formattedCode.toCharArray());
		
		CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());

		MyVisitor visitor = new MyVisitor(formattedCode);
		unit.accept(visitor);

		setLineNum(visitor, rawCode);
		setLineNum2(visitor, rawCode);
		
		if(visitor.isAbstract()) abstractness++;
		/*
		for(MethodInvocation mi: visitor.getMethodInvocations()) {
			System.out.println("\t" + mi.toString());
		}
		*/
		Log.info("SuperClass: " + visitor.getSuperClass());
		ci.entrySet().stream().filter(c -> c.getValue().getPackageName() != null).forEach(c -> Log.info(c.getValue().getPackageName()));

		// printMethodDetail(unit, formattedCode);
		// printVariableDetail(unit, formattedCode);

		//showWarning(unit, formattedCode, pathToPackage + className);
		warnings.addAll(warnings(unit, formattedCode, pathToPackage + fileName));
		
		//ci.add(new ClassInfo(visitor, fileName));
		
		ClassInfo c = visitor.newClassInfo();
		ci.put(c.getClassName(), c);
		
		PackageInfo pi = new PackageInfo(pathToPackage, ci);
		System.out.println();
		
		c.lackOfCohesionInMethods();
	}

	public void analyzeCodes(ICompilationUnit unit, String pathToPackage) {
		List<String> codeList = FileUtil.getSourceCodeList(pathToPackage);
		codeList.stream().forEach(s -> analyze2(pathToPackage, s));
	}
	
	/**
	 * Analyze with level 2
	 * This method uses ICompilationUnit
	 * 
	 * @param pathToPackage
	 * @param filename Filename contains ".java"
	 */
	void analyze2(String pathToPackage, String filename) {
		IJavaProject project = JavaCore.create(ProjectUtil.currentProject());
		IType type;
		String classname = pathToPackage.substring(pathToPackage.lastIndexOf("/src/")+"/src/".length(), pathToPackage.length()) + 
				filename.substring(0, filename.lastIndexOf(".java"));
		
		try {
			type = project.findType(classname.replace('/', '.'));
		} catch (JavaModelException e) {
			e.printStackTrace();
			return;
		}
		ICompilationUnit unit = type.getCompilationUnit();
		
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(unit);
		parser.setResolveBindings(true); // Analyze with level 2 to collect detail information
		ASTNode node = parser.createAST(new NullProgressMonitor());
		
		String rawCode = Objects.requireNonNull(FileUtil.readSourceCode(pathToPackage + filename));
		MyVisitor visitor = new MyVisitor(rawCode);
		node.accept(visitor);
		
		visitor.getMethodInvocations().stream().forEach(m -> Log.verbose("MethodInvocation: " + m.toString()));
		
		ClassInfo c = visitor.newClassInfo();
		ci.put(c.getClassName(), c);
		Log.info("packages used from " + pathToPackage + filename + " {");
		c.efficientCouplings(ClassInfo.COUPLING_LEVEL_CLASS).stream().forEach(p -> Log.info("\t" + p));
		Log.info("}");
		
		Log.print(Log.INFO);
		//Log.print();
	}
	
	public void analyzeCodes22(ICompilationUnit unit, String pathToPackage, IProject iproject) {
		List<String> codeList = FileUtil.getSourceCodeList(pathToPackage);
		codeList.stream().forEach(s -> analyze22(pathToPackage, s, iproject));
	}
	
	void analyze22(String pathToPackage, String filename, IProject iproject) {
		IJavaProject project = JavaCore.create(iproject);
		IType type;
		String classname = pathToPackage.substring(pathToPackage.lastIndexOf("/src/")+"/src/".length(), pathToPackage.length()) + 
				filename.substring(0, filename.lastIndexOf(".java"));
		
		try {
			type = project.findType(classname.replace('/', '.'));
		} catch (JavaModelException e) {
			e.printStackTrace();
			return;
		}
		ICompilationUnit unit = type.getCompilationUnit();
		
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(unit);
		parser.setResolveBindings(true); // Analyze with level 2 to collect detail information
		ASTNode node = parser.createAST(new NullProgressMonitor());
		
		String rawCode = Objects.requireNonNull(FileUtil.readSourceCode(pathToPackage + filename));
		MyVisitor visitor = new MyVisitor(rawCode);
		node.accept(visitor);
		
		visitor.getMethodInvocations().stream().forEach(m -> Log.verbose("MethodInvocation: " + m.toString()));
		
		ClassInfo c = visitor.newClassInfo();
		ci.put(c.getClassName(), c);
		Log.info("packages used from " + pathToPackage + filename + " {");
		c.efficientCouplings(ClassInfo.COUPLING_LEVEL_CLASS).stream().forEach(p -> Log.info("\t" + p));
		Log.info("}");
		
		Log.print(Log.INFO);
		//Log.print();
	}

	private static void printMethodDetail(CompilationUnit unit, String code) {
		MyVisitor visitor = new MyVisitor(code);
		unit.accept(visitor);
		for(MethodDeclaration method : visitor.getMethodList()) {
			System.out.printf("蜿ｯ隕匁�ｧ遲�  =%s%n", method.modifiers());
			System.out.printf("謌ｻ繧雁梛    =%s%n", method.getReturnType2());
			System.out.printf("繝｡繧ｽ繝�繝牙錐=%s%n", method.getName().getIdentifier());
			System.out.printf("蠑墓焚      =%s%n", method.parameters());
			// System.out.printf("譛ｬ菴� =%s%n", method.getBody());
			System.out.printf("陦梧焚 =%s%n", method.properties().get(MyVisitor.LINE_COUNT));
			System.out.printf("McCabe=%s%n", method.properties().get(MyVisitor.CYCLOMATIC_COMPLEXITY));
			System.out.println();
		}
	}

	private static void printVariableDetail(CompilationUnit unit, String code) {
		MyVisitor visitor = new MyVisitor(code);
		unit.accept(visitor);
		for(VariableDeclarationFragment variable : visitor.getVariableList()) {
			System.out.printf("螟画焚蜷�   =%s%n", variable.getName().getIdentifier());
			System.out.printf("髢句ｧ玖｡� =%s%n", variable.getProperty(MyVisitor.DECLARED_LINE));
			System.out.printf("蛻晄悄蛹門ｭ�  =%s%n", variable.getInitializer());

			if(variable.getProperty(MyVisitor.DEFINITION_PLACE) instanceof MethodDeclaration) {
				System.out.println("繝ｭ繝ｼ繧ｫ繝ｫ螟画焚");
				System.out.printf("蟇ｿ蜻ｽ=%s%n", variable.getProperty(MyVisitor.LIFE_SPAN));
			} else {
				System.out.println("繝輔ぅ繝ｼ繝ｫ繝牙､画焚");
			}

			System.out.println();
		}
	}

	private static void setLineNum(MyVisitor formattedVisitor, String rawCode) {
		MyVisitor visitor = new MyVisitor(rawCode);
		List<VariableDeclarationFragment> varList = formattedVisitor.getVariableList();

		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(rawCode.toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
		unit.accept(visitor);

		for(int i = 0; i < varList.size(); i++) {
			varList.get(i).setProperty(MyVisitor.DECLARED_LINE,
					unit.getLineNumber(visitor.getVariableList().get(i).getStartPosition()));
		}
	}
	private static void setLineNum2(MyVisitor formattedVisitor, String rawCode) {
		MyVisitor visitor = new MyVisitor(rawCode);
		List<MethodDeclaration> methList = formattedVisitor.getMethodList();

		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(rawCode.toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
		unit.accept(visitor);

		for(int i = 0; i < methList.size(); i++) {
			methList.get(i).setProperty(MyVisitor.DECLARED_LINE,
					unit.getLineNumber(visitor.getMethodList().get(i).getStartPosition()));
		}
	}

	private static List<Warning> warnings(CompilationUnit unit, String code, String filename) {
		List<Warning> warnings = new ArrayList<>();
		MyVisitor visitor = new MyVisitor(code);
		unit.accept(visitor);

		visitor.getVariableList().stream()
				.filter(v -> (v.getProperty(MyVisitor.DEFINITION_PLACE) instanceof MethodDeclaration)
						&& lifeSpanOf(v) > THRESHOLD_OF_LIFE_SPAN)
				.sorted(comparing(CodeAnalyzer::lifeSpanOf).reversed())
				.forEach(node -> warnings.add(new LargeScopeWarning(unit, node, filename, lifeSpanOf(node))));

		visitor.getMethodList().stream().filter(m -> lineCountOf(m) > THRESHOLD_OF_LINE_COUNT_OF_METHOD)
				.sorted(comparing(CodeAnalyzer::lineCountOf).reversed())
				.forEach(node -> warnings.add(new LargeMethodWarning(unit, node, filename, lineCountOf(node))));

		visitor.getMethodList().stream().filter(m -> cyclomaticComplexityOf(m) > THRESHOLD_OF_CYCLOMATIC_CONPLEXITY)
				.sorted(comparing(CodeAnalyzer::cyclomaticComplexityOf).reversed())
				.forEach(node -> warnings.add(new ComplexMethodWarning(unit, node, filename, cyclomaticComplexityOf(node))));

		return warnings;
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

	public Map<String, ClassInfo> getClassInfo() {
		return ci;
	}
}
