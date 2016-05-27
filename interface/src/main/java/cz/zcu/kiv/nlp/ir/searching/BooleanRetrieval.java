//package cz.zcu.kiv.nlp.ir.search;
//import com.fathzer.soft.javaluator.AbstractEvaluator;
//import com.fathzer.soft.javaluator.BracketPair;
//import com.fathzer.soft.javaluator.Operator;
//import com.fathzer.soft.javaluator.Parameters;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//
//import javax.naming.directory.SearchResult;
//
//public class BooleanRetrieval extends AbstractEvaluator<String> {
//
//	public final static Operator NOT = new Operator("NOT", 2, Operator.Associativity.LEFT, 2);
//	public final static Operator AND = new Operator("AND", 2, Operator.Associativity.LEFT, 2);
//	public final static Operator OR = new Operator("OR", 2, Operator.Associativity.LEFT, 1);
//
//	private final static Parameters PARAMS;
//	private final List<List<SearchResult>> INTER_RESULT_STACK = new ArrayList<>();
//	private final IndexSearcher SEARCHER;
//
//	static {
//		PARAMS = new Parameters();
//
//		PARAMS.add(AND);
//		PARAMS.add(OR);
//		PARAMS.add(NOT);
//
//		PARAMS.addExpressionBracket(BracketPair.PARENTHESES);
//	}
//
//	public BooleanRetrieval(IndexSearcher searcher) {
//		super(PARAMS);
//		this.SEARCHER = searcher;
//	}
//
//	@Override
//	protected String toValue(String string, Object o) {
//		return string;
//	}
//
//	@Override
//	protected String evaluate(Operator operator, Iterator<String> operands, Object evaluationContext) {
//		if (operator == NOT || operator == OR || operator == AND) {
//			return this.applyOperator(operator, operands.next(), operands.next());
//		}
//
//		return "Unknown operand";
//	}
//
//	protected String applyOperator(Operator operator, String o1, String o2) {
//		List<SearchResult> e1 = null;
//		List<SearchResult> e2 = null;
//
//		if (!o1.isEmpty() && !o2.isEmpty()) {
//			// got both operands
//			e1 = this.SEARCHER.searching(o1);
//			e2 = this.SEARCHER.searching(o2);
//		} else if (o1.isEmpty() && o2.isEmpty()) {
//			// no operands, get them from stack
//			if (this.INTER_RESULT_STACK.size() >= 2) {
//
//				e1 = this.INTER_RESULT_STACK.get(this.INTER_RESULT_STACK.size() - 2);
//				e2 = this.INTER_RESULT_STACK.get(this.INTER_RESULT_STACK.size() - 1);
//
//				this.INTER_RESULT_STACK.remove(this.INTER_RESULT_STACK.size() - 1);
//				this.INTER_RESULT_STACK.remove(this.INTER_RESULT_STACK.size() - 1);
//			} else {
//				return "Expected two data sets for operator " + operator.getSymbol() + "!";
//			}
//		} else if (o1.isEmpty() && !this.INTER_RESULT_STACK.isEmpty()) {
//			// only second operand, apply operation to new result list and list from stack
//			e1 = this.INTER_RESULT_STACK.get(this.INTER_RESULT_STACK.size() - 1);
//			e2 = this.SEARCHER.searching(o2);
//
//			this.INTER_RESULT_STACK.remove(this.INTER_RESULT_STACK.size() - 1);
//		}
//
//		if (e1 == null || e2 == null) {
//			this.INTER_RESULT_STACK.add(new ArrayList<>());
//			return "";
//		}
//
//		if (operator == NOT) {
//			excludeLists(e1, e2);
//		} else if (operator == AND) {
//			intersectLists(e1, e2);
//		} else if (operator == OR) {
//			mergeLists(e1, e2);
//		}
//
//		return "";
//
//	}
//
//	protected void excludeLists(List<SearchResult> e1, List<SearchResult> e2) {
//		// gather docs that cant match docs in first set
//		Set<String> ex = new HashSet<>();
//
//		for (SearchResult sr : e2) {
//			ex.addAll(sr.getPostings().keySet());
//		}
//
//		Iterator<SearchResult> srit = e1.iterator();
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
//			// remove searching result if it has no documents
//			if (sr.getPostings().isEmpty()) {
//				srit.remove();
//			}
//		}
//
//		this.INTER_RESULT_STACK.add(e1);
//	}
//
//	protected void mergeLists(List<SearchResult> e1, List<SearchResult> e2) {
//		e1.addAll(e2);
//		this.INTER_RESULT_STACK.add(e1);
//	}
//
//	protected void intersectLists(List<SearchResult> e1, List<SearchResult> e2) {
//		if (e1.isEmpty() || e2.isEmpty()) {
//			this.INTER_RESULT_STACK.add(new ArrayList<>());
//
//		} else {
//			// add document ids from second result set first
//			Set<String> int2 = new HashSet<>();
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
//				this.INTER_RESULT_STACK.add(e1);
//			}
//		}
//	}
//
//	@Override
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
//		if (this.INTER_RESULT_STACK.size() != 1) {
//			throw new QueryParseException("There was an error while parsing "
//					+ "the query, most likely a missing operand for a boolean operator");
//		}
//
//		return QueryResultMerger.merge(this.INTER_RESULT_STACK.get(0));
//	}
//
//}