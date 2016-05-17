package cz.zcu.kiv.nlp.ir.preprocessing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.zcu.kiv.nlp.ir.trec.data.Document;

public class Preprocessing {

	static Stemming stemmer = new CzechStemmerAgressive();

    public static Set<String> run(List<Document> docs, Set<String> stopwords) {
        
    	Set<String> words = new HashSet<String>();
        long numberOfWords = 0;
        long numberOfChars = 0;
        long numberOfDocuments = 0;
        for (Document doc : docs) {
            numberOfDocuments++;
            String line = doc.getText();

            // to lower case
            line = line.toLowerCase();
            // remove accents before stemming
            line = Tokenizer.removeAccents(line);

            for (String token : Tokenizer.tokenize(line, Tokenizer.defaultRegex)) {
            	// do stemming
            	token = stemmer.stem(token);
            	// remove accents after stemming
            	token = Tokenizer.removeAccents(token);

            	numberOfWords++;
                numberOfChars += token.length();
                words.add(token);
            }
        }
        System.out.println("numberOfWords: " + numberOfWords);
        System.out.println("numberOfUniqueWords: " + words.size());
        System.out.println("numberOfDocuments: " + numberOfDocuments);
        System.out.println("average document char length: " + numberOfChars / (0.0 + numberOfDocuments));
        System.out.println("average word char length: " + numberOfChars / (0.0 + numberOfWords));


        Object[] a = words.toArray();
        Arrays.sort(a);
        //System.out.println(Arrays.toString(a));
        
        return words;
    }
	
}
