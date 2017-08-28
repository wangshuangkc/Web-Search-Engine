package rankers;

import java.util.Collections;
import java.util.Vector;

import documents.Document;
import documents.DocumentFull;
import documents.ScoredDocument;
import indexers.Indexer;
import query.Query;
import query.QueryHandler.CgiArguments;
import engine.SearchEngine.Options;

/**
 * @CS2580: Use this template to implement the phrase rankers for HW1.
 *
 * @author congyu
 * @author fdiaz
 */
public class RankerPhrase extends Ranker {

  public RankerPhrase(Options options, CgiArguments arguments, Indexer indexer) {
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

  private ScoredDocument scoreDocument(Query query, int did)
  {
    Document doc = _indexer.getDoc(did);
    double score = scoreDocument(query, doc);

    return new ScoredDocument(query._query, doc, score);
  }

  public double scoreDocument(Query query, Document doc) {
    double score = 0;
    Vector<String> token = ((DocumentFull) doc).getConvertedBodyTokens();
    if (query._tokens.size() == 1) {
      for (String q : query._tokens) {
        for (String t : token) {
          if (q.equals(t)) {
            score++;
          }
        }
      }
    } else {
      for (int i = 0; i < query._tokens.size() - 1; i++) {
        if (token.size() == 1) {
          if (token.get(0).equals(query._tokens.get(i))) {
            score++;
          }
        } else {
          for (int j = 0; j < token.size() - 1; j++) {
            if (query._tokens.get(i).equals(token.get(j)) &&
                query._tokens.get(i + 1).equals(token.get(j + 1))) {
              score++;
            }
          }
        }
      }
    }

    return score;
  }
}

