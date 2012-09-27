package info.ephyra.answerselection.filters;

import org.apache.log4j.Logger;
import org.openqa.core.task.entities.Result;

import info.ephyra.questionanalysis.KeywordExtractor;

/**
 * <p>A filter that drops a result if the answer string contains a keyword from
 * the question. Useful to eliminate answers that simply repeat the question,
 * e.g. without this filter a likely answer to the question "Who killed JFK?"
 * would be "JFK".</p>
 * 
 * <p>Note that this filter causes problems if a correct answer repeats part of
 * the question, e.g. answers to the question "What are the members of the
 * Kennedy clan?" also contain the keyword "Kennedy".</p>
 * 
 * <p>This class extends the class <code>Filter</code>.</p>
 * 
 * @author Nico Schlaefer
 * @version 2007-04-16
 */
public class QuestionKeywordsFilter extends Filter {
	private static Logger _logger = Logger.getLogger(QuestionKeywordsFilter.class);
	/**
	 * Filters a single <code>Result</code> object.
	 * <p>Should not apply to name/identities</p>  
	 * @param result result to filter
	 * @return result or <code>null</code>
	 */
	public Result apply(final Result result) {		
		final String[] tokens = KeywordExtractor.tokenize(result.getAnswer());
		final String[] kws = result.getQuery().getAnalyzedQuestion().getKeywords();
		for (String token : tokens)
			if (!result.getQuery().getInterpretation().getTarget().equalsIgnoreCase(token))
			for (String kw : kws)
				if (token.equalsIgnoreCase(kw)) {
					_logger.debug("seems that result is another form of a question");
					return null;
				}
		
		return result;
	}
}
