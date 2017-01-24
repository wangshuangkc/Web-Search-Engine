package hw1;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import hw1.QueryHandler.CgiArguments;
import hw1.SearchEngine.Options;

/**
 * @CS2580: Use this template to implement the query likelihood ranker for HW1.
 *
 * @author congyu
 * @author fdiaz
 */
public class RankerQl extends Ranker {

  public static final double LAMBDA = 0.5;

  public RankerQl(Options options,
                  CgiArguments arguments, Indexer indexer) {
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
    double score = scoreDocument(query, doc);

    return new ScoredDocument(query._query, doc, score);
  }

  private double scoreDocument(Query query, Document doc) {
    Vector<String> queryTokens = query._tokens;
    Map<String, Integer> queryVec = getVec(queryTokens);

    Vector<String> docTokens = ((DocumentFull) doc).getConvertedBodyTokens();
    Map<String, Integer> docVec = getVec(docTokens);

    Map<String, Integer> docTermFrequencyVec = new HashMap<>();
    for (String key : queryVec.keySet()) {
      if (docVec.containsKey(key)) {
        docTermFrequencyVec.put(key, docVec.get(key));
      } else {
        docTermFrequencyVec.put(key, 0);
      }
    }

    long corpusTotalTerm = _indexer.totalTermFrequency();
    long docTotalTerm = docTokens.size();
    double score = 0;
    for (String key : docTermFrequencyVec.keySet()) {
      score += (1 - LAMBDA) * (docTermFrequencyVec.get(key) / docTotalTerm) +
          LAMBDA * (_indexer.corpusTermFrequency(key) / corpusTotalTerm);
    }

    return score;
  }

  private Map<String,Integer> getVec(Vector<String> tokens) {
    Map<String, Integer> vec = new HashMap<>();
    for (String token : tokens) {
      if (!vec.containsKey(token)) {
        vec.put(token, 1);
      } else {
        vec.put(token, vec.get(token) + 1);
      }
    }

    return vec;
  }

  public static void main(String[] args) throws IOException, ClassNotFoundException {
    Options op = new Options("conf/engine1.conf");
    CgiArguments arg = new CgiArguments("query=house&ranker=ql");
    Indexer indexer = Indexer.Factory.getIndexerByOption(op);
    indexer.loadIndex();
    RankerQl ql = new RankerQl(op, arg, indexer);
    Query processedQuery = new Query(arg._query);
    processedQuery.processQuery();

    // Ranking.
    ql.runQuery(processedQuery, 10);
  }
}

