#!/bin/bash
# this file gives us the map values for each file
for k in 0.2 0.4 0.6 0.8 1.0 1.2 1.4 1.6 1.8 2.0
do
	for b in 0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9
	do
		echo "k = "$k "b = "$b
		./trec_eval lab/qrels.trec678.adhoc "lab/bash/bm25-"$k"-"$b".res" | grep "^map"
	done
done
