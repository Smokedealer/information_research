package cz.zcu.kiv.nlp.ir.indexing;

import java.util.Set;

public class TermInfo {

	private int count;
	private Set<String> postings;
	
	public TermInfo(int count, Set<String> postings) {
		this.count = 1;
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
}
