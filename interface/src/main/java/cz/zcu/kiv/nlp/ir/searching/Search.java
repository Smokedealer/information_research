package cz.zcu.kiv.nlp.ir.searching;

import cz.zcu.kiv.nlp.ir.indexing.TermInfo;
import cz.zcu.kiv.nlp.ir.preprocessing.CzechStemmerAgressive;
import cz.zcu.kiv.nlp.ir.preprocessing.Stemming;
import cz.zcu.kiv.nlp.ir.preprocessing.Tokenizer;

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
        Stemming stemmer = new CzechStemmerAgressive();
        List<String> terms = new ArrayList<String>();
        String q = query.toLowerCase();
        q = Tokenizer.removeAccents(q);


        String[] tokenizeResult = Tokenizer.tokenize(q, Tokenizer.defaultRegex);
        for (String token : tokenizeResult) {
            terms.add(token);

            // do stemming
            token = stemmer.stem(token);
            terms.add(token);

            // remove accents after stemming
            token = Tokenizer.removeAccents(token);
            terms.add(token);
        }

        String [] termsArray = new String [terms.size()];
        termsArray = terms.toArray(termsArray);
        return termsArray;
    }
}