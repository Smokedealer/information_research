package cz.zcu.kiv.nlp.ir.searching;

import cz.zcu.kiv.nlp.ir.indexing.TermInfo;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * Instances of this class represents searcher in documents according term
 *
 * @author Jakub Zíka
 * @version 1.3
 */
public class Search {

    /** Dictionary */
    Map<String, TermInfo> dictionary = null;

    /**
     * Constructor
     * @param dictionary dictionary
     */
    public Search (Map<String, TermInfo> dictionary) {
        this.dictionary = dictionary;
    }

    /**
     * Method search terms in dictionary
     * @param str input term
     * @return list of results
     */
    public List<TermInfo> search (String str) {
        String [] terms = queryPreprocessing(str);
        List<TermInfo> searchResults = new ArrayList<TermInfo>();

        for (int i = 0; i < terms.length; i++) {
            if (dictionary.containsKey(terms[i])) {
                searchResults.add(dictionary.get(terms[i]));
            }
        }

        return searchResults;
    }

    /**
     * Preprocessing for input query
     * @param query input query
     * @return array of results after preprocessing
     */
    public static String [] queryPreprocessing (String query) {
        List<String> terms = new ArrayList<String>();

        List<String> result = new ArrayList<String>();
        CzechAnalyzer analyzer = new CzechAnalyzer(CzechAnalyzer.getDefaultStopSet());
        try {
            TokenStream stream  = analyzer.tokenStream(null, new StringReader(query));
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
            terms.add(token);
        }

        String [] termsArray = new String [terms.size()];
        termsArray = terms.toArray(termsArray);
        return termsArray;
    }
}