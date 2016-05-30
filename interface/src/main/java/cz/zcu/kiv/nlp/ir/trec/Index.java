package cz.zcu.kiv.nlp.ir.trec;

import cz.zcu.kiv.nlp.ir.exceptions.QueryParserException;
import cz.zcu.kiv.nlp.ir.indexing.TermInfo;
import cz.zcu.kiv.nlp.ir.preprocessing.Preprocessing;
import cz.zcu.kiv.nlp.ir.ranking.Evaluator;
import cz.zcu.kiv.nlp.ir.ranking.Ranker;
import cz.zcu.kiv.nlp.ir.searching.BooleanParser;
import cz.zcu.kiv.nlp.ir.searching.ClassicParser;
import cz.zcu.kiv.nlp.ir.searching.Search;
import cz.zcu.kiv.nlp.ir.trec.data.Document;
import cz.zcu.kiv.nlp.ir.trec.data.Result;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author tigi
 */

public class Index implements Indexer, Searcher {

    private Map<String, TermInfo> DICTIONARY;
    private Map<String, Document> docsMap;

    public void index(List<Document> documents) throws FileNotFoundException {
        docsMap = new HashMap<String, Document>();
        for (Document doc : documents) docsMap.put(doc.getId(), doc);

        this.DICTIONARY = Preprocessing.run(docsMap);

    }

    public List<Result> search(String query) throws QueryParserException {
        // create searcher and do boolean searching
        // searching with boolean expressions
        final long startQueryResults = System.currentTimeMillis();
        Search search = new Search(DICTIONARY);

        ClassicParser cp;
        BooleanParser bp;
        Set<String> results;

        if (!query.contains("AND") && !query.contains("OR") && !query.contains("NOT")) {
            cp = new ClassicParser(search);
            results = cp.buildResults(query);
        } else {
            bp = new BooleanParser(search);
            results = (bp).buildResults(query);
        }

        // TIME - Builds results of query

        // ranking data
//        final long startIndexing = System.currentTimeMillis();
        Ranker ranker = new Ranker(docsMap, results, query);
        Map<String, Vector<Double>> resultsWeight = ranker.weightTfIdfDocs();
        Vector<Double> queryWeight = ranker.weightTfIdfQuery();

        // TIME - Ranking
//        final long indexing = System.currentTimeMillis();
//        System.out.println("Time - ranking: " + ((indexing - startIndexing) / 1000) / 60 + " minut" +
//                " " + ((indexing - startIndexing) / 1000) % 60 + " sekund " + ((indexing - startIndexing) % 1000) + " milisekund"  );

        // evaluation
//        final long startEvaluation = System.currentTimeMillis();
        Evaluator eval = new Evaluator(resultsWeight, queryWeight);
        List<Result> hits = eval.getAllSortedHits();

        // TIME - Evaluation
//        final long evaluation = System.currentTimeMillis();
//        System.out.println("Time - evaluation: " + ((evaluation - startEvaluation) / 1000) / 60 + " minut" +
//                " " + ((evaluation - startEvaluation) / 1000) % 60 + " sekund " + ((evaluation - startEvaluation) % 1000) + " milisekund"  );

        final long queryResults = System.currentTimeMillis();
        System.out.println("Time - dotaz: " + ((queryResults - startQueryResults) / 1000) / 60 + " minut" +
                " " + ((queryResults - startQueryResults) / 1000) % 60 + " sekund " + ((queryResults - startQueryResults) % 1000) + " milisekund" );

        return hits;
    }
}
