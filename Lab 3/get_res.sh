#!/bin/bash
# this file gets all the res files for each value of k and b
for k in 0.2 0.4 0.6 0.8 1.0 1.2 1.4 1.6 1.8 2.0
do
	for b in 0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9
	do
		curl -o "bm25-"$k"-"$b".txt" "http://136.206.115.117:8080/IRModelGenerator/res.6.BM25."$k"."$b
	done
done
