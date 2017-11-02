package metricsplugin.test;

import org.eclipse.core.resources.*;
import org.junit.Test;

import codeanalyzer.ClassInfo;
import codeanalyzer.CodeAnalyzer;

public class MetricsTest {

	@Test
	public void test() {
		//fail("Not yet implemented");
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		CodeAnalyzer ca = new CodeAnalyzer();
		ca.analyzeCodes(null, "C:/Users/Hashimoto/runtime-EclipseApplication/SampleProject/src/sample/");
		ClassInfo animal = ca.getClassInfo().get("Animal");
		System.out.println(animal.efficientCouplings(ClassInfo.COUPLING_LEVEL_CLASS));
	}

}
