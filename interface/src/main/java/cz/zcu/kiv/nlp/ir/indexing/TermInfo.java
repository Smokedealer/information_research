package cz.zcu.kiv.nlp.ir.indexing;

import java.util.Set;

public class TermInfo {

	private String term;
	private int count;
	private Set<String> postings;
	
	public TermInfo(String term, int count, Set<String> postings) {
		this.term = term;
		this.count = count;
		this.postings = postings;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Set<String> getPostings() {
		return postings;
	}

	public void setPostings(Set<String> postings) {
		this.postings = postings;
	}
	
	public void setCountNextValue() {
		this.count++;
	}
	
	public void setTerm(String term) {
		this.term = term;
	}
	
	public String getTerm() {
		return this.term;
	}

	@Override
	public String toString() {
		String occuring = "";
		
		for (String posting : postings) {
			occuring += posting + ",";
		}
		
		return count + ";" + occuring;
	}
	
}
