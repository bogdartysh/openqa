package info.ephyra.search.searchers;


import info.ephyra.nlp.SentenceExtractor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openqa.core.task.entities.Query;
import org.openqa.core.task.entities.Result;

/**
 * <p>
 * A <code>KnowledgeAnnotator</code> for the Wikipedia online encyclopedia. It
 * answers a question for a definition by returning a sentence from the
 * corresponding Wikipedia web page.
 * </p>
 * 
 * <p>
 * It runs as a separate thread, so several queries can be performed in
 * parallel.
 * </p>
 * 
 * <p>
 * This class extends the class <code>KnowledgeAnnotator</code>.
 * </p>
 * 
 * @author Nico Schlaefer, Bogdan Artyushenko
 */
public class WikipediaKA extends KnowledgeAnnotator {
	private Logger _log = Logger
			.getLogger(WikipediaKA.class.getCanonicalName());
	
	private static final float SCORE = 100f;

	/** The URL of the Wikipedia search page. */
	private static final String URL = "http://en.wikipedia.org/wiki/Special:Search?search=";

	/**
	 * Protected constructor used by the <code>getCopy()</code> method.
	 * 
	 * @param name
	 *            name of the <code>KnowledgeAnnotator</code>
	 * @param qPatterns
	 *            question patterns
	 * @param qContents
	 *            descriptors of the relevant content of a question
	 */
	protected WikipediaKA(final String name, final List<Pattern> qPatterns,
			final List<String> qContents) {
		super(name, qPatterns, qContents);
	}

	/**
	 * Creates a <code>DbPediaSearcher</code> and calls the constructor of the
	 * superclass that reads the question patterns from a file.
	 * 
	 * @param filename
	 *            file containing the question patterns
	 */
	public WikipediaKA(final String filename) throws IOException {
		super(filename);
	}

	@Override
	public Result findResult(final Query query) {
		try {
			// compose URL for the search
			_log.debug("Wiki searching");

			final String param = getContent().replace(" ", "+");
			final URL search = new URL(URL + param);

			// retrieve document and extract answer sentence

			String line, sentence;
			final String termMatcher = "(?i).*" + getContent() + ".*";
			final String definitionMatcher = "(?i)(an? |the )?" + getContent()
					+ ".*\\.";

			try (BufferedReader in = new BufferedReader(new InputStreamReader(
					search.openStream(), Charset.forName("utf-8")))) {

				while (in.ready()) {
					line = in.readLine();

					// line should contain the term
					if (line.matches(termMatcher)) {
						// extract first sentence
						sentence = SentenceExtractor.getSentencesFromHtml(line)[0];

						// sentence is really a definition of the term
						if (sentence.matches(definitionMatcher))
							// create result from sentence
							return new Result(sentence, query, SCORE, search.getPath());
					}
				}
			} finally {
			}
		} catch (Exception e) {
			_log.error(e, e);
		}
		_log.debug("no results for " + query.getAnalyzedQuestion());

		return null; // search failed
	}
}
