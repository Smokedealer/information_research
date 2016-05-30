package cz.zcu.kiv.nlp.ir.indexing;

import java.util.Set;

/**
 * Instaces of this class represents information about term from searching in documents
 *
 * @author Jakub Zika
 * @version 1.1
 */
public class TermInfo {

	/** Term after preprocessing */
	private String term;
	/** Count of terms in doc */
	private int count;
	/** How many times occurs term in documents */
	private Set<String> postings;

	/**
	 * Constructor
	 * @param term term after preprocessing
	 * @param count count of terms in doc
	 * @param postings how many times occurs term in documents
     */
	public TermInfo(String term, int count, Set<String> postings) {
		this.term = term;
		this.count = count;
		this.postings = postings;
	}

	/**
	 * Return count
	 * @return count
     */
	public int getCount() {
		return count;
	}

	/**
	 * Set count
	 * @param count count
     */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * Return postings
	 * @return postings
     */
	public Set<String> getPostings() {
		return postings;
	}

	/**
	 * Set postings
	 * @param postings postings
     */
	public void setPostings(Set<String> postings) {
		this.postings = postings;
	}

	/**
	 * Set count with increment
	 */
	public void setCountNextValue() {
		this.count++;
	}

	/**
	 * Set term
	 * @param term term
     */
	public void setTerm(String term) {
		this.term = term;
	}

	/**
	 * Get term
	 * @return term
     */
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
