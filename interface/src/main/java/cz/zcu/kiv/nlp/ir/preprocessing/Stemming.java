package cz.zcu.kiv.nlp.ir.preprocessing;

/**
 * Interface for representation stemmer method.
 * 
 * @author Jakub Zíka
 * @version 1.0
 */
public interface Stemming {

    /**
     * Method do stemming on input
     * @param input word for stemming
     * @return word after stemming
     */
    String stem(String input);
	
}
