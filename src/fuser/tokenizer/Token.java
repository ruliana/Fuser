package fuser.tokenizer;

// IMMUTABLE
public class Token {

	private final String value;
	private final String label;

	public Token(String value, String label) {
		this.value = value;
		this.label = label;
	}
	
	public String getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}
	
	@Override
	public String toString() {
		return "(" + getValue() + " :" + getLabel() + ")";
	}
}
