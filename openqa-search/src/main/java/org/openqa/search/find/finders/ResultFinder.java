package org.openqa.search.find.finders;

import java.util.concurrent.Callable;

import info.ephyra.querygeneration.Query;
import info.ephyra.search.Result;



public abstract class ResultFinder implements Callable<Result[]> {
	/** Query that is performed. */
	protected Query query;
	
	public ResultFinder() {
		super();
	}

	public ResultFinder(Query query) {
		super();
		this.query = query;
	}

	public Query getQuery() {
		return query;
	}
	
	public abstract Result[] getResults(final Query query);

}
