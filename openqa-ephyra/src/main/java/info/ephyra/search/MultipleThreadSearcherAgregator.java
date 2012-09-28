package info.ephyra.search;

import java.util.ArrayList;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.openqa.core.task.entities.Query;
import org.openqa.core.task.entities.Result;

public class MultipleThreadSearcherAgregator implements SearchAgregator {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 3102372687797751321L;
	private static final int DEFAULT_POOL_SIZE = 30;
	private Logger _log = Logger
			.getLogger(MultipleThreadSearcherAgregator.class.getCanonicalName());

	private List<Searcher> searchers;

	ExecutorService pool = null;

	public MultipleThreadSearcherAgregator() {
		super();
		searchers = new LinkedList<Searcher>();
		pool = Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);
	}

	public MultipleThreadSearcherAgregator(final int poolSize) {
		super();
		searchers = new LinkedList<Searcher>();
		pool = Executors.newFixedThreadPool(poolSize);

	}

	public Collection<Result> findResults(final Collection<Query> queries) {
		final Set<Result> results = new HashSet<Result>();
		final Set<Future<Collection<Result>>> set = new HashSet<Future<Collection<Result>>>(
				getSearchers().size());
		for (Searcher searcher : getSearchers()) {
			final Callable<Collection<Result>> callable = new SearcherCallable(
					searcher, queries);
			final Future<Collection<Result>> future = getPool()
					.submit(callable);
			set.add(future);
		}
		for (Future<Collection<Result>> future : set) {
			Collection<Result> res;
			try {
				res = future.get();
				if (res != null)
					results.addAll(res);
			} catch (InterruptedException | ExecutionException e) {
				_log.error(e, e);
			}

		}
		return results;
	}

	@Override
	public Result findResult(final Query query) {
		final List<Query> lst = new ArrayList<Query>(1);
		lst.add(query);
		final Collection<Result> results = findResults(lst);
		if (results == null)
			return null;
		return results.iterator().next();
	}

	@Override
	public boolean matches(final Query query) {
		for (Searcher searcher : getSearchers())
			if (searcher.matches(query))
				return false;
		return true;
	}

	/**
	 * @return the searchers
	 */
	@Override
	public List<Searcher> getSearchers() {
		return searchers;

	}

	/**
	 * @param searchers
	 *            the searchers to set
	 */
	@Override
	public void setSearchers(final List<Searcher> searchers) {
		this.searchers = searchers;
	}

	/**
	 * @return the pool
	 */
	public ExecutorService getPool() {
		return pool;
	}

	@Override
	public Collection<Result> findResults(Query query) {
		final List<Query> lst = new ArrayList<Query>(1);
		lst.add(query);
		return findResults(lst);
	}

}
