package cz.zcu.kiv.nlp.ir.preprocessing;

/**
 * Interface for representation stemmer method.
 * 
 * @author Jakub Zíka
 * @date 12.5. 2016
 * @version 1.0
 */
public interface Stemming {

    String stem(String input);
	
}
