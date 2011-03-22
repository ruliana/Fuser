package fuser.tokenizer;

import static junit.framework.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class TokenizerTest {

	@Test
	public void basicTokenization() throws Exception {
		Tokenizer parser = new Tokenizer();
		parser.define("\\[\\w+\\]", "FIELD");
		parser.define("'\\w+'", "STRING");
		parser.define("\\w+", "IDENTIFIER");

		List<Token> result = parser.parse("this 'is' a [test]");
		assertEquals("[(this :IDENTIFIER), ('is' :STRING), (a :IDENTIFIER), ([test] :FIELD)]", result.toString());
	}

	@Test
	public void preservingSpacing() {
		Tokenizer parser = new Tokenizer();
		parser.define("\\w+", "SOMETHING");
		parser.define("\\S+", "SYMBOL");
		parser.define("\\s+", "SPACING");

		List<Token> result = parser.parse("this is \na\r\ntest\tcapiche?   HAH!");
		assertEquals(13, result.size());

		assertEquals(" \n", result.get(3).getValue());
		assertEquals("\r\n", result.get(5).getValue());
		assertEquals("\t", result.get(7).getValue());
		assertEquals("   ", result.get(10).getValue());
		assertEquals("SPACING", result.get(10).getLabel());
		assertEquals("!", result.get(12).getValue());
		assertEquals("SYMBOL", result.get(12).getLabel());
	}
}
