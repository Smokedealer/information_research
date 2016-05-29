package cz.zcu.kiv.nlp.ir.ranking;

import java.util.Comparator;

/**
 * Created by dzejkob23 on 29.5.16.
 */
public class Hit implements Comparator<Hit>, Comparable<Hit> {

    private String docId;
    private Double eval;

    public Hit (String docId, Double eval) {
        this.docId = docId;
        this.eval = eval;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public Double getEval() {
        return eval;
    }

    public void setEval(Double eval) {
        this.eval = eval;
    }

    public int compareTo(Hit hit) {
        return (this.eval).compareTo(hit.getEval());
    }

    public int compare(Hit h1, Hit h2) {
        return (int) (h1.getEval() - h2.getEval());
    }
}
