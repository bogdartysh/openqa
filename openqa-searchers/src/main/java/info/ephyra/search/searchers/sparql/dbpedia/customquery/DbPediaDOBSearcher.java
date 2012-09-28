package info.ephyra.search.searchers.sparql.dbpedia.customquery;

import org.openqa.core.task.entities.Query;

import info.ephyra.questionanalysis.QuestionInterpretation;
import info.ephyra.search.Searcher;
import info.ephyra.search.searchers.sparql.dbpedia.DbPediaSearcher;

public class DbPediaDOBSearcher extends DbPediaSearcher implements Searcher {

	@Override
	public boolean matches(final Query query) {
		final QuestionInterpretation interpretation = query.getInterpretation();
		if (interpretation!= null) {
		//	if (interpretation.getProperty() == )
		}
		return false;
	}

	@Override
	public String getSparqlQueryString(final Query query) {
		// TODO Auto-generated method stub
		return null;
	}



}
