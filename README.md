# Search Engine

[![IMAGE ALT TEXT HERE](http://img.youtube.com/vi/zCIvku1lT9s/0.jpg)](http://www.youtube.com/watch?v=zCIvku1lT9s)

[![IMAGE ALT TEXT HERE](http://img.youtube.com/vi/YlrLvLgVzqk/0.jpg)](http://www.youtube.com/watch?v=YlrLvLgVzqk)


This is a minified search engine that specializes in discovering the top 10 most relevant documents in the Los Angeles Times Collection. The collection has 136k+ documents but this search engine's performance can retrieve those relevant documents in several milliseconds. 

## This Search Engine Consists of:
* IndexEngine
* Lexicon
* Query Interpreter 
* Snippet Engine
* Ranking Engine 

Note: Does not have a web crawler. 

## Ranking Function: BM25

![BM25](https://i.gyazo.com/34ab79556c3347446a2d95f65bc55770.png)

Future Implementation: Cosine Similarity (Vector Space)

#### Language Modelling: Query Biased Summary

![Dirichlet Smoothing](https://i.gyazo.com/d16fa22dec66137ce1de9894a0a5a69a.png)

* Used Dirichlet Smoothing to avoid the zero probability 

The reason why I used Dirichlet smoothing vs other smoothing techniques such as Jelinek-Mercer is because Dirichlet Smoothing takes into consideration the documents' length and therefore smoothes shorter documents more than longer ones. 

## Indexing

The Index Engine creates an inverted index such that as it is indexing each document, it's also tokenizing each word as an id and mapping it to an postings list. The posting list consists of the document id and the number of times the word appears in that document. Using an inverted index saves a significant amount of space compared to a matrix form. 

