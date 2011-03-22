package fuser;

import static ginger.Seq.s;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import fuser.tokenizer.TokenList;

public class Function extends Parameter {

	private static List<String> notAFunction = Arrays.asList("AND", "OR", "FROM", "JOIN", "ON");
	private List<Parameter> parameters;

	public Function(String name, Parameter... parameters) {
		super(name);
		this.parameters = new LinkedList<Parameter>(Arrays.asList(parameters));
	}

	@Override
	public String toString() {
		return name + "(" + s(parameters).join(", ") + ")";
	}

	public String getName() {
		return name;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public Parameter getParameter(int i) {
		return getParameters().get(i);
	}


	public static Function getFunction(TokenList tokens) {
		if (!looksLikeAFunction(tokens)) return null;
		
		Function function = new Function(tokens.currentToken());
		tokens.consume(2);
		function.getParameters(tokens);
		return function;
	}

	private static boolean looksLikeAFunction(TokenList tokens) {
		return tokens.currentTokenMatches("\\w+")
		    && tokens.nextTokenIs("(")
		    && !notAFunction.contains(tokens.currentToken());
	}

	private void getParameters(TokenList tokens) {
		while (tokens.hasNextToken()) {
			Function function = Function.getFunction(tokens);
			if (function != null) parameters.add(function);
			
			Parameter parameter = Parameter.getParameter(tokens);
			if (parameter != null) parameters.add(parameter);
			
			if (tokens.currentTokenIs(",")) {
				tokens.consume();
				continue;
			}
			
			if (tokens.currentTokenIs(")")) {
				tokens.consume();
				return;
			}
			
			tokens.consume();
		}
	}
}
