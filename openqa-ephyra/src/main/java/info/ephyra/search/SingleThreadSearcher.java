package info.ephyra.search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openqa.core.task.entities.Query;
import org.openqa.core.task.entities.Result;

public abstract class SingleThreadSearcher implements Searcher {
	public Collection<Result> findResults(final Collection<Query> queries) {
		final Set<Result> results = new HashSet<Result>();
		for (Query query : queries)
			if (matches(query)) {
				final Collection<Result> res = findResults(query);
				if (res != null)
					results.addAll(res);
			}
		return results;
	}

	public Collection<Result> findResults(final Query query) {
		final Result res = findResult(query);
		if (res == null)
			return null;
		Set<Result> result = new HashSet<Result>();
		result.add(res);
		return result;
	}

}
