package fuser;

import java.util.LinkedList;

import fuser.tokenizer.TokenList;
import fuser.tokenizer.Tokenizer;

public class Fuser {

	private final String sqlString;
	private Tokenizer tokenizer;

	public Fuser(String sqlString) {
		this.sqlString = sqlString;

		initializeTokenizer();
	}

	private void initializeTokenizer() {
		tokenizer = new Tokenizer();
		tokenizer.define("\\[\\w+\\]", "FIELD");
		tokenizer.define("\"\\w+\"", "FIELD");
		tokenizer.define("'\\w+'", "STRING");
		tokenizer.define("\\w+", "SYMBOL");
		tokenizer.define("\\S", "PUNCTUATION");
	}

	public LinkedList<Function> functionList() {
		LinkedList<Function> result = new LinkedList<Function>();

		TokenList tokens = tokenizedSqlString();
		while (tokens.hasNextToken()) {
			Function function = Function.getFunction(tokens);
			if (function != null) result.add(function);
			tokens.consume();
		}
		return result;
	}

	protected TokenList tokenizedSqlString() {
		return tokenizer.tokenize(sqlString);
	}
}
