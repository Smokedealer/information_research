package cz.zcu.kiv.nlp.ir.ranking;

import cz.zcu.kiv.nlp.ir.preprocessing.Tokenizer;
import cz.zcu.kiv.nlp.ir.trec.data.Document;

import java.util.*;

/**
 * Created by dzejkob23 on 27.05.16.
 */
public class Ranker {


    private final Set<String> RESULTS;
    private final Map<String, Document> DOCUMENTS;
    private final String QUERY;

    public Ranker (Map<String, Document> documents, Set<String> resultDocs, String query) {
        this.QUERY = query;
        this.RESULTS = resultDocs;
        this.DOCUMENTS = documents;
    }

    public Vector<Double> weightTfIdfQuery () {
        Map<String, Integer> termFrequencyInQuery = new HashMap<String, Integer>();
        Vector<Double> weightVector = new Vector<Double>();

        String [] queryParse = Tokenizer.tokenize(QUERY, Tokenizer.defaultRegex);
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


            double idfTerm = Math.log10(1 + 1 / 1);
            double tfTerm = 1 + Math.log10(termFrequencyInQuery.get(term));

            weightVector.add(tfTerm * idfTerm);

        }

        return weightVector;
    }

    public Map<String, Vector<Double>> weightTfIdfDocs() {
        Map<String, Map<String, Integer>> termFrequencyInDocs = new HashMap<String, Map<String, Integer>>();
        Map<String, Set<String>> docsContainsTerm = new HashMap<String, Set<String>>();

        for (String docId : RESULTS) {

            termFrequencyInDocs.put(docId, new HashMap<String, Integer>());
            Map<String, Integer> frequency = termFrequencyInDocs.get(docId);

            String [] queryParse = Tokenizer.tokenize(QUERY, Tokenizer.defaultRegex);
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

            String [] textArray = Tokenizer.tokenize(fullText.toLowerCase(), Tokenizer.defaultRegex);
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
        return computeTfIdf(termFrequencyInDocs, docsContainsTerm, sumaDocs);
    }

    private Map<String, Vector<Double>> computeTfIdf (Map<String, Map<String, Integer>> termFrequencyInDocs,
                               Map<String, Set<String>> docsContainsTerm,
                               int sumaDocs) {

        Map<String, Vector<Double>> tfIdf_vectors = new HashMap<String, Vector<Double>>();

        for (Map.Entry<String, Map<String, Integer>> doc : termFrequencyInDocs.entrySet()) {

            // Sort docs IDs
            String [] terms = docsContainsTerm.keySet().toArray(new String [docsContainsTerm.keySet().size()]);
            Arrays.sort(terms);

            Vector<Double> tfidf = new Vector<Double>();
            for (String term : terms) {

                if (doc.getValue().containsKey(term)) {

                    Double tfTerm;
                    Double idfTerm;

                    double termFreqInDoc = doc.getValue().get(term);
                    double termInDocs = docsContainsTerm.get(term).size();

                    // TODO - check if I need this condition (termInDoc is DOUBLE)
                    if (termInDocs != 0) {
                        idfTerm = Math.log10(1 + sumaDocs / termInDocs);
                    } else {
                        idfTerm = Math.log10(1);
                    }

                    tfTerm = 1 + Math.log10(termFreqInDoc);

                    // compute tf-idf
                    // save value to the Vector
                    tfidf.add(tfTerm * idfTerm);
                }

            }

            tfIdf_vectors.put(doc.getKey(), tfidf);
        }

        return tfIdf_vectors;

    }

    private Map<String, Integer> removeBooleanOperators (Map<String, Integer> frequency) {

        if (frequency.containsKey("and")) {
            frequency.remove("and");
        }
        if (frequency.containsKey("or")) {
            frequency.remove("or");
        }
        if (frequency.containsKey("not")) {
            frequency.remove("not");
        }

        return frequency;
    }

}
