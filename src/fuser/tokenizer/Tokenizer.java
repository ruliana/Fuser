package fuser.tokenizer;

import static ginger.Seq.s;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {

	private LinkedHashMap<String, String> definitions = new LinkedHashMap<String, String>();
	
	public void define(String regex, String label) {
		definitions.put(regex, label);
	}

	public List<Token> tokenize(String string) {
		List<Token> result = new LinkedList<Token>();
		Pattern pattern = Pattern.compile(allDefinitionsTogether());
		Matcher matcher = pattern.matcher(string);
		
		while (matcher.find()) {
			String tokenCandidate = matcher.group();
			for (Entry<String, String> entry : definitions.entrySet()) {
				if (tokenCandidate.matches(entry.getKey())) {
					result.add(new Token(tokenCandidate, entry.getValue()));
					break; // If find something, stop trying to classify it
				}
			}
		}
		return result;
	}

	private String allDefinitionsTogether() {
		return s(definitions.keySet()).join("|");
	}
}
