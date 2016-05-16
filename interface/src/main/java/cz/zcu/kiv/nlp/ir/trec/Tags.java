package cz.zcu.kiv.nlp.ir.trec;

public enum Tags {
	LANG("lang"),
	TOPICS("topics"),
    TOPIC("topic"),
    IDENTIFIER("identifier"),
    TITLE("title"),
    DESCRIPTION("description"),
    NARRATIVE("narrative");

    private String tag;

	Tags(String url) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

}
