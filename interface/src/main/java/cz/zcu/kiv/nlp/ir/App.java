package cz.zcu.kiv.nlp.ir;

import cz.zcu.kiv.nlp.ir.statistics.Preprocessing;
import cz.zcu.kiv.nlp.ir.statistics.Statistics;

public class App {

	public static void main(String [] args) {

		// TODO - load data
		
		// TODO - do preprocessing, stop words, stemming, lemmatization
		Statistics stats = new Preprocessing();
		stats.RunStatistics(args[0]);
		
		// TODO - indexing data, analyzation for czech lang., save/load index, searching with boolean expressions
		
	}
	
}
