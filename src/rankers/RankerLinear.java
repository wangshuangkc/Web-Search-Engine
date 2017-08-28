package rankers;

import java.util.Collections;
import java.util.Vector;

import documents.Document;
import documents.ScoredDocument;
import query.QueryHandler.CgiArguments;
import engine.SearchEngine.Options;
import indexers.Indexer;
import query.Query;

public class RankerLinear extends Ranker {
  private float _betaCosine = 1.0f;
  private float _betaQl = 1.0f;
  private float _betaPhrase = 1.0f;
  private float _betaNumviews = 1.0f;

  public RankerLinear(Options options, CgiArguments arguments, Indexer indexer) {
    super(options, arguments, indexer);
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
    _betaCosine = options._betaValues.get("beta_cosine");
    _betaQl = options._betaValues.get("beta_ql");
    _betaPhrase = options._betaValues.get("beta_phrase");
    _betaNumviews = options._betaValues.get("beta_numviews");
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {
    System.out.println("  with beta values" +
        ": cosine=" + Float.toString(_betaCosine) +
        ", ql=" + Float.toString(_betaQl) +
        ", phrase=" + Float.toString(_betaPhrase) +
        ", numviews=" + Float.toString(_betaNumviews));
    double maxCosScore = 0.0;
    double minCosScore = Double.MAX_VALUE;
    double maxQlScore = 0.0;
    double minQlScore = Double.MAX_VALUE;
    double maxPhraseScore = 0.0;
    double minPhraseScore = Double.MAX_VALUE;
    double maxNumviewsScore = 0.0;
    double minNumviewsScore = Double.MAX_VALUE;

    RankerCosine cos = new RankerCosine(_options, _arguments, _indexer);
    RankerQl ql = new RankerQl(_options, _arguments, _indexer);
    RankerPhrase phrase = new RankerPhrase(_options, _arguments, _indexer);
    RankerNumviews numviews = new RankerNumviews(_options, _arguments, _indexer);

    for (int i = 0; i < _indexer.numDocs(); ++i) {
      Document doc = _indexer.getDoc(i);

      double cosScore = cos.scoreDocument(query, doc);
      maxCosScore = Math.max(maxCosScore, cosScore);
      minCosScore = Math.min(minCosScore, cosScore);
      double qlScore = ql.scoreDocument(query, doc);
      maxQlScore = Math.max(maxQlScore, qlScore);
      minQlScore = Math.min(minQlScore, qlScore);
      double phraseScore = phrase.scoreDocument(query, doc);
      maxPhraseScore = Math.max(maxPhraseScore, phraseScore);
      minPhraseScore = Math.min(minPhraseScore, phraseScore);
      double numviewsScore = numviews.scoreDocument(doc);
      maxNumviewsScore = Math.max(maxNumviewsScore,numviewsScore);
      minNumviewsScore = Math.min(minNumviewsScore, numviewsScore);
    }

    Vector<ScoredDocument> all = new Vector<>();
    for (int i = 0; i < _indexer.numDocs(); ++i) {
      Document doc = _indexer.getDoc(i);

      double cosScore = calScore(cos.scoreDocument(query, doc), maxCosScore, minCosScore);
      double qlScore = calScore(ql.scoreDocument(query, doc), maxQlScore, minQlScore);
      double phraseScore = calScore(phrase.scoreDocument(query, doc), maxPhraseScore, minPhraseScore);
      double numviewsScore = calScore(numviews.scoreDocument(doc), maxNumviewsScore, minNumviewsScore);

      double score = _betaCosine * cosScore + _betaQl * qlScore +
          _betaPhrase * phraseScore + _betaNumviews * numviewsScore;
      all.add(new ScoredDocument(query._query, doc, score));
    }

    Collections.sort(all, Collections.reverseOrder());
    Vector<ScoredDocument> results = new Vector<>();
    for (int i = 0; i < all.size() && i < numResults; ++i) {
      results.add(all.get(i));
    }

    return results;
  }

  private double calScore(double score, double maxScore, double minScore) {
    if (maxScore > minScore) {
      return (score - minScore) / (maxScore - minScore);
    }

    return 0;
  }
}

