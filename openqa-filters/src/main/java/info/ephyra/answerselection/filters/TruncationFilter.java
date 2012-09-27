package info.ephyra.answerselection.filters;

import info.ephyra.nlp.NETagger;
import info.ephyra.util.RegexConverter;
import info.ephyra.util.StringUtils;

import java.util.ArrayList;
import java.util.Hashtable;

import org.openqa.core.task.entities.Result;

/**
 * <p>A filter that truncates the answer strings. It drops the following
 * prefixes and suffixes:
 * <ul>
 *   <li>blanks and some special characters</li>
 *   <li>articles</li>
 *   <li>"and", "or"</li>
 *   <li>prepositions</li>
 * </ul>
 * After truncation, similar answers are merged.</p>
 * 
 * <p>This filter is not applied to answer strings that have been extracted with
 * a rule- or list-based NE tagger since these answers are assumed to be
 * properly formatted.</p>
 * 
 * <p>This class extends the class <code>Filter</code>.</p>
 * 
 * @author Nico Schlaefer
 * @version 2007-05-28
 */
public class TruncationFilter extends Filter {
	
	
	/**
	 * Filters a single <code>Result</code> object.
	 * 
	 * @param result result to filter
	 * @return result or <code>null</code>
	 */
	public Result apply(Result result) {
		// do not apply the filter if the answer string is a NE that was
		// extracted with a rule- or list-based tagger
		if (result.isNamedEntity() &&
				!NETagger.allModelType(result.getNeTypes())) return result;
		
		String answer = result.getAnswer();
		answer = FilterUtils.truncate(answer);
		result.setAnswer(answer);
		
		return result;
	}
	
	/**
	 * Filters an array of <code>Result</code> objects.
	 * 
	 * @param results results to filter
	 * @return filtered results
	 */
	public Result[] apply(Result[] results) {
		// all results that pass the filter
		ArrayList<Result> filtered = new ArrayList<Result>();
		// for each extractor, truncated answers and corresponding results
		Hashtable<String, Hashtable<String, Result>> truncated =
			new Hashtable<String, Hashtable<String, Result>> ();
		
		// sort results by their scores in descending order
		results = (new ScoreSorterFilter()).apply(results);
		
		for (Result result : results) {
			// only truncate factoid answers
			if (result.getScore() <= 0 ||
					result.getScore() == Float.POSITIVE_INFINITY) {
				filtered.add(result);
				continue;
			}
			// make sure that answers come from a single extractor
			String[] extractors = result.getExtractionTechniques();
			if (extractors == null || extractors.length != 1) {
				filtered.add(result);
				continue;
			}
			String extractor = extractors[0];
			
			// truncate result
			result = apply(result);
			
			// merge with similar results from same extractor
			Hashtable<String, Result> truncatedT = truncated.get(extractor);
			if (truncatedT == null) {
				truncatedT = new Hashtable<String, Result>();
				truncated.put(extractor, truncatedT);
			}
			String norm = StringUtils.normalize(result.getAnswer());
			Result similar = truncatedT.get(norm);
			if (similar == null) {
				filtered.add(result);
				truncatedT.put(norm, result);
			} else {
				similar.incScore(result.getScore());
			}
		}
		
		return filtered.toArray(new Result[filtered.size()]);
	}
}
