package cz.zcu.kiv.nlp.ir.searching;

import java.util.*;

import com.fathzer.soft.javaluator.AbstractEvaluator;
import com.fathzer.soft.javaluator.BracketPair;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;

import cz.zcu.kiv.nlp.ir.exceptions.QueryParserException;
import cz.zcu.kiv.nlp.ir.indexing.TermInfo;

public class ParserEvaluator extends AbstractEvaluator<String> {
	
	final static Operator NOT = new Operator("NOT", 1, Operator.Associativity.LEFT, 3);
	final static Operator AND = new Operator("AND", 2, Operator.Associativity.LEFT, 2);
	final static Operator OR = new Operator("OR", 2, Operator.Associativity.LEFT, 1);

	private static final Parameters PARAMETERS;
	private final Searcher SEARCHER;
	private final List<List<TermInfo>> TMP_RESULTS = new ArrayList<List<TermInfo>>();
	
	static {
		PARAMETERS = new Parameters();
		
		PARAMETERS.add(AND);
		PARAMETERS.add(OR);
		PARAMETERS.add(NOT);
		
		PARAMETERS.addExpressionBracket(BracketPair.PARENTHESES);
	}
	
	public ParserEvaluator(Searcher searcher) {
		super(PARAMETERS);
		this.SEARCHER = searcher;
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

		if (!o1.isEmpty() && !o2.isEmpty()) {
			// got both operands
			e1 = this.SEARCHER.search(o1);
			e2 = this.SEARCHER.search(o2);
		} else if (o1.isEmpty() && o2.isEmpty()) {
			// no operands, get them from stack
			if (this.TMP_RESULTS.size() >= 2) {

				e1 = this.TMP_RESULTS.get(this.TMP_RESULTS.size() - 2);
				e2 = this.TMP_RESULTS.get(this.TMP_RESULTS.size() - 1);

				this.TMP_RESULTS.remove(this.TMP_RESULTS.size() - 1);
				this.TMP_RESULTS.remove(this.TMP_RESULTS.size() - 1);
			} else {
				return "Expected two data sets for operator " + operator.getSymbol() + "!";
			}
		} else if (o1.isEmpty() && !this.TMP_RESULTS.isEmpty()) {
			// only second operand, apply operation to new result list and list from stack
			e1 = this.TMP_RESULTS.get(this.TMP_RESULTS.size() - 1);
			e2 = this.SEARCHER.search(o2);

			this.TMP_RESULTS.remove(this.TMP_RESULTS.size() - 1);
		}

		if(o1.isEmpty() && !this.TMP_RESULTS.isEmpty()) {

		}

		if (e1 == null || e2 == null) {
			this.TMP_RESULTS.addAll(new ArrayList<List<TermInfo>>());
			return "";
		}

		if (operator.getSymbol().equals(NOT.getSymbol())) {
			excludeLists(e1, e2);
		} else if (operator.getSymbol().equals(AND.getSymbol())) {
			intersectLists(e1, e2);
		} else if (operator.getSymbol().equals(OR.getSymbol())) {
			mergeLists(e1, e2);
		}
	
		return "";
	}
	
	private void excludeLists(List<TermInfo> e1, List<TermInfo> e2) {
		// gather docs that cant match docs in first set
		Set<String> ex = new HashSet<String>();

		for (TermInfo tr : e2) {
			ex.addAll(tr.getPostings());
		}

		Iterator<TermInfo> trit = e1.iterator();

		// remove doc references
		while (trit.hasNext()) {

			TermInfo tr = trit.next();
			Iterator<String> it = tr.getPostings().iterator();

			while (it.hasNext()) {
				String doc = it.next();

				if (ex.contains(doc)) {
					it.remove();
				}
			}

			// remove searching result if it has no documents
			if (tr.getPostings().isEmpty()) {
				trit.remove();
			}
		}

		this.TMP_RESULTS.add(e1);
	}
	
	private void mergeLists(List<TermInfo> e1, List<TermInfo> e2) {
		e1.addAll(e2);
		this.TMP_RESULTS.add(e1);
	}
	
	private void intersectLists(List<TermInfo> e1, List<TermInfo> e2) {
		// TODO - like BooleanRetrieval
		if (e1.isEmpty() || e2.isEmpty()) {
			this.TMP_RESULTS.addAll(new ArrayList<List<TermInfo>>());

		} else {
			// add document ids from second result set first
			Set<String> int2 = new HashSet<String>();

			for (TermInfo sr : e2) {
				int2.addAll(sr.getPostings());
			}

			Iterator<TermInfo> it1 = e1.iterator();

			// remove results from e1 that are not in e2
			while (it1.hasNext()) {
				TermInfo sr = it1.next();
				Iterator<String> it = sr.getPostings().iterator();

				while (it.hasNext()) {
					String docId = it.next();

					if (!int2.contains(docId)) {
						it.remove();
					}
				}

				if (sr.getPostings().isEmpty()) {
					it1.remove();
				}
			}

			// check if there are any results left
			if (!e1.isEmpty()) {
				Set<String> int1 = new HashSet<String>();

				// build doclist from e1
				for (TermInfo sr : e1) {
					int1.addAll(sr.getPostings());
				}

				Iterator<TermInfo> it2 = e2.iterator();

				while (it2.hasNext()) {
					TermInfo sr = it2.next();
					Iterator<String> it = sr.getPostings().iterator();

					while (it.hasNext()) {
						String docId = it.next();

						if (!int1.contains(docId)) {
							it.remove();
						}
					}

					if (sr.getPostings().isEmpty()) {
						it2.remove();
					}
				}

				// merge remaining results
				this.mergeLists(e1, e2);
			} else {
				this.TMP_RESULTS.add(e1);
			}
		}
	}

// TODO (original) - public List<QueryEvalResult> buildResults(String query) throws QueryParserException {
	public Set<String> buildResults(String query) throws QueryParserException {
		String evalResult;

		try {
			// eval query and build intermediate result list
			evalResult = this.evaluate(query);
		} catch (IllegalArgumentException ex) {
			throw new QueryParserException("There was an error while parsing the query.");
		}

		if(!evalResult.isEmpty()) {
			throw new QueryParserException(evalResult);
		}

		// if evaluation went OK, only one list should remain
		if (this.TMP_RESULTS.size() != 1) {
			throw new QueryParserException("There was an error while parsing "
					+ "the query, most likely a missing operand for a boolean operator");
		}

		return mergeResults(this.TMP_RESULTS.get(0));
	}

	private Set<String> mergeResults (List<TermInfo> results) {
		Set<String> finalSetOfDocsID = new HashSet<String>();
		for (TermInfo term : results) finalSetOfDocsID.addAll(term.getPostings());
        return finalSetOfDocsID;
	}
}
