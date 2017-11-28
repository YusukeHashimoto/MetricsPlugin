package warning.suggestion;

import warning.Warning;

public class ExtractConditionsSuggestion extends Suggestion {

	public ExtractConditionsSuggestion(Warning warning) {
		super(warning);
	}

	@Override
	public String message() {
		return "条件式をメソッドに抽出";
	}

}
