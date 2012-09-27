/**
 * 
 */
package info.ephyra.search;

import java.util.List;

/**
 * @author bogdan Artyushenko
 *
 */
public interface SearchAgregator extends Searcher {
	/**
	 * @return the searchers
	 */
	public List<Searcher> getSearchers() ;

	/**
	 * @param searchers
	 *            the searchers to set
	 */
	public void setSearchers(final List<Searcher> searchers) ;
}
