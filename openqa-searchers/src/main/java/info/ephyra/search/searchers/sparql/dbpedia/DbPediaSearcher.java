package info.ephyra.search.searchers.sparql.dbpedia;

import info.ephyra.search.searchers.sparql.SparqlSearcher;

import org.apache.log4j.Logger;
import org.openqa.core.task.entities.Query;
import org.openqa.core.task.entities.Result;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;

/**
 * 
 * @author Bogdan Artyushenko
 */
public abstract class DbPediaSearcher extends SparqlSearcher {
	private Logger _log = Logger.getLogger(DbPediaSearcher.class
			.getCanonicalName());

	private static final float SCORE = 200f;

	/** The URL of the Wikipedia search page. */
	private static final String URL = "http://dbpedia.org/sparql";

	private static final String PREFIXES = "PREFIX p: <http://dbpedia.org/property/>"
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/>"
			+ "PREFIX category: <http://dbpedia.org/resource/Category:>"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
			+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
			+ "PREFIX geo: <http://www.georss.org/georss/>";

	@Override
	public Result findResult(final Query query) {
		Result result = null;
		com.hp.hpl.jena.query.Query querySparq = getSparqlQuery(query);
		if (querySparq == null) {
			_log.warn("returnin null, s.a. failed to generate a SPARQL query");
			return null;
		}
		// initializing queryExecution factory with remote service.
		// **this actually was the main problem I couldn't figure out.**
		QueryExecution qexec = null;

		// after it goes standard query execution and result processing which
		// can
		// be found in almost any Jena/SPARQL tutorial.
		try {
			qexec = QueryExecutionFactory.sparqlService(URL, querySparq);
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {

				// Result processing is done here.
			}
		} catch (Exception e) {
			_log.error(e, e);
		} finally {
			if (qexec != null)
				try {
					qexec.close();
				} catch (Exception e) {
					_log.error(e, e);
				}
		}
		return result;

	}

	@Override
	public String getURL() {
		return URL;
	}

	@Override
	public String getPrefixes() {
		return PREFIXES;
	}
}
