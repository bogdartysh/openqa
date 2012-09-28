package info.ephyra.search.searchers.sparql;

import org.apache.log4j.Logger;
import org.openqa.core.task.entities.Query;

import com.hp.hpl.jena.query.QueryFactory;

import info.ephyra.search.Searcher;
import info.ephyra.search.SingleThreadSearcher;

public abstract class SparqlSearcher extends SingleThreadSearcher implements
		Searcher {
	private Logger _log = Logger.getLogger(SparqlSearcher.class
			.getCanonicalName());
	
	public String getLanguage() {
		return "en";
	}

	public String getLanguageFilter(final String parameter) {
		final StringBuilder strBuilder = new StringBuilder(25);
		strBuilder.append("FILTER ( lang(?");
		strBuilder.append(parameter);
		strBuilder.append(")=\"");
		strBuilder.append(getLanguage());
		strBuilder.append("\")");
		return strBuilder.toString();
	}
	
	public abstract String getURL();

	public abstract String getPrefixes();

	/**
	 * creating query object
	 * 
	 * @param query
	 * @return
	 */
	public com.hp.hpl.jena.query.Query getSparqlQuery(final Query query) {
		final String queryString = getSparqlQueryString(query);
		if (queryString == null) {
			_log.warn("failed to create a query");
			return null;
		}
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(getPrefixes());
		strBuilder.append(queryString);
		
		return QueryFactory.create(strBuilder.toString());
	}

	public abstract String getSparqlQueryString(final Query query);
}
