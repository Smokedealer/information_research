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

    public Map<String, Vector<Double>> weightTfIdfDocs() {
        Map<String, Map<String, Integer>> termFrequencyInDocs = new HashMap<String, Map<String, Integer>>();
        Map<String, Set<String>> docsContainsTerm = new HashMap<String, Set<String>>();

        for (String docId : RESULTS) {

            termFrequencyInDocs.put(docId, new HashMap<String, Integer>());
            Map<String, Integer> frequency = termFrequencyInDocs.get(docId);

            for (String word : QUERY.toLowerCase().split(Tokenizer.defaultRegex)) {
                frequency.put(word, 0);
                docsContainsTerm.put(word, new HashSet<String>());
            }

            frequency = removeBooleanOperators(frequency);

            Document doc = DOCUMENTS.get(docId);
            String fullText = doc.getText() + " " +
                            doc.getTitle() + " " +
                            doc.getLocations() + " " +
                            doc.getDate() + " " +
                            doc.getTags();

            String [] textArray = fullText.toLowerCase().split(Tokenizer.defaultRegex);
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

            // Sort terms
            String [] terms = termFrequencyInDocs.keySet().toArray(new String [termFrequencyInDocs.keySet().size()]);
            Arrays.sort(terms);

            Vector<Double> tfidf = new Vector<Double>();

            for (String term : terms) {
                Double tfTerm = 1 + Math.log10(termFrequencyInDocs.get(doc.getKey()).get(term));
                Double idfTerm = Math.log10(1 + sumaDocs / docsContainsTerm.get(doc.getKey()).size());

                // compute tf-idf
                // save value to the Vector
                tfidf.add(tfTerm * idfTerm);
            }

            tfIdf_vectors.put(doc.getKey(), tfidf);
        }

        return tfIdf_vectors;

    }

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
