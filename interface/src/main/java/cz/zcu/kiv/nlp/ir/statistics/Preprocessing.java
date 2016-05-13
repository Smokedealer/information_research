package cz.zcu.kiv.nlp.ir.statistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.zcu.kiv.nlp.ir.preprocessing.CzechStemmerAgressive;
import cz.zcu.kiv.nlp.ir.preprocessing.CzechStemmerLight;
import cz.zcu.kiv.nlp.ir.preprocessing.Stemming;
import cz.zcu.kiv.nlp.ir.preprocessing.Tokenizer;
import cz.zcu.kiv.nlp.ir.trec.IOUtils;

public class Preprocessing implements Statistics{

	static Stemming stemmer = new CzechStemmerAgressive();

    public void RunStatistics(String file) {
    	
    	// get words for preprocessing from file
    	List<String> textForTokenizing = new ArrayList<String>();
    	try {
    		File textFromFile = new File(file);
    		textForTokenizing.addAll(IOUtils.readLines(new FileInputStream(textFromFile)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	
    	// create docs
    	final ArrayList<String> documents = new ArrayList<String>();
    	for(int i = 0; i < textForTokenizing.size(); i++) {
    		final String[] tokenize = split(textForTokenizing.get(i), " ");
    		Arrays.sort(tokenize);

    		for (String document : split(textForTokenizing.get(i), "\n")) {
    			documents.add(document);
    		}
    	}

//        wordsStatistics(documents, " ", false, null, false, false);

        System.out.println();
        System.out.println("-------------------------------");
        System.out.println();

        wordsStatistics(documents, "\\S+", false, null, false, false, false);

        System.out.println();
        System.out.println("------------LOWERCASE---------------");
        System.out.println();
        wordsStatistics(documents, "\\S+", false, null, false, false, true);

        System.out.println();
        System.out.println("----------STEM Light------------------");
        System.out.println();
        stemmer = new CzechStemmerLight();
        wordsStatistics(documents, "\\S+", true, null, false, false, true);

        System.out.println();
        System.out.println("----------STEM Agressive------------------");
        System.out.println();

        wordsStatistics(documents, "\\S+", true, null, false, false, true);

        System.out.println();
        System.out.println("-----------ACCENTS---------------");
        System.out.println();

        wordsStatistics(documents, "\\S+", true, null, false, true, true);


        System.out.println();
        System.out.println("-----------REGEX---------------");
        System.out.println();
        wordsStatistics(documents, Tokenizer.defaultRegex, true, null, false, true, true);

    }



    public static void wordsStatistics(List<String> lines, String tokenizeRegex, boolean stemm, Set<String> stopwords, boolean removeAccentsBeforeStemming, boolean removeAccentsAfterStemming, boolean toLowercase) {
        Set<String> words = new HashSet<String>();
        long numberOfWords = 0;
        long numberOfChars = 0;
        long numberOfDocuments = 0;
        for (String line : lines) {
            numberOfDocuments++;

            if (toLowercase) {
                line = line.toLowerCase();
            }
            if (removeAccentsBeforeStemming) {
                line = Tokenizer.removeAccents(line);
            }
            for (String token : tokenize(line, tokenizeRegex)) {
//            for (String token : BasicTokenizer.tokenize(line, BasicTokenizer.defaultRegex)) {
                if (stemm) {
                    token = stemmer.stem(token);
                }
                if (removeAccentsAfterStemming) {
                    token = Tokenizer.removeAccents(token);
                }
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
        System.out.println(Arrays.toString(a));
    }

    private static String[] split(String line, String regex) {
        return line.split(regex);
    }

    private static String[] tokenize(String line, String regex) {
        return Tokenizer.tokenize(line, regex);
    }
	
}
