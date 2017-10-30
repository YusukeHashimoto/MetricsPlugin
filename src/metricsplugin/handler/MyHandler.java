package metricsplugin.handler;

import java.util.Map;

import org.eclipse.core.commands.*;

import codeanalyzer.ClassInfo;
import codeanalyzer.CodeAnalyzer;
import metricsplugin.views.WebViewer;
import util.ProjectUtil;

public class MyHandler extends AbstractHandler {
	private Map<String, ClassInfo> classMap;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		/*
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
			window.getShell(),
			"Plugin-example2",
			"Hello, Eclipse world");*/
		CodeAnalyzer ca = new CodeAnalyzer();
		try {
			ca.run(ProjectUtil.pathToPackage().substring(1));
		} catch(Exception e) {
			ca.run(ProjectUtil.pathToPackage());
		}

		classMap = ca.getClassInfo();
		StringBuilder url = new StringBuilder("file:///C:/Users/Hashimoto/GoogleDrive/MetricsGraph/graphsample.html?");
		classMap.entrySet().stream().map(e -> e.getValue()).forEach(v -> url.append(v.toURLParameter() + '&'));
		//WebViewer.showInternalBrowser("http://google.com");
		WebViewer.showInternalBrowser(url.toString());
		return null;
	}

}
