# Search Engine

[1st DEMO](https://youtu.be/zCIvku1lT9s)

[2nd DEMO](https://youtu.be/YlrLvLgVzqk)

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

### Future Implementation: 

#### Cosine Similarity (Vector Space)

![Cosine](https://i.gyazo.com/b58e22b21e3e9d7844e3d8e104c8b414.png)

![Dk](https://i.gyazo.com/bfde795778ec9949c72240008684daa8.png)

#### Language Modelling

![Dirichlet Smoothing](https://i.gyazo.com/d16fa22dec66137ce1de9894a0a5a69a.png)

* Dirichlet Smoothing to avoid the zero probability 

The reason why I will choose Dirichlet smoothing vs other smoothing techniques such as Jelinek-Mercer is because Dirichlet Smoothing takes into consideration the documents' length and therefore smoothes shorter documents more than longer ones. 

## Indexing

The Index Engine creates an inverted index such that as it is indexing each document, it's also tokenizing each word as an id and mapping it to an postings list. The posting list consists of the document id and the number of times the word appears in that document. Using an inverted index saves a significant amount of space compared to a matrix form. 

## Snippets

#### Query Biased Summary 

The snippet summaries implemented underneath each document are biased towards the a given query and thus dynamic. In other words, the snippet will change depending on the query that the user inputs. 

