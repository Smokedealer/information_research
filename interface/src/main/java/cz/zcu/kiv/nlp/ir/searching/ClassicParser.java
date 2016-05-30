package cz.zcu.kiv.nlp.ir.searching;

import cz.zcu.kiv.nlp.ir.exceptions.QueryParserException;
import cz.zcu.kiv.nlp.ir.indexing.TermInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Instances of this classes represents classic parser and searcher for queries
 *
 * @author Jakub Zika
 * @version 1.0
 */
public class ClassicParser implements Parser {

    /** Searcher */
    private final Search SEARCH;

    /** Temporary results */
    private final List<List<TermInfo>> TMP_REUSLTS = new ArrayList<List<TermInfo>>();

    /**
     * Constructor
     * @param search instace of searcher
     */
    public ClassicParser (Search search) {
        this.SEARCH = search;
    }

    /**
     * Build all results to one list
     * @param query input query
     * @return merged results to one
     * @throws QueryParserException query exception
     */
    public Set<String> buildResults (String query) throws QueryParserException {
        String [] tmp = query.split(" ");

        for (String term : tmp) {
            List<TermInfo> e = this.SEARCH.search(term);
            if (!e.isEmpty()) {
                TMP_REUSLTS.add(e);
            }
        }

        List<TermInfo> results = new ArrayList<TermInfo>();
        for (List<TermInfo> list : this.TMP_REUSLTS) {
            results.addAll(list);
        }

        return mergeResults(results);
    }

    /**
     * Do merge on results
     * @param results results
     * @return set with original documents IDs
     */
    public Set<String> mergeResults(List<TermInfo> results) {
        Set<String> finalSetOfDocsID = new HashSet<String>();
        for (TermInfo term : results) finalSetOfDocsID.addAll(term.getPostings());
        return finalSetOfDocsID;
    }


}
