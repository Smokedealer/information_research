package cz.zcu.kiv.nlp.ir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.zcu.kiv.nlp.ir.IO.LoadData;
import cz.zcu.kiv.nlp.ir.IO.SaveLoadIndexes;
import cz.zcu.kiv.nlp.ir.indexing.TermInfo;
import cz.zcu.kiv.nlp.ir.preprocessing.Preprocessing;
import cz.zcu.kiv.nlp.ir.trec.IOUtils;
import cz.zcu.kiv.nlp.ir.trec.data.Document;

public class App {

	public static void main(String [] args) throws FileNotFoundException {

		// load data and stopwords

		List<Document> documents = LoadData.loadData();
		List<String> words = IOUtils.readLines(new FileInputStream(new File("./stopwords/stopwords.txt")));
		Set<String> stopwords = new HashSet<String>(words);
		
		// do preprocessing, stop words, stemming, lemmatization
		// do postings lists and dictionary

		Map<String, TermInfo> dictionary = Preprocessing.run(documents, stopwords);
		List<String> sortingTerms = new ArrayList<String>(dictionary.keySet()); // TODO - sort
		Collections.sort(sortingTerms);
		
		// save dictionary indexes;
//		SaveLoadIndexes.saveIndexes(dictionary);
//		HashMap<String, TermInfo> dictionary = SaveLoadIndexes.loadIndexes();
//		System.out.println("Size of dictionary: " + dictionary.keySet().size());
		
		// TODO - indexing data, analyzation for czech lang., save/load index, searching with boolean expressions
		
	}
	
}
