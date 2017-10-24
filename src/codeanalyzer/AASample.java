package codeanalyzer;

import java.util.ArrayList;

public class AASample {
	void main() {
		new ArrayList().stream().filter(x -> x.equals(x)).forEach(v -> System.out.println(v));
	}
}
