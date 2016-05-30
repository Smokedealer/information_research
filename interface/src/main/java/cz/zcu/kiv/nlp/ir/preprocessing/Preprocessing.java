package cz.zcu.kiv.nlp.ir.preprocessing;

import java.util.*;

import cz.zcu.kiv.nlp.ir.indexing.TermInfo;
import cz.zcu.kiv.nlp.ir.trec.data.Document;

/**
 * Class represents preprocessing for loaded documents
 *
 * @author Jakub Zíka
 * @cersion 1.3
 */
public class Preprocessing {

	/** Czech stemmer */
	static Stemming stemmer = new CzechStemmerAgressive();

	/**
	 * Method run preprocessing on loadid dates with stopwords
	 * @param docs loaded documents
	 * @param stopwords czech stopwords
     * @return dictionary
     */
    public static HashMap<String, TermInfo> run(Map<String, Document> docs, Set<String> stopwords) {
        
        HashMap<String, TermInfo> dictionary = new HashMap<String, TermInfo>();

		for (Map.Entry<String, Document> entry : docs.entrySet()) {
            String line = entry.getValue().getTitle();
			line += " " + entry.getValue().getTags();

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

    /**
	 * Method does control on tokens
	 * @param dictionary dictionary
	 * @param token token
	 * @param docID document ID
	 * @param stopwords stopwords
     * @return hash-map dictionary
     */
    public static HashMap<String, TermInfo> controlToken(HashMap<String, TermInfo> dictionary, String token, String docID, Set<String> stopwords) {
    	
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
