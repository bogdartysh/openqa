package info.ephyra.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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

public class MultipleThreadSearcherAgregator extends HashSet<Searcher>
		implements SearcherAgregator {
	private static final long serialVersionUID = 3102372687797751321L;
	private static final int DEFAULT_POOL_SIZE = 30;
	private Logger _log = Logger
			.getLogger(MultipleThreadSearcherAgregator.class.getCanonicalName());
	
	ExecutorService pool = null;

	public ExecutorService getPool() {
		if (pool == null)
			pool = Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);
		return pool;
	}

	public boolean setPoolSize(final int poolSize) {
		if (pool == null) {
			pool = Executors.newFixedThreadPool(poolSize);
			return true;
		}
		_log.warn("try to change pool size is not permitted");
		return false;

	}

	public Collection<Result> findResults(final Collection<Query> queries) {
		final Set<Result> results = new HashSet<Result>();
		final Set<Future<Collection<Result>>> set = new HashSet<Future<Collection<Result>>>(
				size());
		for (Searcher searcher : this) {
			final Callable<Collection<Result>> callable = new SearcherCallable(
					searcher, queries);
			final Future<Collection<Result>> future = pool.submit(callable);
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
	public Result findResult(Query query) {
		List<Query> lst = new ArrayList<Query>(1);
		lst.add(query);
		return findResults(lst)?.iterator()?.next();		
	}

	@Override
	public boolean matches(Query query) {
		for (Searcher searcher : this)
			if (searcher.matches(query))
				return false;
		return true;
	}

}
