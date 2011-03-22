package fuser;

import fuser.tokenizer.TokenList;

public class Parameter {

	protected String name;
	
	public Parameter(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	public static Parameter getParameter(TokenList tokens) {
		if (!looksLikeAParameter(tokens)) return null;
		Parameter parameter = new Parameter(tokens.currentToken());
		tokens.consume();
		return parameter;
	}

	private static boolean looksLikeAParameter(TokenList tokens) {
		return tokens.currentTokenMatches("\\[\\w+\\]|'\\w+'|\"\\w+\"|\\w+");
	}
}
