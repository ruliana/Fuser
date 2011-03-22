package fuser.tokenizer;

import java.util.List;

public class TokenList {

	private int pos;
	private final List<Token> tokens;

	public TokenList(List<Token> list){
		this.pos = 0;
		this.tokens = list;
	}

	public TokenList consume() {
		return consume(1);
	}

	public TokenList consume(int add) {
		pos += add;
		return this;
	}

	public int currentPosition() {
		return pos;
	}

	public int size() {
		return tokens.size();
	}

	public List<Token> toList() {
		return tokens;
	}

	private String getToken(int atPosition) {
		if (atPosition < 0 || atPosition >= tokens.size()) return "";
		return tokens.get(atPosition).getValue();
	}

	public String currentToken() {
		return getToken(pos);
	}

	public boolean currentTokenIs(String string) {
		return currentToken().equals(string);
	}

	public boolean currentTokenMatches(String regularExpression) {
		return currentToken().matches(regularExpression);
	}

	public String nextToken() {
		return getToken(pos + 1);
	}

	public boolean nextTokenIs(String string) {
		return nextToken().equals(string);
	}

	public boolean nextTokenMatches(String regularExpression) {
		return nextToken().matches(regularExpression);
	}

	public boolean hasNextToken() {
		return pos < tokens.size();
	}

	@Override
	public String toString() {
		return tokens.toString();
	}
}
