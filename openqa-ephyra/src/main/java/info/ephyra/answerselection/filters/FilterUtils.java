package info.ephyra.answerselection.filters;

import info.ephyra.util.RegexConverter;
import info.ephyra.nlp.indices.Prepositions;

public class FilterUtils {
	/**
	 * Special characters that are truncated from answer strings. The following
	 * characters are excluded because they may be the first/last character of
	 * an answer:
	 * <ul>
	 * <li>$������%~.</li>
	 * <li>��������</li>
	 * <li>��������������������������������������������������������������</li>
	 * </ul>
	 */
	private static final String SPECIAL_CHARS =
		RegexConverter.strToRegex("-+�*��=_�|�\\/�:,;�?�!��\"���'�`" +
								  "()[]{}<>#&�@���");
	/** Articles that are truncated from answer strings. */
	private static final String ARTICLES = "(an?|that|the|these|this|those)";

	/**
	 * Truncates a phrase.
	 * 
	 * @param phrase phrase to truncate
	 * @return truncated phrase
	 */
	public static String truncate(String phrase) {
		String old = "";
		
		while (!old.equals(phrase)) {
			 old = phrase;
			
			// drop leading and trailing blanks and some special characters
			phrase = phrase.replaceFirst("^[\\s" + SPECIAL_CHARS + "]", "");
			phrase = phrase.replaceFirst("[\\s" + SPECIAL_CHARS + "]$", "");
			
			// drop leading '.' and trailing '.' if not preceded by an
			// upper-case character (which indicates an acronym)
			phrase = phrase.replaceFirst("^\\.", "");
			if (phrase.matches(".*?(^|[^A-Z])\\.$"))
				phrase = phrase.replaceFirst("\\.$", "");
			
			// drop leading and trailing articles
			phrase = phrase.replaceFirst("(?i)^" + ARTICLES + " ", "");
			phrase = phrase.replaceFirst("(?i) " + ARTICLES + "$", "");
			
			// drop leading and trailing "and", "or"
			phrase = phrase.replaceFirst("(?i)^(and|or) ", "");
			phrase = phrase.replaceFirst("(?i) (and|or)$", "");
			
			// drop leading and trailing prepositions
			String[] tokens = phrase.split(" ", -1);
			if (Prepositions.lookup(tokens[0]))
				phrase = phrase.replaceFirst("^[^ ]++($| )", "");
			if (Prepositions.lookup(tokens[tokens.length - 1]))
				phrase = phrase.replaceFirst("(^| )[^ ]++$", "");
		}
		
		return phrase;
	}

	
}
