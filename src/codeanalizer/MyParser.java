package codeanalizer;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class MyParser {
	private String code;

	public MyParser(String code) {
		this.code = code;
	}

	/**
	 * 変数の寿命を行数で返す <br>
	 * 空行は読み飛ばすが， コメント行は1行として数える<br>
	 * エラーがあれば-1を返す
	 * 
	 * @param variable
	 * @return
	 */
	int lifeSpanOf(VariableDeclarationFragment variable) {
		int start = variable.getStartPosition();
		for(int i = 0, open = 0, close = 0, lines = 0; i < code.length(); i++) {
			switch(code.charAt(start + i)) {
			case '{':
				open++;
				break;
			case '}':
				close++;
				break;
			case '\n':
				lines++;
				break;
			}
			if(close > open) return lines;
		}
		return -1;
	}

	/**
	 * 文字列で与えられたコードから空行を削除して返す
	 * 
	 * @param code
	 * @return
	 */
	static String removeBlankLines(String code) {
		return removeMatchedLine(code, "^\\s*$");
	}

	/**
	 * 与えられたコードから正規表現regexに一致する行を削除して返す
	 * 
	 * @param code
	 * @param regex
	 * @return
	 */
	static String removeMatchedLine(String code, String regex) {
		return Arrays.asList(code.split("\n")).stream().filter(s -> !s.matches(regex))
				.collect(Collectors.joining("\n"));
	}

	/**
	 * 与えられたコードからコメント行を削除
	 * 
	 * @param code
	 * @return
	 */
	static String removeCommentLines(String code) {
		return removeMatchedLine(code, "^\\s*//.*");
	}

	static String format(String code) {
		return removeBlankLines(removeCommentLines(code));
	}
}
