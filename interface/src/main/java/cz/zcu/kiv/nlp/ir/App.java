package cz.zcu.kiv.nlp.ir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import cz.zcu.kiv.nlp.ir.exceptions.QueryParserException;
import cz.zcu.kiv.nlp.ir.io.LoadData;
import cz.zcu.kiv.nlp.ir.indexing.TermInfo;
import cz.zcu.kiv.nlp.ir.preprocessing.Preprocessing;
import cz.zcu.kiv.nlp.ir.ranking.Ranker;
import cz.zcu.kiv.nlp.ir.searching.ParserEvaluator;
import cz.zcu.kiv.nlp.ir.searching.Searcher;
import cz.zcu.kiv.nlp.ir.trec.IOUtils;
import cz.zcu.kiv.nlp.ir.trec.data.Document;

public class App {

	public static final String QEURY = "hodnotil AND nove";

	public static void main(String [] args) throws FileNotFoundException, QueryParserException {

		// load data and stopwords
		final long startLoadData = System.currentTimeMillis();
		Map<String, Document> documents = LoadData.loadData();

		// TIME - Load data
		final long loadData = System.currentTimeMillis();
		System.out.println("Time - load data: " + ((loadData - startLoadData) / 1000) / 60 + " minut" +
				" " + ((loadData - startLoadData) / 1000) % 60 + " sekund " + ((loadData - startLoadData) % 1000) + " milisekund" );

		// do preprocessing, take out stop words, do stemming, do lemmatization
		// do postings lists and dictionary
		final long startCreateDictionary = System.currentTimeMillis();
		Map<String, TermInfo> dictionary = Preprocessing.run(documents,
				new HashSet<String>(IOUtils.readLines(new FileInputStream(new File("/home/dzejkob23/GIT/information_research/interface/stopwords/stopwords.txt")))));

		// TIME - Create dictionary
		final long createDictionary = System.currentTimeMillis();
		System.out.println("Time - create dictionary: " + ((createDictionary - startCreateDictionary) / 1000) / 60 + " minut" +
				" " + ((createDictionary - startCreateDictionary) / 1000) % 60 + " sekund " + ((createDictionary - startCreateDictionary) % 1000) + " milisekund" );

		// create searcher and do boolean searching
		// searching with boolean expressions
		final long startQueryResults = System.currentTimeMillis();
		Searcher search = new Searcher(dictionary);
		ParserEvaluator pe = new ParserEvaluator(search);
		Set<String> results = (pe).buildResults(QEURY);

		// TIME - Builds results of query
		final long queryResults = System.currentTimeMillis();
		System.out.println("Time - build query results: " + ((queryResults - startQueryResults) / 1000) / 60 + " minut" +
				" " + ((queryResults - startQueryResults) / 1000) % 60 + " sekund " + ((queryResults - startQueryResults) % 1000) + " milisekund" );

		// indexing data
		final long startIndexing = System.currentTimeMillis();
		Ranker ranker = new Ranker(documents, results, QEURY);
		Map<String, Vector<Double>> tfidf_vectors = ranker.weightTfIdfDocs();

		// TIME - Indexing
		final long indexing = System.currentTimeMillis();
		System.out.println("Time - indexing: " + ((indexing - startIndexing) / 1000) / 60 + " minut" +
				" " + ((indexing - startIndexing) / 1000) % 60 + " sekund " + ((indexing - startIndexing) % 1000) + " milisekund"  );

		// TODO - analyzation for czech lang.
		// TODO - implamantation for NOT query - check the computing of weight, cut values in maps by NOT configuration
	}
	
}
// TODO - tf idf => 1 + log ([kolikrat se vyskytl v dokumentu]) *
// TODO - log(1 + [kolik dokumentu je celkem]/ ([v kolika dokumentech byl term] + 1))
