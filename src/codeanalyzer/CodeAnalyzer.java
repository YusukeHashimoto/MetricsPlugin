package codeanalyzer;

import static java.util.Comparator.comparing;

import java.util.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;

import util.*;
import warning.*;
import warning.ckmetrics.*;

public class CodeAnalyzer {
	private static final int THRESHOLD_OF_LINE_COUNT_OF_METHOD = 10;
	private static final int THRESHOLD_OF_CYCLOMATIC_CONPLEXITY = 10;
	private static final int THRESHOLD_OF_LIFE_SPAN = 15;

	private double abstractness = 0;
	private List<Warning> warnings = new ArrayList<>();
	private Map<String, ClassInfo> ci = new HashMap<>();

	public static void main(String args[]) {
		new CodeAnalyzer().run("src/codeanalyzer/");
	}

	public void run(String... pathsToPackage) {
		for (String path : pathsToPackage) {
			run(path);
		}
		Log.print(Log.INFO);
	}

	private void run(String pathToPackage) {
		List<String> classList = FileUtil.getSourceCodeList(pathToPackage);
		Log.info("number of classes in the package: " + classList.size());

		for (String className : classList) {
			Log.info("\n" + className + "\n");
			analyze(pathToPackage, className);
		}
		abstractness /= classList.size();
		Log.info("Abstractness: " + abstractness);
	}

	/**
	 * Analyze with level 1 This method does not use ICompilationUnit
	 * 
	 * @param pathToPackage
	 * @param fileName
	 *            Filename contains ".java"
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

		if (visitor.isAbstract())
			abstractness++;

		Log.info("SuperClass: " + visitor.getSuperClass());
		ci.entrySet().stream().filter(c -> c.getValue().getPackageName() != null)
				.forEach(c -> Log.info(c.getValue().getPackageName()));

		//warnings.addAll(warnings(unit, formattedCode, pathToPackage + fileName));

		for (ClassInfo c : visitor.classInfoSet()) {
			ci.put(c.getClassName(), c);
			PackageInfo pi = new PackageInfo(pathToPackage, ci);
			System.out.println();
		}
	}

	public void analyzeCodes(ICompilationUnit unit, String pathToPackage, IProject iproject) {
		List<String> codeList = FileUtil.getSourceCodeList(pathToPackage);
		codeList.stream().forEach(s -> analyze2(pathToPackage, s, iproject));
	}

	/**
	 * Analyze with level 2<br>
	 * This method uses ICompilationUnit
	 * 
	 * @param pathToPackage
	 * @param filename
	 *            Filename contains ".java"
	 * @param iproject
	 *            If it's null, project currently opened in editor will be
	 *            selected automatically
	 */
	void analyze2(String pathToPackage, String filename, IProject iproject) {
		IJavaProject project = JavaCore.create(iproject == null ? ProjectUtil.currentProject() : iproject);
		IType type;
		String classname = pathToPackage.substring(pathToPackage.lastIndexOf("/src/") + "/src/".length(),
				pathToPackage.length()) + filename.substring(0, filename.lastIndexOf(".java"));

		try {
			type = project.findType(classname.replace('/', '.'));
		} catch (JavaModelException e) {
			e.printStackTrace();
			return;
		}
		ICompilationUnit unit = type.getCompilationUnit();

		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(unit);
		parser.setResolveBindings(true); // Analyze with level 2 to collect
											// detail information
		ASTNode node = parser.createAST(new NullProgressMonitor());

		String rawCode = Objects.requireNonNull(FileUtil.readSourceCode(pathToPackage + filename));
		MyVisitor visitor = new MyVisitor(rawCode);
		node.accept(visitor);

		setLineNum(visitor, rawCode);
		setLineNum2(visitor, rawCode);
		visitor.getMethodInvocations().stream().forEach(m -> Log.verbose("MethodInvocation: " + m.toString()));

		for (ClassInfo c : visitor.classInfoSet()) {
			ci.put(c.getClassName(), c);
			//Log.info("packages used from " + pathToPackage + filename + " {");
			//c.efficientCouplings(ClassInfo.COUPLING_LEVEL_CLASS).stream().forEach(p -> Log.info("\t" + p));
			warnings.addAll(warnings(c, pathToPackage + filename, visitor.classInfoSet()));
			//Log.info("}");
		}
		genClassMetrics(visitor.classInfoSet());
	}

	private static void setLineNum(MyVisitor formattedVisitor, String rawCode) {
		MyVisitor visitor = new MyVisitor(rawCode);
		List<VariableDeclarationFragment> varList = formattedVisitor.getVariableList();

		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(rawCode.toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
		unit.accept(visitor);

		for (int i = 0; i < varList.size(); i++) {
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

		for (int i = 0; i < methList.size(); i++) {
			methList.get(i).setProperty(MyVisitor.DECLARED_LINE,
					unit.getLineNumber(visitor.getMethodList().get(i).getStartPosition()));
		}
	}

	private List<Warning> warnings(ClassInfo ci, String filename, Set<ClassInfo> ciset) {
		List<Warning> warnings = new ArrayList<Warning>();

		ci.getMethodDeclarations().stream().filter(m -> lineCountOf(m) > THRESHOLD_OF_LINE_COUNT_OF_METHOD)
				.sorted(comparing(CodeAnalyzer::lineCountOf).reversed())
				.forEach(node -> warnings.add(new LargeMethodWarning(null, node, filename, lineCountOf(node))));

		ci.getMethodDeclarations().stream().filter(m -> cyclomaticComplexityOf(m) > THRESHOLD_OF_CYCLOMATIC_CONPLEXITY)

				.sorted(comparing(CodeAnalyzer::cyclomaticComplexityOf).reversed()).forEach(node -> warnings
						.add(new ComplexMethodWarning(null, node, filename, cyclomaticComplexityOf(node))));

		ci.getVarDecls().stream()
				.filter(v -> (v.getProperty(MyVisitor.DEFINITION_PLACE) instanceof MethodDeclaration)
						&& lifeSpanOf(v) > THRESHOLD_OF_LIFE_SPAN)
				.sorted(comparing(CodeAnalyzer::lifeSpanOf).reversed())
				.forEach(node -> warnings.add(new LargeScopeWarning(null, node, filename, lifeSpanOf(node))));

		if (ci.weightedMethodsPerClass() > Threshold.WEIGHTED_METHOD_PER_CLASS) {
			warnings.add(new WMCWarning(null, ci.getMethodDeclarations(), filename, ci.weightedMethodsPerClass()));
		}
		
		if(ci.lackOfCohesionInMethods() > Threshold.LACK_OF_COHESION) {
			warnings.add(new LCOMWarning(null, ci.getMethodDeclarations(), filename, ci.lackOfCohesionInMethods()));
		}
		
		if(ci.efficientCouplings().size() > Threshold.COUPLING_BETWEEN_OBJECTS) {
			warnings.add(new CBOWarning(null, ci.getMethodDeclarations(), filename, ci.efficientCouplings().size()));
		}
		
		if(ci.depthOfInheritanceTree(ciset) > Threshold.DEPTH_OF_INHERITANCE_TREE) {
			warnings.add(new DITWarning(null, ci.getMethodDeclarations(), filename, ci.depthOfInheritanceTree(ciset)));
		}
		
		if(ci.numberOfChildren(ciset) > Threshold.NUMBER_OF_CHILDREN) {
			warnings.add(new NOCWarning(null, ci.getMethodDeclarations(), filename, ci.numberOfChildren(ciset)));
		}
		
		if(ci.responsesForClass().size() > Threshold.RESPONSE_FOR_CLASS) {
			warnings.add(new RFCWarning(null, ci.getMethodDeclarations(), filename, ci.responsesForClass().size()));
		}
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

	public ClassMetrics genClassMetrics(Collection<ClassInfo> classInfos) {
		for (ClassInfo ci : classInfos) {
			ClassMetrics cm = new ClassMetrics(ci, classInfos);
			System.err.println(cm.toJson());
		}
		return null;
	}
}
