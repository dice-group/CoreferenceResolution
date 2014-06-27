package org.aksw.sandbox.nlp;

import java.util.List;

import com.clearnlp.nlp.NLPGetter;
import com.clearnlp.reader.AbstractReader;
import com.clearnlp.tokenization.AbstractTokenizer;

public class Tokenizer {

	private AbstractTokenizer tok;

	public Tokenizer() {
		String language = AbstractReader.LANG_EN;
		this.tok = NLPGetter.getTokenizer(language);
	}

	public String[] tokenize(String document) {
		List<String> list = tok.getTokens(document);
		return list.toArray(new String[list.size()]);
	}

	public static void main(String args[]) {
		String document = "Which buildings in art deco style did Shreve, Lamb and Harmon design?";
		Tokenizer tokenizer = new Tokenizer();
		String[] tokens = tokenizer.tokenize(document);
		for (String token : tokens) {
			System.out.println(token);
		}
	}

}
