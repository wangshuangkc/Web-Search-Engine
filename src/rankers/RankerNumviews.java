package rankers;

import java.util.Collections;
import java.util.Vector;

import documents.Document;
import documents.ScoredDocument;
import indexers.Indexer;
import query.Query;
import query.QueryHandler.CgiArguments;
import engine.SearchEngine.Options;

public class RankerNumviews extends Ranker {

  public RankerNumviews(Options options, CgiArguments arguments, Indexer indexer) {
    super(options, arguments, indexer);
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {
    Vector<ScoredDocument> all = new Vector<>();
    for (int i = 0; i < _indexer.numDocs(); ++i) {
      all.add(scoreDocument(query, i));
    }
    Collections.sort(all, Collections.reverseOrder());
    Vector<ScoredDocument> results = new Vector<>();
    for (int i = 0; i < all.size() && i < numResults; ++i) {
      results.add(all.get(i));
    }
    return results;
  }

  private ScoredDocument scoreDocument(Query query, int did) {
    Document doc = _indexer.getDoc(did);
    double score = scoreDocument(doc);

    return new ScoredDocument(query._query, doc, score);
  }

  public double scoreDocument(Document doc) {
    return doc.getNumViews();
  }
}

