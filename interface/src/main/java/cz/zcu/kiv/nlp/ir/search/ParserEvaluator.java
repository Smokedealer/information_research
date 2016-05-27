package cz.zcu.kiv.nlp.ir.search;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.directory.SearchResult;

import com.fathzer.soft.javaluator.AbstractEvaluator;
import com.fathzer.soft.javaluator.BracketPair;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;

import cz.zcu.kiv.nlp.ir.indexing.Dictionary;
import cz.zcu.kiv.nlp.ir.indexing.TermInfo;

public class ParserEvaluator extends AbstractEvaluator<String> {
	
	final static Operator NOT = new Operator("NOT", 1, Operator.Associativity.LEFT, 3);
	final static Operator AND = new Operator("AND", 2, Operator.Associativity.LEFT, 2);
	final static Operator OR = new Operator("OR", 2, Operator.Associativity.LEFT, 1);

	private static final Parameters PARAMETERS;
	private final Map<String, TermInfo> DICTIONARY;
	private final List<List<TermInfo>> TMP_RESULTS = new ArrayList<List<TermInfo>>();
	
	static {
		PARAMETERS = new Parameters();
		
		PARAMETERS.add(AND);
		PARAMETERS.add(OR);
		PARAMETERS.add(NOT);
		
		PARAMETERS.addExpressionBracket(BracketPair.PARENTHESES);
	}
	
	public ParserEvaluator(Map<String, TermInfo> dictionary ) {
		super(PARAMETERS);
		this.DICTIONARY = dictionary;
	}
	
	@Override
	protected String toValue(String str, Object arg1) {
		return str;
	}
	
	@Override
	protected String evaluate(Operator operator, Iterator<String> operands, Object evaluationContext) {
		if (operator == NOT || operator == OR || operator == AND) {
			return this.applyOperator(operator, operands.next(), operands.next());
		}
	
		return "Unknown operand";
	}

	private String applyOperator(Operator operator, String o1, String o2) {
		List<TermInfo> e1 = null;
		List<TermInfo> e2 = null;

		System.out.println("Operator: " + operator.getSymbol());
		System.out.println("O1: " + o1);
		System.out.println("O2: " + o2);

//		if (!o1.isEmpty() && !o2.isEmpty()) {
//			// got both operands
//			e1 = this.SEARCHER.search(o1);
//			e2 = this.SEARCHER.search(o2);
//		} else if (o1.isEmpty() && o2.isEmpty()) {
//			// no operands, get them from stack
//			if (this.TMP_RESULTS.size() >= 2) {
//
//				e1 = this.TMP_RESULTS.get(this.TMP_RESULTS.size() - 2);
//				e2 = this.TMP_RESULTS.get(this.TMP_RESULTS.size() - 1);
//
//				this.TMP_RESULTS.remove(this.TMP_RESULTS.size() - 1);
//				this.TMP_RESULTS.remove(this.TMP_RESULTS.size() - 1);
//			} else {
//				return "Expected two data sets for operator " + operator.getSymbol() + "!";
//			}
//		} else if (o1.isEmpty() && !this.TMP_RESULTS.isEmpty()) {
//			// only second operand, apply operation to new result list and list from stack
//			e1 = this.TMP_RESULTS.get(this.TMP_RESULTS.size() - 1);
//			e2 = this.SEARCHER.search(o2);
//
//			this.TMP_RESULTS.remove(this.TMP_RESULTS.size() - 1);
//		}
//
//		if (e1 == null || e2 == null) {
//			this.TMP_RESULTS.addAll(new ArrayList<List<TermInfo>>());
//			return "";
//		}
//
//		if (operator.getSymbol().equals(NOT.getSymbol())) {
//			excludeLists(e1, e2);
//		} else if (operator.getSymbol().equals(AND.getSymbol())) {
//			intersectLists(e1, e2);
//		} else if (operator.getSymbol().equals(OR.getSymbol())) {
//			mergeLists(e1, e2);
//		}
	
		return "";
	}
	
//	private void excludeLists(List<TermInfo> e1, List<TermInfo> e2) {
//		// TODO - like BooleanRetrieval
//		// gather docs that cant match docs in first set
//		Set<String> ex = new HashSet<String>();
//
//		for (TermInfo sr : e2) {
//			ex.addAll(sr.getPostings().keySet());
//		}
//
//		Iterator<TermInfo> srit = e1.iterator();
//
//		// remove doc references
//		while (srit.hasNext()) {
//
//			SearchResult sr = srit.next();
//			Iterator<String> it = sr.getPostings().keySet().iterator();
//
//			while (it.hasNext()) {
//				String doc = it.next();
//
//				if (ex.contains(doc)) {
//					it.remove();
//				}
//			}
//
//			// remove search result if it has no documents
//			if (sr.getPostings().isEmpty()) {
//				srit.remove();
//			}
//		}
//
//		this.TMP_RESULTS.add(e1);
//	}
	
//	private void mergeLists(List<TermInfo> e1, List<TermInfo> e2) {
//		// TODO - like BooleanRetrieval
//		e1.addAll(e2);
//		this.TMP_RESULTS.add(e1);
//	}
	
//	private void intersectLists(List<TermInfo> e1, List<TermInfo> e2) {
//		// TODO - like BooleanRetrieval
//		if (e1.isEmpty() || e2.isEmpty()) {
//			this.TMP_RESULTS.addAll(new ArrayList<List<TermInfo>>());
//
//		} else {
//			// add document ids from second result set first
//			Set<String> int2 = new HashSet<String>();
//
//			for (SearchResult sr : e2) {
//				int2.addAll(sr.getPostings().keySet());
//			}
//
//			Iterator<SearchResult> it1 = e1.iterator();
//
//			// remove results from e1 that are not in e2
//			while (it1.hasNext()) {
//				SearchResult sr = it1.next();
//				Iterator<String> it = sr.getPostings().keySet().iterator();
//
//				while (it.hasNext()) {
//					String docId = it.next();
//
//					if (!int2.contains(docId)) {
//						it.remove();
//					}
//				}
//
//				if (sr.getPostings().isEmpty()) {
//					it1.remove();
//				}
//			}
//
//			// check if there are any results left
//			if (!e1.isEmpty()) {
//				Set<String> int1 = new HashSet<>();
//
//				// build doclist from e1
//				for (SearchResult sr : e1) {
//					int1.addAll(sr.getPostings().keySet());
//				}
//
//				Iterator<SearchResult> it2 = e2.iterator();
//
//				while (it2.hasNext()) {
//					SearchResult sr = it2.next();
//					Iterator<String> it = sr.getPostings().keySet().iterator();
//
//					while (it.hasNext()) {
//						String docId = it.next();
//
//						if (!int1.contains(docId)) {
//							it.remove();
//						}
//					}
//
//					if (sr.getPostings().isEmpty()) {
//						it2.remove();
//					}
//				}
//
//				// merge remaining results
//				this.mergeLists(e1, e2);
//			} else {
//				this.TMP_RESULTS.add(e1);
//			}
//		}
//	}
	
//	public List<QueryEvalResult> buildResults(String query) throws QueryParseException {
//		String evalResult;
//
//		try {
//			// eval query and build intermediate result list
//			evalResult = this.evaluate(query);
//		} catch (IllegalArgumentException ex) {
//			throw new QueryParseException("There was an error while parsing the query.");
//		}
//
//		if(!evalResult.isEmpty()) {
//			throw new QueryParseException(evalResult);
//		}
//
//		// if evaluation went OK, only one list should remain
//		if (this.TMP_RESULTS.size() != 1) {
//			throw new QueryParseException("There was an error while parsing "
//					+ "the query, most likely a missing operand for a boolean operator");
//		}
//
//		return QueryResultMerger.merge(this.TMP_RESULTS.get(0));
//	}
}
