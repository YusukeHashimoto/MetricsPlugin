package metricsplugin.views.metricstreeview;

import java.util.ArrayList;
import java.util.List;

import warning.Warning;

public enum MetricsCategory implements Node<Object, Warning> {
	SIMPLE_METRICS("Simple Metrics"), CK_METRICS("CK Metrics");

	private List<Warning> warnings = new ArrayList<>();

	public List<Warning> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<Warning> warnings) {
		this.warnings = warnings;
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
		return label;
	}

	private String label;

	private MetricsCategory(String label) {
		this.label = label;
	}

}
