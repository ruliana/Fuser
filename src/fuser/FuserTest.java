package fuser;

import static ginger.Seq.s;
import static junit.framework.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class FuserTest {

	private Fuser fuser;

	@Before
	public void setUp() throws Exception {
		fuser = null;
	}

	@Test
	public void nothingToBeFound() throws Exception {
		fuser = new Fuser("SELECT * FROM tabela");
		assertTrue(fuser.functionList().isEmpty());

		fuser = new Fuser("SELECT abc AS xyz, def hij, klm FROM tabela");
		assertTrue(fuser.functionList().isEmpty());
	}

	@Test
	public void simpleFunctions() throws Exception {
		fuser = new Fuser("SELECT length(abc) FROM tabela");
		assertEquals(1, fuser.functionList().size());
		assertEquals("length(abc)", fuser.functionList().getFirst().toString());

		fuser = new Fuser("SELECT length(abc) FROM tabela WHERE substring(abc, 1, 3) = 'uga'");
		assertEquals(2, fuser.functionList().size());
		assertEquals("length(abc)", fuser.functionList().get(0).toString());
		assertEquals("substring(abc, 1, 3)", fuser.functionList().get(1).toString());
	}

	@Test
	public void decomposeFunctions() throws Exception {
		Function function;
		
		fuser = new Fuser("SELECT length(abc) FROM tabela");
		function = fuser.functionList().getFirst();
		assertEquals("length", function.getName());
		assertEquals(1, function.getParameters().size());
		assertEquals("abc", function.getParameters().get(0).toString());
		
		fuser = new Fuser("SELECT length(abc) FROM tabela WHERE substring(abc, 1, 3) = 'uga'");
		function = fuser.functionList().get(0);
		assertEquals("length", function.getName());
		assertEquals("abc", function.getParameters().get(0).toString());
		function = fuser.functionList().get(1);
		assertEquals("substring", function.getName());
		assertEquals("[abc, 1, 3]", function.getParameters().toString());
	}
	
	@Test
	public void nestedFunctions() throws Exception {
		fuser = new Fuser("SELECT substring(abc, 1, charindex(abc(123), 'a')) FROM tabela");
		assertEquals(1, fuser.functionList().size());
		
		Function firstFunction = fuser.functionList().get(0);
		Function innerFunction = (Function) firstFunction.getParameter(2);
		
		assertEquals("charindex", innerFunction.getName());
		assertEquals("'a'", innerFunction.getParameter(1).getName());
		
		Function innerInnerFunction = (Function) innerFunction.getParameter(0);
		assertEquals("abc", innerInnerFunction.getName());
		assertEquals("123", innerInnerFunction.getParameter(0).getName());
	}
	
	@Test
	public void bewareOfGratuitousParenthesis() throws Exception {
		fuser = new Fuser("SELECT * FROM (SELECT * FROM tabela WHERE a = 1 AND (b = 1 OR b = 2))");
		assertEquals(0, fuser.functionList().size());
	}
	
	@Test
	public void tokenizer() throws Exception {
		fuser = new Fuser("SELECT substring(abc, 1, charindex(abc, 'a')) FROM tabela");
		TokenList tokens = fuser.tokenizedSqlString();
		assertEquals(16, tokens.size());
		assertEquals(s("SELECT", "substring", "(", "abc", ",", "1", ",", "charindex", "(", "abc", ",", "'a'", ")", ")", "FROM", "tabela").toList(), tokens.toList());
	}
}
