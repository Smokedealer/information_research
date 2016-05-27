package cz.zcu.kiv.nlp.ir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import cz.zcu.kiv.nlp.ir.Exceptions.QueryParserException;
import cz.zcu.kiv.nlp.ir.IO.LoadData;
import cz.zcu.kiv.nlp.ir.indexing.TermInfo;
import cz.zcu.kiv.nlp.ir.preprocessing.Preprocessing;
import cz.zcu.kiv.nlp.ir.searching.ParserEvaluator;
import cz.zcu.kiv.nlp.ir.searching.Searcher;
import cz.zcu.kiv.nlp.ir.trec.IOUtils;
import cz.zcu.kiv.nlp.ir.trec.data.Document;

public class App {

	public static void main(String [] args) throws FileNotFoundException, QueryParserException {

		// load data and stopwords
		List<Document> documents = LoadData.loadData();
		List<String> words = IOUtils.readLines(new FileInputStream(new File("./stopwords/stopwords.txt")));
		Set<String> stopwords = new HashSet<String>(words);
		
		// do preprocessing, take out stop words, do stemming, do lemmatization
		// do postings lists and dictionary
		Map<String, TermInfo> dictionary = Preprocessing.run(documents, stopwords);
//		List<String> sortingTerms = new ArrayList<String>(dictionary.keySet()); // TODO - sort
//		Collections.sort(sortingTerms);

		// create searcher and do boolean searching
		Searcher search = new Searcher(dictionary);
		ParserEvaluator parser = new ParserEvaluator(search);
		List<TermInfo> results = parser.buildResults("(ahoj AND nazdar) OR dobry den");

		System.out.println("Konec");

		// TODO - only for test
//		ParserEvaluator parser = new ParserEvaluator(searcher);
//		String query = "(auto AND pes kočka) OR dům";
//		String evalResult = parser.evaluate(query);

		// TODO - indexing data, analyzation for czech lang., save/load index, searching with boolean expressions
		
	}
	
}
