package warning.suggestion;

import warning.Warning;

public class UsePolymorphismSuggestion extends Suggestion {

	public UsePolymorphismSuggestion(Warning warning) {
		super(warning);
	}

	@Override
	public String message() {
		return "条件分岐をポリモーフィズムで置き換える";
	}

}
