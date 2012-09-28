package info.ephyra.search.searchers.sparql.dbpedia.customquery;

import org.openqa.core.enums.PropertyEnum;
import org.openqa.core.enums.TargetTypeEnum;
import org.openqa.core.task.entities.Query;
import org.openqa.core.task.entities.Result;

import info.ephyra.questionanalysis.QuestionInterpretation;
import info.ephyra.search.Searcher;
import info.ephyra.search.searchers.sparql.dbpedia.DbPediaSearcher;

public class DbPediaDOBSearcher extends DbPediaSearcher implements Searcher {

	@Override
	public boolean matches(final Query query) {
		final QuestionInterpretation interpretation = query.getInterpretation();
		if (interpretation != null) {
			return TargetTypeEnum.PERSON.name().equals(
					interpretation.getTarget())
					&& PropertyEnum.DATEOFBIRTH.name().equals(
							interpretation.getProperty());
		}

		boolean hasAny = false;
		for (QuestionInterpretation interpt : query.getAnalyzedQuestion()
				.getInterpretations())
			if (interpt != null)
				if (TargetTypeEnum.PERSON.name().equals(interpt.getTarget())
						&& PropertyEnum.DATEOFBIRTH.name().equals(
								interpt.getProperty())) {
					hasAny = true;
					break;
				}
		return hasAny;
	}

	@Override
	public String getSparqlQueryString(final Query query) {
		return "SELECT ?birth WHERE {  ?person foaf:name ?name}  FILTER( lang(?name ) = \"en\") ORDER BY ?name";
	}

	@Override
	public Result findResult(Query queries) {		
		return null;
	}

}
