package metricsplugin.handler;

import java.util.Map;

import org.eclipse.core.commands.*;

import codeanalyzer.ClassInfo;
import codeanalyzer.CodeAnalyzer;
import log.OpeLog;
import metricsplugin.views.WebViewer;
import util.ProjectUtil;

abstract class URLHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		CodeAnalyzer ca = new CodeAnalyzer();
		try {
			ca.analyzeCodes(null, ProjectUtil.pathToPackage(), null);
			WebViewer.showInternalBrowser(generateURL(ca.getClassInfo()), title);
		} catch(ClassCastException e) {
			
		}
		
		String message = "showed ";
		message += this instanceof InheritanceTreeHandler ? "Inheritance tree" : "Dependence graph";
		OpeLog.getInstance().log(message);
		
		return null;
	}
	
	abstract String generateURL(Map<String, ClassInfo> classMap);
	
	protected String title = "No title";
}
