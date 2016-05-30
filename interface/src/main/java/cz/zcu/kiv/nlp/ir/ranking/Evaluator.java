package cz.zcu.kiv.nlp.ir.ranking;

import cz.zcu.kiv.nlp.ir.trec.data.Result;
import cz.zcu.kiv.nlp.ir.trec.data.ResultImpl;

import java.util.*;

/**
 * Class evaluate results after searching according query
 *
 * @author Jakub Zika
 * @version 1.2
 */
public class Evaluator {

    /** Weight of results */
    private Map<String, Vector<Double>>  resultsWeight;

    /** Weight of query */
    private Vector<Double> queryWeight;

    /**
     * Constructor
     * @param resultsWeight weight of result
     * @param queryWeight weight of qeury
     */
    public Evaluator (Map<String, Vector<Double>>  resultsWeight, Vector<Double> queryWeight) {
        this.resultsWeight = resultsWeight;
        this.queryWeight = queryWeight;
    }

    /**
     * Return sorted hits
     * @return sorted hits
     */
    public List<Result> getAllSortedHits () {
        Map<String, Double> cosinus = cosinusLength();
        List<Result> hits = new ArrayList<Result>();

        for (Map.Entry<String, Double> entry : cosinus.entrySet()) {
            ResultImpl ri = new ResultImpl();
            ri.setDocumentID(entry.getKey());
            ri.setScore(Float.parseFloat(Double.toString(entry.getValue())));
            hits.add(ri);
        }

        Comparator<Result> cmp = new Comparator<Result>() {
            public int compare(Result o1, Result o2) {
                if (o1.getScore() > o2.getScore()) return -1;
                if (o1.getScore() == o2.getScore()) return 0;
                return 1;
            }
        };

        Collections.sort(hits, cmp);
        int rank = 0;

        for (Result hit : hits) {
            rank++;
            ((ResultImpl) hit).setRank(rank);
        }

        return hits;
    }

    /**
     * Compute cosinus length
     * @return map with cosinus
     */
    private Map<String, Double> cosinusLength () {
        Map<String, Double> cosinus = new HashMap<String, Double>();
        Double queryNormalizeVector = normalizeVector(queryWeight);

        for (Map.Entry<String, Vector<Double>> entry : resultsWeight.entrySet()) {
            Double resulNormalizeVector = normalizeVector(entry.getValue());
            Double cos = 0.0;
            for (int i = 0; i < queryWeight.size(); i++) {
                cos += (queryWeight.get(i) * entry.getValue().get(i))/(queryNormalizeVector * resulNormalizeVector);
            }
            cosinus.put(entry.getKey(), cos);
        }

        return cosinus;
    }

    /**
     * Compute normalized vector
     * @param v input vector
     * @return normalized vector
     */
    private double normalizeVector (Vector<Double> v) {
        Double result = 0.0;

        for (int i = 0; i < v.size(); i++) {
            result += v.get(i) * v.get(i);
        }

        return Math.sqrt(result);
    }

}
