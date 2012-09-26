package info.ephyra.search;
import java.util.Collection;
import java.util.concurrent.Callable;

import org.openqa.core.task.entities.Query;
import org.openqa.core.task.entities.Result;
public class SearcherCallable implements Callable<Collection<Result>>{
	private final Searcher searcher;
	private final Collection<Query> queries;
	
	public SearcherCallable(final Searcher theSearcher, final Collection<Query> theQueries) {
		searcher = theSearcher;
		queries = theQueries;
	}

	@Override
	public Collection<Result> call() throws Exception {
		return searcher.findResults(queries);
	}

}
