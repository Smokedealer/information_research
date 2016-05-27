package cz.zcu.kiv.nlp.ir.searching;

import cz.zcu.kiv.nlp.ir.indexing.TermInfo;
import cz.zcu.kiv.nlp.ir.preprocessing.Tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Searcher {

    Map<String, TermInfo> dictionary = null;

    public Searcher (Map<String, TermInfo> dictionary) {
        this.dictionary = dictionary;
    }

    public List<TermInfo> search (String str) {
        String [] terms = str.split(Tokenizer.defaultRegex);
        List<TermInfo> searchResults = new ArrayList<TermInfo>();

        for (int i = 0; i < terms.length; i++) {
            if (dictionary.containsKey(terms[i])) {
                searchResults.add(dictionary.get(terms[i]));
            }
        }

        return searchResults;
    }
}