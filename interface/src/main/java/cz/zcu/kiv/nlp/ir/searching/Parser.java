package cz.zcu.kiv.nlp.ir.searching;

import cz.zcu.kiv.nlp.ir.exceptions.QueryParserException;
import cz.zcu.kiv.nlp.ir.indexing.TermInfo;

import java.util.List;
import java.util.Set;

/**
 * Class has to implements these methods about parsing
 *
 * @author Jakub Zíka
 * @version 1.0
 */
public interface Parser {

    /**
     * Build all results to one list
     * @param query input query
     * @return merged results to one
     * @throws QueryParserException query exception
     */
    public abstract Set<String> buildResults(String query) throws QueryParserException;

    /**
     * Do merge on results
     * @param results results
     * @return set with original documents IDs
     */
    public abstract Set<String> mergeResults(List<TermInfo> results);

}
