package cz.zcu.kiv.nlp.ir;

import java.io.FileNotFoundException;
import java.util.HashMap;

import cz.zcu.kiv.nlp.ir.indexing.TermInfo;
import cz.zcu.kiv.nlp.ir.searching.ParserEvaluator;

public class App {

	public static void main(String [] args) throws FileNotFoundException {

		// load data and stopwords

//		List<Document> documents = LoadData.loadData();
//		List<String> words = IOUtils.readLines(new FileInputStream(new File("./stopwords/stopwords.txt")));
//		Set<String> stopwords = new HashSet<String>(words);
		
		// do preprocessing, stop words, stemming, lemmatization
		// do postings lists and dictionary

//		Map<String, TermInfo> dictionary = Preprocessing.run(documents, stopwords);
//		List<String> sortingTerms = new ArrayList<String>(dictionary.keySet()); // TODO - sort
//		Collections.sort(sortingTerms);

		// TODO - only for test
		ParserEvaluator parser = new ParserEvaluator(new HashMap<String,TermInfo>());
		String query = "(auto AND pes kočka) OR dům";
		String evalResult = parser.evaluate(query);


		// save dictionary indexes;
//		SaveLoadIndexes.saveIndexes(dictionary);
//		HashMap<String, TermInfo> dictionary = SaveLoadIndexes.loadIndexes();
//		System.out.println("Size of dictionary: " + dictionary.keySet().size());
		
		// TODO - indexing data, analyzation for czech lang., save/load index, searching with boolean expressions
		
	}
	
}
