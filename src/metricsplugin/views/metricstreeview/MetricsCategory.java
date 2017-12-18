package metricsplugin.views.metricstreeview;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import warning.Warning;
import warning.ckmetrics.CKMetricsWarning;

public enum MetricsCategory implements Node<Object, Warning> {
	SIMPLE_METRICS("Simple Metrics"), CK_METRICS("CK Metrics");

	private List<Warning> warnings = new ArrayList<>();

	public List<Warning> getWarnings() {
		return warnings;
	}

	protected void setWarnings(List<Warning> warnings) {
		this.warnings = warnings;
	}
	
	public static void setAllWarnings(List<Warning> warnings) {
		SIMPLE_METRICS.setWarnings(warnings.stream().filter(w -> !(w instanceof CKMetricsWarning)).collect(Collectors.toList()));
		CK_METRICS.setWarnings(warnings.stream().filter(w -> w instanceof CKMetricsWarning).collect(Collectors.toList()));
	}

	@Override
	public List<Warning> getChildren() {
		return warnings;
	}

	@Override
	public Object getParent() {
		return null;
	}

	@Override
	public boolean hasChildren() {
		return !warnings.isEmpty();
	}

	@Override
	public String getLabel() {
		return label + " (" + warnings.size() + ")";
	}

	private String label;

	private MetricsCategory(String label) {
		this.label = label;
	}

}
