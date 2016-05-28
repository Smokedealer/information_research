package cz.zcu.kiv.nlp.ir.preprocessing;

import java.util.*;

import cz.zcu.kiv.nlp.ir.indexing.TermInfo;
import cz.zcu.kiv.nlp.ir.trec.data.Document;

public class Preprocessing {

	static Stemming stemmer = new CzechStemmerAgressive();

    public static HashMap<String, TermInfo> run(Map<String, Document> docs, Set<String> stopwords) {
        
        HashMap<String, TermInfo> dictionary = new HashMap<String, TermInfo>();

		for (Map.Entry<String, Document> entry : docs.entrySet()) {
            String line = entry.getValue().getText();

            // to lower case
            line = line.toLowerCase();
            // remove accents before stemming
            line = Tokenizer.removeAccents(line);
			String[] tokenizeResult = Tokenizer.tokenize(line, Tokenizer.defaultRegex);

            for (String token : tokenizeResult) {
            	controlToken(dictionary, token, entry.getValue().getId(), stopwords);
            	
            	// do stemming
            	token = stemmer.stem(token);
            	controlToken(dictionary, token, entry.getValue().getId(), stopwords);
            	
            	// remove accents after stemming
            	token = Tokenizer.removeAccents(token);
            	controlToken(dictionary, token, entry.getValue().getId(), stopwords);
            }
        }
        
        return dictionary;
    }
    
    private static HashMap<String, TermInfo> controlToken(HashMap<String, TermInfo> dictionary, String token, String docID, Set<String> stopwords) {
    	
    	if (stopwords.contains(token)) {
    		// TOKEN IS FROM STOPWORDS
    		return null;
    	}

    	if (!dictionary.containsKey(token)) {
    		// TOKEN IS NEW
    		// create postings
    		Set<String> postings = new HashSet<String>();
    		postings.add(docID);
    		
    		// create term
    		TermInfo term = new TermInfo(token, 1, postings);    		
    		dictionary.put(token, term);
    		
    	} else {
    		// DICTIONARY CONTAINS TOKEN
    		TermInfo term = dictionary.get(token);
    		term.setCountNextValue();
    		term.getPostings().add(docID);
    		
    	}
    	
    	return dictionary;
    	
    }
	
}
