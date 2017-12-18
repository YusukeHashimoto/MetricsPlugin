package warning.suggestion;

import warning.Warning;

public class CollapseHierarchySuggestion extends Suggestion {

	public CollapseHierarchySuggestion(Warning warning) {
		super(warning);
	}

	@Override
	public String message() {
		return "階層の平坦化";
	}

}
