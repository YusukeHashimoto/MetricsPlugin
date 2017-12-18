package warning.suggestion;

import warning.Warning;

public class SplitClassSuggestion extends Suggestion {

	public SplitClassSuggestion(Warning warning) {
		super(warning);
	}

	@Override
	public String message() {
		return "クラスを分割する";
	}

}
