package util;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import codeanalyzer.ClassInfo;

public class ClassMetrics {
	private String classname;
	private String filename;
	private List<MethodMetrics> methodList;

	private int wmc;
	private int noc;
	private double lcom;
	private int rfc;
	private int dit;
	private int cbo;

	private static final Gson gson = new Gson();

	public ClassMetrics(ClassInfo ci, Collection<ClassInfo> allClass) {
		classname = ci.getClassName();
		filename = ci.getFileName();
		wmc = ci.weightedMethodsPerClass();
		noc = ci.numberOfChildren(allClass);
		//lcom = ci.lackOfCohesionInMethods();
		rfc = ci.responsesForClass().size();
		dit = ci.depthOfInheritanceTree(allClass);
		cbo = ci.efficientCouplings(ClassInfo.COUPLING_LEVEL_CLASS).size();
		
		methodList = ci.getMethodDeclarations().stream().map(m -> new MethodMetrics(m)).collect(Collectors.toList());
	}

	public String toJson() {
		return gson.toJson(this);
	}
}
