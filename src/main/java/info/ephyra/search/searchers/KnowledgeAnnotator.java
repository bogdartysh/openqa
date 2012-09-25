package info.ephyra.search.searchers;

import info.ephyra.search.Searcher;
import info.ephyra.search.SingleThreadSearcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.core.task.entities.Query;


/**
 * <p>
 * A <code>KnowledgeAnnotator</code> searches a (semi)structured knowledge
 * source. It provides a specialized solution to certain classes of questions,
 * described by a set of question patterns. Only questions that match at least
 * one of the patterns in the field <code>qPatterns</code> are supported by a
 * <code>KnowledgeAnnotator</code>.
 * </p>
 * 
 * <p>
 * It runs as a separate thread, so several queries can be performed in
 * parallel.
 * </p>
 * 
 * <p>
 * This class extends the class <code>Searcher</code> and is abstract.
 * </p>
 * 
 * @author Nico Schlaefer
 * @version 2005-09-28
 */
public abstract class KnowledgeAnnotator extends SingleThreadSearcher implements Searcher {
	/** Name of the knowledge annotator. */
	private final String name;
	/**
	 * A question that matches at least one of these patterns can be handled by
	 * the <code>KnowledgeAnnotator</code>.
	 */
	private List<Pattern> qPatterns;
	/**
	 * Strings identifying the relevant content of a question by referring to
	 * the groups in the corresponding question patterns.
	 */
	private List<String> qContents;
	
	private Pattern matcherPattern = Pattern.compile("\\[(\\d*)\\]");
	
	/** Index of the matching pattern. */
	private int index;
	/** The <code>Matcher</code> that matched the pattern with the question. */
	private Matcher matcher;

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
	protected KnowledgeAnnotator(final String name,
			final List<Pattern> qPatterns, final List<String> qContents) {
		this.name = name;
		this.qPatterns = qPatterns;
		this.qContents = qContents;
	}

	/**
	 * <p>
	 * Creates a <code>KnowledgeAnnotator</code> and reads the question patterns
	 * and descriptors of the relevant content of a question from a file.
	 * </p>
	 * 
	 * <p>
	 * The file must have the following format:
	 * </p>
	 * 
	 * <p>
	 * <code>KnowledgeAnnotator::<br>
	 * [name of the knowledge annotator]<br>
	 * <br>
	 * QuestionPatterns:<br>
	 * [regular expression 1]<br>
	 * [relevant content 1]<br>
	 * ...<br>
	 * [regular expression n]<br>
	 * [relevant content n]</code>
	 * </p>
	 * 
	 * <p>
	 * The relevant content of a question is described by a string that may
	 * contain group identifiers of the format <code>[group_no]</code> that are
	 * replaced by the capturing groups that occur in the corresponding question
	 * pattern.
	 * </p>
	 * 
	 * @param filename
	 *            file containing the question patterns and descriptors of the
	 *            relevant content of a question
	 */
	public KnowledgeAnnotator(final String filename) throws IOException {
		final File file = new File(filename);
		try (BufferedReader in = new BufferedReader(new FileReader(file))) {
			// read name of the knowledge
			in.readLine();
			name = in.readLine();
			in.readLine();

			// read answer patterns
			in.readLine();
			while (in.ready()) {
				qPatterns.add(Pattern.compile(in.readLine()));
				qContents.add(in.readLine());
			}
		}
	}

	/**
	 * Tests whether the knowledge annotator is appropriate for a question by
	 * applying the patterns in the field <code>qPatterns</code>.
	 * 
	 * @param query
	 *            <code>Query</code> object
	 * @return true, if the question matches at least one of the patterns in
	 *         <code>qPatterns</code>
	 */
	@Override
	public boolean matches(final Query query) {
		final String question = query.getAnalyzedQuestion().getQuestion();

		for (int i = 0; i < qPatterns.size(); i++) {
			Matcher m = qPatterns.get(i).matcher(question);

			if (m.matches()) {
				index = i;	// save the index of the pattern
				matcher = m;	// save the matcher
				return true;
			}
		}

		return false;
	}

	/**
	 * Extracts the relevant content of a question by resolving the group
	 * identifiers of the format <code>[group_no]</code> in the content string
	 * that corresponds to the matching pattern.
	 * 
	 * @return relevant content of the question
	 */
	protected String getContent() {
		String content = qContents.get(index);


		final Matcher m = matcherPattern.matcher(content);

		// replace all group IDs by the corresponding parts of the question
		while (m.find()) {
			int group = Integer.parseInt(m.group(1));

			content = content.replace(m.group(), matcher.group(group));
		}

		return content;
	}

	/**
	 * Returns the name of the knowledgeAnnotator.
	 * 
	 * @return name of the knowledge annotator
	 */
	public String getKAName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public List<Pattern> getqPatterns() {
		return qPatterns;
	}

	public List<String> getqContents() {
		return qContents;
	}

	public int getIndex() {
		return index;
	}

	public Matcher getMatcher() {
		return matcher;
	}

	/**
	 * @return the matcherPattern
	 */
	public Pattern getMatcherPattern() {
		return matcherPattern;
	}

	/**
	 * @param matcherPattern the matcherPattern to set
	 */
	public void setMatcherPattern(Pattern matcherPattern) {
		this.matcherPattern = matcherPattern;
	}
	
	
	
	
}
