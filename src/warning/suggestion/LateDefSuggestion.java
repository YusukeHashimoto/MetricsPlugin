package warning.suggestion;

import warning.Warning;

public class LateDefSuggestion extends Suggestion {

	public LateDefSuggestion(Warning warning) {
		super(warning);
	}

	@Override
	public String message() {
		return "変数の宣言をするタイミングを遅くする";
	}

}
