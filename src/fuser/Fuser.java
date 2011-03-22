package fuser;

import static ginger.Regex.r;

import java.util.LinkedList;

import fuser.tokenizer.TokenList;

public class Fuser {

	private final String sqlString;

	public Fuser(String sqlString) {
		this.sqlString = sqlString;
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
		return new TokenList(r(sqlString).findAll("(\\[\\w+\\]|'\\w+'|\"\\w+\"|\\w+|\\S)"));
	}
}
