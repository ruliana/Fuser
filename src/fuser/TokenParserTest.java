package fuser;

import java.util.List;

import org.junit.Test;
import static junit.framework.Assert.*;

public class TokenParserTest {

	@Test
	public void basicTokenization() throws Exception {
		TokenParser parser = new TokenParser();
		parser.define("\\[\\w+\\]", "FIELD");
		parser.define("'\\w+'", "STRING");
		parser.define("\\w+", "IDENTIFIER");
		
		List<Token> result = parser.tokenize("this 'is' a [test]");
		assertEquals("[(this :IDENTIFIER), ('is' :STRING), (a :IDENTIFIER), ([test] :FIELD)]", result.toString());
	}
	
	@Test
	public void preservingSpacing() {
		TokenParser parser = new TokenParser();
		parser.define("\\w+", "SOMETHING");
		parser.define("\\S+", "SYMBOL");
		parser.define("\\s+", "SPACING");
		
		List<Token> result = parser.tokenize("this is \na\r\ntest\tcapiche?   HAH!");
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
