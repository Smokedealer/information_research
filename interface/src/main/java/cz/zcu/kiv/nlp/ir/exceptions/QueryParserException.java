package cz.zcu.kiv.nlp.ir.exceptions;

/**
 * Class represents exception for parsing query
 *
 * @author Jakub Zíka
 * @version 1.0
 */
public class QueryParserException extends Exception {

    /**
     * Query exception
     * @param message error message
     */
    public QueryParserException(String message) {
        super(message);
    }

}
