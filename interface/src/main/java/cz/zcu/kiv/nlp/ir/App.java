package cz.zcu.kiv.nlp.ir;

import java.io.FileNotFoundException;
import java.util.*;

import cz.zcu.kiv.nlp.ir.exceptions.QueryParserException;
import cz.zcu.kiv.nlp.ir.io.LoadData;
import cz.zcu.kiv.nlp.ir.trec.Index;
import cz.zcu.kiv.nlp.ir.trec.data.Document;
import cz.zcu.kiv.nlp.ir.trec.data.Result;

public class App {

	public static final String QUERY = "vláda sestavila nový návrh";

	public static void main(String [] args) throws FileNotFoundException, QueryParserException {

		// load data and stopwords
		final long startLoadData = System.currentTimeMillis();
		List<Document> documents = LoadData.loadData();

		// TIME - Load data
		final long loadData = System.currentTimeMillis();
		System.out.println("Time - load data: " + ((loadData - startLoadData) / 1000) / 60 + " minut" +
				" " + ((loadData - startLoadData) / 1000) % 60 + " sekund " + ((loadData - startLoadData) % 1000) + " milisekund" );

		Index index = new Index();
		index.index(documents);
		List<Result> hits = index.search(QUERY);

		for (Result hit : hits) {
			System.out.println(hit.toString(""));
		}

		// TODO - analyzation for czech lang.
		// TODO - implamantation for NOT query - check the computing of weight, cut values in maps by NOT configuration
	}
	
}
// TODO - tf idf => 1 + log ([kolikrat se vyskytl v dokumentu]) *
// TODO - log(1 + [kolik dokumentu je celkem]/ ([v kolika dokumentech byl term] + 1))
