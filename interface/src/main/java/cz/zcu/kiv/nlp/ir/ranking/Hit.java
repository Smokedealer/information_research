package cz.zcu.kiv.nlp.ir.ranking;

/**
 * Created by dzejkob23 on 29.5.16.
 */
public class Hit {

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
}
