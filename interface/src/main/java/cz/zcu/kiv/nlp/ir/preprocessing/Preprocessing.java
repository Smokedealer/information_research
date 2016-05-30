package cz.zcu.kiv.nlp.ir.preprocessing;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import cz.zcu.kiv.nlp.ir.indexing.TermInfo;
import cz.zcu.kiv.nlp.ir.trec.data.Document;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * Class represents preprocessing for loaded documents
 *
 * @author Jakub Zika
 * @version 1.3
 */
public class Preprocessing {

	/**
	 * Method run preprocessing on loadid dates with stopwords
	 * @param docs loaded documents
     * @return dictionary
     */
    public static HashMap<String, TermInfo> run(Map<String, Document> docs) {

		CzechAnalyzer analyzer = new CzechAnalyzer(CzechAnalyzer.getDefaultStopSet());
        HashMap<String, TermInfo> dictionary = new HashMap<String, TermInfo>();

		for (Map.Entry<String, Document> entry : docs.entrySet()) {
            String line = entry.getValue().getTitle();
			line += " " + entry.getValue().getText();
			line += " " + entry.getValue().getTags();

			List<String> result = new ArrayList<String>();
			try {
				TokenStream stream  = analyzer.tokenStream(null, new StringReader(line));
				stream.reset();
				while (stream.incrementToken()) {
					result.add(stream.getAttribute(CharTermAttribute.class).toString());
				}
				stream.close();
			} catch (IOException e) {
				// not thrown b/c we're using a string reader...
				throw new RuntimeException(e);
			}

            for (String token : result) {
            	controlToken(dictionary, token, entry.getValue().getId());
            }
        }

        return dictionary;
    }

    /**
	 * Method does control on tokens
	 * @param dictionary dictionary
	 * @param token token
	 * @param docID document ID
     * @return hash-map dictionary
     */
    public static HashMap<String, TermInfo> controlToken(HashMap<String, TermInfo> dictionary, String token, String docID) {

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
