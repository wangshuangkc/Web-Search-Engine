package hw1;

import java.util.Vector;

import hw1.QueryHandler.CgiArguments;
import hw1.SearchEngine.Options;

/**
 * @CS2580: Use this template to implement the numviews ranker for HW1.
 *
 * @author congyu
 * @author fdiaz
 */
public class RankerNumviews extends Ranker {

  public RankerNumviews(Options options,
                        CgiArguments arguments, Indexer indexer) {
    super(options, arguments, indexer);
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {
    Vector<ScoredDocument> all = new Vector<ScoredDocument>();
    // @CS2580: fill in your code here.
    return all;
  }
}

