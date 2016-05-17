package cz.zcu.kiv.nlp.ir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.zcu.kiv.nlp.ir.IO.LoadData;
import cz.zcu.kiv.nlp.ir.preprocessing.Preprocessing;
import cz.zcu.kiv.nlp.ir.trec.IOUtils;
import cz.zcu.kiv.nlp.ir.trec.data.Document;

public class App {

	public static void main(String [] args) throws FileNotFoundException {

		// TODO - load data and stopwords
		List<Document> documents = LoadData.loadData();
		List<String> words = IOUtils.readLines(new FileInputStream(new File("./stopwords/stopwords.txt")));
		Set<String> stopwords = new HashSet<String>(words);
		
		// TODO - do preprocessing, stop words, stemming, lemmatization = return unique words
		Set<String> dictionary = Preprocessing.run(documents, stopwords);
		System.out.println("Count of dictionary: " + dictionary.size());
		
		// TODO - do postings lists for terms
		// each term has to have document frequncy and pointer to postings list
		
		// TODO - indexing data, analyzation for czech lang., save/load index, searching with boolean expressions
		
	}
	
}
