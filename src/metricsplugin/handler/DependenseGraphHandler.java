package metricsplugin.handler;

import java.util.Map;
import java.util.Map.Entry;

import codeanalyzer.ClassInfo;

public class DependenseGraphHandler extends URLHandler{
	{
		this.title = "Dependense Graph";
	}
	
	@Override
	String generateURL(Map<String, ClassInfo> classMap) {
		StringBuilder url = new StringBuilder("file:///C:/Users/Hashimoto/GoogleDrive/MetricsGraph/graphsample.html?");
		//classMap.entrySet().stream().map(e -> e.getValue()).forEach(v -> url.append(v.toURLParameter() + '&'));
		
		for(Entry<String, ClassInfo> e : classMap.entrySet()) {
			for(String name : e.getValue().efficientCouplings(ClassInfo.COUPLING_LEVEL_CLASS)) {
				url.append(e.getValue().getClassName() + "=" + name + "&");
			}
		}

		return url.toString();
	}
}
