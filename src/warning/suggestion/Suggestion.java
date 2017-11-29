package warning.suggestion;

import java.util.List;

import metricsplugin.views.metricstreeview.Node;
import warning.Warning;

public abstract class Suggestion implements Node<Warning, Object> {
	/*
	 * public static final int SPLIT_METHOD = 0; public static final int
	 * EXTRACT_CONDITIONS_AS_METHOD = 1; public static final int
	 * INLINING_VARIABLE = 2;
	 */
	// private int type = 0;
	private Warning warning;

	@Override
	public List<Object> getChildren() {
		return null;
	}

	@Override
	public Warning getParent() {
		return warning;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public String getLabel() {
		return message();
	}

	public Suggestion(Warning warning) {
		this.warning = warning;
	}

	public abstract String message();

	public Warning parentWarning() {
		return warning;
	}
}
