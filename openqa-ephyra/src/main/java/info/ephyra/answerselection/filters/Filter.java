package info.ephyra.answerselection.filters;


import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.openqa.core.task.entities.Result;

/**
 * <p>A <code>Filter</code> is part of a pipeline for answer extraction and
 * selection. It manipulates an array of <code>Result</code> objects.</p>
 * 
 * <p>A filter can drop results, create new results from existing ones, or
 * modify results. It can process the results independently (in which case the
 * apply(Result) method should be implemented, or it can simultaneously process
 * a list of results (in which case the apply(Result[]) method should be
 * implemented).</p>
 * 
 * @author Nico Schlaefer
 * @version 2007-03-07
 */
public abstract class Filter {
	/**
	 * Filters a single <code>Result</code> object.
	 * 
	 * @param result result to filter
	 * @return modified result or <code>null</code> if the result is dropped
	 */
	public Result apply(final Result result) {
		return result;
	}

	
	/**
	 * Filters an array of <code>Result</code> objects.
	 * 
	 * @param results results to filter
	 * @return filtered results
	 */
	public Collection<Result> apply(final Collection<Result> results) {
		Collection<Result> filtered = new HashSet<Result>();
		
		for (Result result : results) {
			// filter results that do not have a score of
			// Float.NEGATIVE_INFINITY or Float.POSITIVE_INFINITY
			if (result.getScore() > Float.NEGATIVE_INFINITY &&
				result.getScore() < Float.POSITIVE_INFINITY)
				result = apply(result);
			
			if (result != null) filtered.add(result);
		}
		
		return filtered;
	}
	
	/**
	 * Filters an array of <code>Result</code> objects.
	 * 
	 * @param results results to filter
	 * @return filtered results
	 */
	public Result[] apply(final Result[] results) {
		final Collection<Result> filtered = Arrays.asList(results);
		return apply(filtered).toArray(new Result[0]);
	}
	
	
}
