package metricsplugin.handler;

import java.util.Map;

import codeanalyzer.ClassInfo;

public class InheritanceTreeHandler extends URLHandler {
	
	@Override
	String generateURL(Map<String, ClassInfo> classMap) {
		StringBuilder url = new StringBuilder("file:///C:/Users/Hashimoto/GoogleDrive/MetricsGraph/inheritancetree.html?");
		classMap.entrySet().stream().map(e -> e.getValue()).forEach(v -> url.append(v.toURLParameter() + '&'));
		/*
		for(Entry<String, ClassInfo> e : classMap.entrySet()) {
			for(String name : e.getValue().efficientCouplings(ClassInfo.COUPLING_LEVEL_CLASS)) {
				url.append(e.getValue().getClassName() + "=" + name + "&");
			}
		}*/

		return url.toString();
	}
	
	{
		this.title = "Inheritance Tree";
	}
}
