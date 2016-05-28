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
		Map<String, Document> documents = LoadData.loadData();

		// do preprocessing, take out stop words, do stemming, do lemmatization
		// do postings lists and dictionary
		Map<String, TermInfo> dictionary = Preprocessing.run(documents,
				new HashSet<String>(IOUtils.readLines(new FileInputStream(new File("./stopwords/stopwords.txt")))));

		// create searcher and do boolean searching
		// searching with boolean expressions
		Searcher search = new Searcher(dictionary);
		ParserEvaluator pe = new ParserEvaluator(search);
		Set<String> results = (pe).buildResults(QEURY);

		// indexing data
		Map<String, Vector<Double>> tfidf_vectors = (new Ranker(documents, results, QEURY)).weightTfIdfDocs();

		// TODO - analyzation for czech lang.
	}
	
}
// TODO - tf idf => 1 + log ([kolikrat se vyskytl v dokumentu]) *
// TODO - log(1 + [kolik dokumentu je celkem]/ ([v kolika dokumentech byl term] + 1))
