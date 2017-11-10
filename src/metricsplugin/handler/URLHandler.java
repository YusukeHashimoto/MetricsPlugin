package metricsplugin.handler;

import java.util.Map;

import org.eclipse.core.commands.*;

import codeanalyzer.ClassInfo;
import codeanalyzer.CodeAnalyzer;
import metricsplugin.views.WebViewer;
import util.ProjectUtil;

abstract class URLHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		CodeAnalyzer ca = new CodeAnalyzer();
		ca.analyzeCodes(null, ProjectUtil.pathToPackage(), null);
		WebViewer.showInternalBrowser(generateURL(ca.getClassInfo()));
		return null;
	}
	
	abstract String generateURL(Map<String, ClassInfo> classMap);
}
