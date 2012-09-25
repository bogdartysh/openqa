package info.ephyra.answerselection.filters;


import java.util.Arrays;

import org.openqa.core.task.entities.Result;

/**
 * <p>The <code>ReverseScoreSorterFilter</code> sorts the results by their
 * scores in ascending order. The sort is guaranteed to be stable.</p>
 * 
 * <p>This class extends the class <code>Filter</code>.</p>
 * 
 * @author Nico Schlaefer
 * @version 2007-05-24
 */
public class ReverseScoreSorterFilter extends Filter {
	/**
	 * Sorts the results by their scores in ascending order.
	 * 
	 * @param results array of <code>Result</code> objects
	 * @return sorted array of <code>Result</code> objects
	 */
	public Result[] apply(Result[] results) {
		Arrays.sort(results);
		
		return results;
	}
}
