package fuser;

import java.util.List;

public class TokenList {

	private int pos;
	private final List<String> tokens;

	public TokenList(List<String> tokens){
		this.pos = 0;
		this.tokens = tokens;
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
	
	public List<String> toList() {
		return tokens;
	}
	
	private String getToken(int atPosition) {
		if (atPosition < 0 || atPosition >= tokens.size()) return "";
		return tokens.get(atPosition);
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
}
