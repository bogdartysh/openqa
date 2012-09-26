package info.ephyra.search;

import java.util.Collection;

import org.openqa.core.task.entities.Query;
import org.openqa.core.task.entities.Result;

public interface Searcher {
	public Collection<Result> findResults(final Collection<Query> queries);
	public Result findResult(final Query queries);
	public boolean matches(final Query query);
}
