 # WSE-Homework 1
Homework 1 for Web Search Engine Course

Compile:
<pre>javac -cp src/hw1/*.java</pre>

Index Phase:
<pre>java -cp hw1.SearchEngine --mode=index --options=conf/engine1.conf</pre>

Start Server:
<pre>java -cp -Xmx256m hw1.SearchEngine --mode=serve --port=25801 --options=conf/engine1.conf</pre>

Run Query:
<pre>curl 'localhost:25801/search?query=%22web%20search%22&ranker=fullscan'</pre>
