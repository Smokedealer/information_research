package cz.zcu.kiv.nlp.ir.ranking;

import java.util.*;

/**
 * Created by dzejkob23 on 29.5.16.
 */
public class Evaluator {

    private Map<String, Vector<Double>>  resultsWeight;
    private Vector<Double> queryWeight;

    public Evaluator (Map<String, Vector<Double>>  resultsWeight, Vector<Double> queryWeight) {
        this.resultsWeight = resultsWeight;
        this.queryWeight = queryWeight;
    }

    public List<Hit> getAllSortedHits () {
        Map<String, Double> cosinus = cosinusLength();
        List<Hit> hits = new ArrayList<Hit>();

        for (Map.Entry<String, Double> entry : cosinus.entrySet()) {
            hits.add(new Hit(entry.getKey(), entry.getValue()));
        }

        Collections.sort(hits);
        return hits;
    }

    private Map<String, Double> cosinusLength () {
        Map<String, Double> cosinus = new HashMap<String, Double>();
        Double queryNormalizeVector = normalizeVector(queryWeight);

        for (Map.Entry<String, Vector<Double>> entry : resultsWeight.entrySet()) {
            for (int i = 0; i < queryWeight.size(); i++) {
                Double resulNormalizeVector = normalizeVector(entry.getValue());
                Double cos = (queryWeight.get(i) * entry.getValue().get(i))/(queryNormalizeVector * resulNormalizeVector);
                cosinus.put(entry.getKey(), cos);
            }
        }

        return cosinus;
    }

    private double normalizeVector (Vector<Double> v) {
        Double result = 0.0;

        for (int i = 0; i < v.size(); i++) {
            result += v.get(i) * v.get(i);
        }

        return Math.sqrt(result);
    }

}
