package warning;

public class Suggestion {
	public static final int SPLIT_METHOD = 0;
	public static final int EXTRACT_CONDITIONS_AS_METHOD = 1;
	public static final int INLINING_VARIABLE = 2;

	private int type = 0;

	public Suggestion(int type) {
		this.type = type;
	}

	public String message() {
		return "message";
	}
}
