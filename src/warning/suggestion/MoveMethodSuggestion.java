package warning.suggestion;

import warning.Warning;

public class MoveMethodSuggestion extends Suggestion{

	public MoveMethodSuggestion(Warning warning) {
		super(warning);
	}

	@Override
	public String message() {
		return "メソッドを移動する";
	}

}
