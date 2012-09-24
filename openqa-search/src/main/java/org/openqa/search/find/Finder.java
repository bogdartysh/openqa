package org.openqa.search.find;

import org.openqa.search.find.KnowledgeAnnotatorFinder;
import info.ephyra.querygeneration.Query;
import info.ephyra.search.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Finder {
	
	private ExecutorService executor ;
	
	/**
	 * <code>KnowledgeAnnotators</code> used to query (semi)structured knowledge
	 * sources.
	 */
	private List<KnowledgeAnnotatorFinder> kas;
	/**
	 * <code>KnowledgeMiners</code> used to query unstructured knowledge
	 * sources.
	 */
	private Set<Result> results;
	
	public Finder(final int maxPending) {
		kas = new ArrayList<KnowledgeAnnotatorFinder>();
		results = new HashSet<Result>();
		executor = Executors.newFixedThreadPool(maxPending);
	}
	public Finder() {	
		this(30);
	}

	/**
	 * Sends several alternative queries to all the searchers that have been
	 * registered and returns the aggregated results.
	 * 
	 * @param queries queries to be pr	ocessed
	 * @return results returned by the searchers
	 */
	public Collection<Result> doSearch(final Query[] queries) {
		final Set<Result> results = new HashSet<Result>();
		final List<Future<List<Result>>> futures = new LinkedList<Future<List<Result>>>(queries.size * kas.size);
		for (Query query:queries)
		for (KnowledgeAnnotatorFinder ka:kas) {
			ka.setQuery(query);
                  futures.add(executor.submit(ka)); 	
		}
		for (Future<List<Result>> future : futures) {
			for (Result result: future.get()) 
				results.add(result);			
		}
		return results;
	}
}
