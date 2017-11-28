package warning.suggestion;

import warning.Warning;

public class InliningVariableSuggestion extends Suggestion {

	public InliningVariableSuggestion(Warning warning) {
		super(warning);
	}

	@Override
	public String message() {
		return "変数をインライン化";
	}

}
