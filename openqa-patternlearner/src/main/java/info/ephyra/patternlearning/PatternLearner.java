package info.ephyra.patternlearning;

import info.ephyra.answerselection.filters.AnswerPatternFilterUtils;
import info.ephyra.io.MsgPrinter;


import info.ephyra.questionanalysis.QuestionInterpretation;
import info.ephyra.questionanalysis.QuestionInterpreter;
import info.ephyra.questionanalysis.QuestionNormalizer;
import info.ephyra.trec.TREC8To12Parser;
import info.ephyra.trec.TRECAnswer;
import info.ephyra.trec.TRECPattern;
import info.ephyra.trec.TRECQuestion;
import info.ephyra.util.RegexConverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;



/**
 * A pattern learning tool for Ephyra.
 * 
 * @author Nico Schlaefer
 * @version 2006-04-04
 */
public class PatternLearner {
	/** Support threshold for answer patterns. */
	private static final float SUPPORT_THRESH = 0.0001f;
	/** Confidence threshold for answer patterns. */
	private static final float CONFIDENCE_THRESH = 0.01f;
	/** Question strings. */
	private static String[] qss;
	/** Maps questions or query strings to answers. */
	private static Hashtable<String, String> ass;
	/** Maps questions or query strings to patterns for correct answers. */
	private static Hashtable<String, String> regexs;

	/**
	 * Loads the questions, answers and patterns from TREC files.
	 * 
	 * @param qFile
	 *            name of the file containing the questions
	 * @param aFile
	 *            name of the file containing the answers or an empty string
	 * @param pFile
	 *            name of the file containing the patterns or an empty string
	 */
	private static void loadTRECData(String qFile, String aFile, String pFile) {
		ass = new Hashtable<String, String>();
		regexs = new Hashtable<String, String>();

		// load questions from file
		TRECQuestion[] questions = TREC8To12Parser.loadQuestions(qFile);
		qss = new String[questions.length];
		for (int i = 0; i < questions.length; i++)
			qss[i] = questions[i].getQuestionString();

		// if an answer file is provided,
		// load answers and derive patterns
		if (!aFile.equals("")) {
			TRECAnswer[] answers = TREC8To12Parser.loadTREC9Answers(aFile);
			String answer;

			for (int i = 0; i < questions.length; i++) {
				answer = answers[i].getAnswerString();
				ass.put(qss[i], answer);
				if (pFile.equals(""))
					regexs.put(qss[i], RegexConverter.strToRegex(answer));
			}
		}

		// if a patterns file is provided,
		// load patterns and derive answer strings
		if (!pFile.equals("")) {
			TRECPattern[] patterns = TREC8To12Parser.loadPatternsAligned(pFile);
			String pattern;

			for (int i = 0; i < questions.length; i++)
				if ((i < patterns.length) && (patterns[i] != null)) {
					pattern = patterns[i].getRegexs()[0];
					regexs.put(qss[i], pattern);
					if (aFile.equals(""))
						ass.put(qss[i], RegexConverter.regexToQueryStr(pattern));
				}
		}
	}

	/**
	 * Interprets the questions and writes target-context-answer-regex tuples to
	 * resource files.
	 * 
	 * @param dir
	 *            target directory
	 * @return <code>true</code>, iff the interpretations could be written to
	 *         resource files
	 */
	private static boolean interpretQuestions(String dir) {
		boolean success = true;

		for (int i = 0; i < qss.length; i++) {
			// print original question string
			MsgPrinter.printQuestionString(qss[i]);

			// normalize question
			String qn = QuestionNormalizer.normalize(qss[i]);
			// stem verbs and nouns
			String stemmed = QuestionNormalizer.stemVerbsAndNouns(qn);

			// print normalized and stemmed question string
			MsgPrinter.printNormalization(stemmed);

			// interpret question
			QuestionInterpretation[] qis = QuestionInterpreter.interpret(qn,
					stemmed);
			MsgPrinter.printInterpretations(qis);

			for (QuestionInterpretation qi : qis)
				if (!saveInterpretation(dir, qi, ass.get(qss[i]),
						regexs.get(qss[i])))
					success = false;
		}

		return success;
	}

	/**
	 * Saves a question interpretation, an answer string and a regular
	 * expression that describes a correct answer to a file.
	 * 
	 * @param dir
	 *            target directory
	 * @param qi
	 *            question interpretation
	 * @param as
	 *            answer string
	 * @param regex
	 *            regular expression
	 * @return <code>true</code>, iff the tuple could be saved
	 */
	private static boolean saveInterpretation(String dir,
			QuestionInterpretation qi, String as, String regex) {
		try {
			File file = new File(dir + "/" + qi.getProperty());

			// form tuple
			String tuple = qi.getTarget();
			for (String context : qi.getContext())
				tuple += "#" + context;
			tuple += "#" + as;
			tuple += "#" + regex;

			// first check if the tuple already exists in the file
			if (file.exists()) {
				try (BufferedReader in = new BufferedReader(
						new FileReader(file))) {
					while (in.ready())
						if (tuple.equalsIgnoreCase(in.readLine()))
							return true;
				}
			}

			// append new tuple
			PrintWriter out = new PrintWriter(new FileOutputStream(file, true));
			out.println(tuple);
			out.close();
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	
	
	/**
	 * Saves answer patterns to resource files.
	 * 
	 * @param dir
	 *            target directory
	 * @return <code>true</code>, iff the answer patterns could be saved
	 */
	private static boolean savePatterns(String dir) {
		return AnswerPatternFilterUtils.savePatterns(dir);
	}

	/**
	 * Loads answer patterns from resource files.
	 * 
	 * @param dir
	 *            directory containing the answer patterns
	 * @return <code>true</code>, iff the answer patterns could be loaded
	 */
	private static boolean loadPatterns(String dir) {
		return AnswerPatternFilterUtils.loadPatterns(dir);
	}

	

	/**
	 * Drops answer patterns that have a low support or confidence.
	 */
	private static void filterPatterns() {
		// drop answer patterns that have a low support
		AnswerPatternFilterUtils.dropLowSupport(SUPPORT_THRESH);

		// drop answer patterns that have a low confidence
		AnswerPatternFilterUtils.dropLowConfidence(CONFIDENCE_THRESH);
	}

	/**
	 * Loads the TREC data, interprets the questions and writes
	 * target-context-answer-regex tuples to files.
	 * 
	 * @param qFile
	 *            name of the file containing the questions
	 * @param aFile
	 *            name of the file containing the answers or an empty string
	 * @param pFile
	 *            name of the file containing the patterns or an empty string
	 * @return <code>true, iff the TREC data could be interpreted
	 */
	public static boolean interpret(String qFile, String aFile, String pFile) {
		// load TREC data
		MsgPrinter.printLoadingTRECData();
		loadTRECData(qFile, aFile, pFile);

		// interpret TREC questions and save interpretations to files
		MsgPrinter.printInterpretingQuestions();
		return interpretQuestions("res/patternlearning/interpretations");
	}

	/**
	 * Loads answer patterns from resource files, drops patterns with a low
	 * support or confidence and writes the remaining patterns back to resource
	 * files.
	 * 
	 * @return <code>true</code>, iff the answer patterns could be filtered
	 */
	public static boolean filter() {
		// load answer patterns
		MsgPrinter.printLoadingPatterns();
		if (!loadPatterns("res/patternlearning/answerpatterns_assess"))
			return false;

		// drop patterns with low support/confidence
		MsgPrinter.printFilteringPatterns();
		filterPatterns();

		// save answer patterns
		MsgPrinter.printSavingPatterns();
		return savePatterns("res/patternlearning/answerpatterns");
	}

}
