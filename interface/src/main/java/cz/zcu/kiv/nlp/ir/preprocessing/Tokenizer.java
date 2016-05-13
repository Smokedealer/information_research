package cz.zcu.kiv.nlp.ir.preprocessing;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class creates tokens from imported text.
 *
 * @author Jakub Zíka
 * @date 12.5. 2016
 * @version 1.0
 */
public class Tokenizer {

	/** Regular expression for create tokens. */
	public static final String defaultRegex = "(\\w+\\*\\w+)|(\\d{1,2}\\.\\d{1,2}\\.)|(\\d+[.,](\\d+)?)|([\\p{L}\\d]+)|(<.*?>)|([\\p{Punct}])";
	
	/**
	 * Create tokens.
	 * 
	 * @param text imported text
	 * @param regex regular expression
	 * @return
	 */
	public static String[] tokenize(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);

        ArrayList<String> words = new ArrayList<String>();

        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            words.add(text.substring(start, end));
        }

        String[] ws = new String[words.size()];
        ws = words.toArray(ws);

        return ws;
    }

	/**
	 * Removing accents.
	 * 
	 * @param text imported text
	 * @return upgraded word
	 */
    public static String removeAccents(String text) {
        return text == null ? null : Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    /**
     * Tokenization.
     * 
     * @param text imported text
     * @return word after tokenization
     */
    public String[] tokenize(String text) {
        return tokenize(text, defaultRegex);
    }
	
}
