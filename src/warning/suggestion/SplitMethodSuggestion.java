package warning.suggestion;

import warning.Warning;

public class SplitMethodSuggestion extends Suggestion {

	public SplitMethodSuggestion(Warning warning) {
		super(warning);
	}

	@Override
	public String message() {
		return "メソッドを分割";
	}

}
