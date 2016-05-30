package cz.zcu.kiv.nlp.ir.ranking;

import cz.zcu.kiv.nlp.ir.trec.data.Document;

import java.util.*;

import static cz.zcu.kiv.nlp.ir.searching.Search.queryPreprocessing;

/**
 * Class for computing rank
 */
public class Ranker {

    /** Set of results */
    private final Set<String> RESULTS;

    /** Map of documents */
    private final Map<String, Document> DOCUMENTS;

    /** Search query */
    private final String QUERY;

    /** Map with occurs term */
    private Map<String, Set<String>> docsContainsTerm = new HashMap<String, Set<String>>();

    /**
     * Constructor
     * @param documents documents
     * @param resultDocs result documents
     * @param query searching query
     */
    public Ranker (Map<String, Document> documents, Set<String> resultDocs, String query) {
        this.QUERY = query;
        this.RESULTS = resultDocs;
        this.DOCUMENTS = documents;
    }

    /**
     * Compute tf-idf for query
     * @return vector with tf-idf
     */
    public Vector<Double> weightTfIdfQuery () {
        Map<String, Integer> termFrequencyInQuery = new HashMap<String, Integer>();
        Vector<Double> weightVector = new Vector<Double>();

        String [] queryParse = queryPreprocessing(QUERY);
        for (String word : queryParse) {
            if (!word.equals("AND") && !word.equals("OR") && !word.equals("NOT")) {
                termFrequencyInQuery.put(word.toLowerCase(), 0);
            }
        }

        termFrequencyInQuery = removeBooleanOperators(termFrequencyInQuery);

        // word frequency
        for (String word : queryParse) {
            Integer f = termFrequencyInQuery.get(word);
            if (f != null) {
                termFrequencyInQuery.put(word, f + 1);
            }
        }

        String [] terms = termFrequencyInQuery.keySet().toArray(new String [termFrequencyInQuery.keySet().size()]);
        Arrays.sort(terms);

        for (String term : terms) {

            double termInDocs = docsContainsTerm.get(term).size();
            double idfTerm = Math.log(1 + RESULTS.size() / (termInDocs + 1));
            double tfTerm = 1 + Math.log(termFrequencyInQuery.get(term));

            weightVector.add(tfTerm * idfTerm);

        }

        return weightVector;
    }

    /**
     * Compute tf-idf for documents
     * @return vector with tf-idf
     */
    public Map<String, Vector<Double>> weightTfIdfDocs() {
        Map<String, Map<String, Integer>> termFrequencyInDocs = new HashMap<String, Map<String, Integer>>();

        for (String docId : RESULTS) {

            termFrequencyInDocs.put(docId, new HashMap<String, Integer>());
            Map<String, Integer> frequency = termFrequencyInDocs.get(docId);

            String [] queryParse = queryPreprocessing(QUERY);
            for (String word : queryParse) {
                if (!word.equals("AND") && !word.equals("OR") && !word.equals("NOT")) {
                    frequency.put(word.toLowerCase(), 0);
                    docsContainsTerm.put(word.toLowerCase(), new HashSet<String>());
                }
            }

            frequency = removeBooleanOperators(frequency);

            Document doc = DOCUMENTS.get(docId);
            String fullText = doc.getText() + " " +
                            doc.getTitle() + " " +
                            doc.getLocations() + " " +
                            doc.getDate() + " " +
                            doc.getTags();

            String [] textArray = queryPreprocessing(fullText);
            Set<String> textSet = new HashSet<String>(Arrays.asList(textArray));

            // word frequency
            for (String word : textSet) {
                Integer f = frequency.get(word);
                if (f != null) {
                    frequency.put(word, f + 1);
                }
            }

            // which docs contains term
            for (Map.Entry<String, Integer> term : frequency.entrySet()) {
                if (term.getValue() != 0) {
                    docsContainsTerm.get(term.getKey()).add(doc.getId());
                }
            }
        }

        // howm many times doc contains term - termFrequencyInDocs;
        // which docs contains term - docsContainsTerm
        int sumaDocs = RESULTS.size();

        // call method for computing tf_idf
        return computeTfIdf(termFrequencyInDocs, sumaDocs);
    }

    /**
     * Compute angle merging query and documents tf-idf vectors
     * @param termFrequencyInDocs term frequency in doc
     * @param sumaDocs count of documents
     * @return computed tf-idf
     */
    private Map<String, Vector<Double>> computeTfIdf (Map<String, Map<String,
                                                        Integer>> termFrequencyInDocs, int sumaDocs) {

        Map<String, Vector<Double>> tfIdf_vectors = new HashMap<String, Vector<Double>>();

        for (Map.Entry<String, Map<String, Integer>> doc : termFrequencyInDocs.entrySet()) {

            // Sort docs IDs
            String [] terms = docsContainsTerm.keySet().toArray(new String [docsContainsTerm.keySet().size()]);
            Arrays.sort(terms);

            Vector<Double> tfidf = new Vector<Double>();
            for (String term : terms) {

                if (doc.getValue().containsKey(term)) {

                    Double tfTerm = 0.0;
                    Double idfTerm;

                    double termFreqInDoc = doc.getValue().get(term);
                    double termInDocs = docsContainsTerm.get(term).size();

                    if (termInDocs == 0) {
                        termInDocs = 1;
                    }

                    idfTerm = Math.log(1 + sumaDocs / (termInDocs + 1));

                    if (termFreqInDoc != 0) {
                        tfTerm = 1 + Math.log(termFreqInDoc);
                    }

                    // compute tf-idf
                    // save value to the Vector
                    tfidf.add(tfTerm * idfTerm);
                }

            }

            tfIdf_vectors.put(doc.getKey(), tfidf);
        }

        return tfIdf_vectors;

    }

    /**
     * Remove boolean operators from query
     * @param frequency map with term frequency
     * @return updated map
     */
    private Map<String, Integer> removeBooleanOperators (Map<String, Integer> frequency) {

        if (frequency.containsKey("AND")) {
            frequency.remove("AND");
        }
        if (frequency.containsKey("OR")) {
            frequency.remove("OR");
        }
        if (frequency.containsKey("NOT")) {
            frequency.remove("NOT");
        }

        return frequency;
    }

}
