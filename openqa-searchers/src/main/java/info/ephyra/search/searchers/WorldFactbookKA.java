package info.ephyra.search.searchers;

import info.ephyra.nlp.SentenceExtractor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openqa.core.task.entities.Query;
import org.openqa.core.task.entities.Result;

/**
 * <p>
 * A <code>KnowledgeAnnotator</code> for the CIA World Factbook. It answers a
 * question about a country by extracting the information from the web page for
 * that country.
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
public class WorldFactbookKA extends KnowledgeAnnotator {
	private Logger _log = Logger.getLogger(WorldFactbookKA.class
			.getCanonicalName());
	private static final float SCORE = 100f;
	
	/** The URL of the CIA World Factbook. */
	private static final String URL = "https://www.cia.gov/library/publications/the-world-factbook/";

	private static final Pattern FACT_PATTERN = Pattern
			.compile(".*<option\\s*value=\"(.*)\"\\s*>(.*)" + "</option>.*");
	/** Country names and corresponding web pages. */
	private Hashtable<String, String> countries = new Hashtable<String, String>();

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
	protected WorldFactbookKA(String name, ArrayList<Pattern> qPatterns,
			ArrayList<String> qContents) {
		super(name, qPatterns, qContents);
	}

	/**
	 * <p>
	 * Creates a <code>WorldFactbookKA</code> and calls the constructor of the
	 * superclass that reads the question patterns from a file.
	 * </p>
	 * 
	 * <p>
	 * Furthermore, a list of the available countries and the URLs of the
	 * corresponding web pages are retrieved from the Factbook.
	 * </p>
	 * 
	 * @param filename
	 *            file containing the question patterns
	 */
	public WorldFactbookKA(final String filename) throws IOException {
		super(filename);

		try {
			URL factbook = new URL(URL); // URL of the main page

			String line;

			try (BufferedReader in = new BufferedReader(new InputStreamReader(
					factbook.openStream(), Charset.forName("iso-8859-1")))) {

				Matcher m;
				while (in.ready()) {
					line = in.readLine();

					m = FACT_PATTERN.matcher(line);
					if (m.matches()) // (country, url) pair found
						countries.put(m.group(2).toLowerCase(), m.group(1));
				}
			}
		} catch (Exception e) {
			_log.error(e, e);
		}
	}

	/**
	 * Searches the World Factbook for country details and returns an array
	 * containing a single <code>Result</code> object or an empty array, if the
	 * search failed.
	 * 
	 * @return array containing a single <code>Result</code> or an empty array
	 */
	@Override
	public Result findResult(final Query query) {
		try {
			// get country name and demanded information

			String[] content = getContent().split("#");
			if (content == null || content.length < 2) {
				_log.warn("content is null or of not enaugh length");
				return null;
			}

			String info = content[0];
			String country = content[1];

			// get URL of country web page

			String countryPage = countries.get(country.toLowerCase());
			if (countryPage == null) {
				_log.warn("contry page is null");
				return null;
			}
			URL page = new URL(URL + countryPage);

			// retrieve document

			String html = "";
			try (BufferedReader in = new BufferedReader(new InputStreamReader(
					page.openStream(), Charset.forName("iso-8859-1")))) {

				while (in.ready()) {
					html += in.readLine() + " ";
				}
			}

			// extract information
			Pattern p = Pattern.compile("(?i).*" + info + ":</div>\\s*</td>"
					+ "\\s*<td .*?>(.*?)</td>.*");
			Matcher m = p.matcher(html);
			if (m.matches()) {
				// extract sentence
				String sentence = SentenceExtractor.getSentencesFromHtml(m
						.group(1))[0];

				// create result from that sentence
				return new Result(sentence, query);
			}
		} catch (Exception e) {
			_log.error(e, e);
		}
		_log.debug("no results for " + query.getAnalyzedQuestion());

		return null; // search failed
	}
}
