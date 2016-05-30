package cz.zcu.kiv.nlp.ir.ranking;

import cz.zcu.kiv.nlp.ir.trec.data.Result;

import java.text.DecimalFormat;
import java.util.Comparator;

/**
 * Class represents hit after searching in documents
 */
public class Hit implements Result {

    /** Convertor to decimal */
    private final DecimalFormat decimalFormat = new DecimalFormat("#");

    /** Document ID */
    private String docId;

    /** Hit evaluation */
    private Double eval;

    /** Rank of hit */
    private int rank;

    /**
     * Constructor
     * @param docId document ID
     * @param eval hit evaluation
     */
    public Hit (String docId, Double eval) {
        this.docId = docId;
        this.eval = eval;
    }

    /**
     * Return document ID
     * @return document ID
     */
    public String getDocumentID() {
        return docId;
    }

    /**
     *
     * @return
     */
    public float getScore() {
//        System.out.println("Original value: " + this.eval);
//        return Float.parseFloat(decimalFormat.format(this.eval));
        return Float.parseFloat(String.valueOf(this.eval));
    }

    public int getRank() {
        return this.rank;
    }

    public String toString(String topic) {
        return getDocumentID() + " score: " + getScore() + " rank: " + getRank() ;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
