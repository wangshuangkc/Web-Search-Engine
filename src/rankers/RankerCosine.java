package rankers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import documents.Document;
import documents.DocumentFull;
import documents.ScoredDocument;
import indexers.Indexer;
import query.Query;
import query.QueryHandler.CgiArguments;
import engine.SearchEngine.Options;

public class RankerCosine extends Ranker {

  public RankerCosine(Options options, CgiArguments arguments, Indexer indexer) {
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

  public double scoreDocument(Query query, Document doc) {
    Vector<String> queryTokens = query._tokens;
    Map<String, Integer> queryVec = getVec(queryTokens);

    Vector<String> docTokens = ((DocumentFull) doc).getConvertedBodyTokens();
    Map<String, Integer> docVec = getVec(docTokens);

    double score = 0.0;
    double sumProduct = 0.0;
    double sumQueryWeightSq = 0.0;

    for (String key : queryVec.keySet()) {
      double idf = 1 +
          Math.log(_indexer.numDocs() / _indexer.corpusDocFrequencyByTerm(key))
              / Math.log(2);
      int tf = docVec.containsKey(key) ? docVec.get(key) : 0;
      double docWeight = tf * idf;
      double queryWeight = queryVec.get(key) * idf;
      sumQueryWeightSq += queryWeight * queryWeight;
      sumProduct += queryWeight * docWeight;
    }

    double sumDocWeightSq = 0.0;
    for (String key : docVec.keySet()) {
      double idf = 1 +
          Math.log(_indexer.numDocs() / _indexer.corpusDocFrequencyByTerm(key))
              / Math.log(2);
      int tf = docVec.get(key);
      double weight = idf * tf;
      sumDocWeightSq += weight * weight;
    }
    if (sumDocWeightSq * sumQueryWeightSq != 0) {
      score = sumProduct / (Math.sqrt(sumQueryWeightSq) * Math.sqrt(sumDocWeightSq));
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
}
