 # Web-Search-Engine
Homework for Web Search Engine Course

## Homework 1

### Requirements
**Implement ranking signals**
- cosine similarity (vector space model)
- query likelihood (language model)
- phrase
- numviews
- simple linear model

**Evaluation metrics**
- Precision at 1, 5 and 10
- Recall at 1, 5, and 10
- F0.5 at 1, 5, and 10
- Precision at recall points (0.0, 0.1, 0.2 ... 1.0)
- Average precision
- NDCG at 1, 5, and 10
- Reciprocal rank

### Commands
**Compile:**
<pre>javac src/hw1/*.java</pre>

**Index Phase:**
<pre>java -cp src hw1.SearchEngine --mode=index --options=conf/engine1.conf</pre>

**Start Server:**
<pre>java -cp src -Xmx256m hw1.SearchEngine --mode=serve --port=25801 --options=conf/engine1.conf</pre>

**Run Query:**
<pre>curl 'localhost:25801/search?query=%22web%20search%22&ranker=fullscan'</pre>

**Generate Ranking Result e.g.:**
<pre>curl 'localhost:25801/search?query=bing&ranker=cosine' >> hw1.1-vsm.tsv</pre>

## Homework 2

### Requirements
**Implement indexers**
- Doc only
- Occurance
- Compressed

**Concreate Ranker**
- RankerFavorite

**Improve document and query representations**
- DocumentIndexed
- QueryPhrase

## Homework 3

### Requirements
**Compute document qualities**
- PageRank
- Numviews

**Spearman's rank correlation coefficient**

**Pseudo-Relevance Feedback**
