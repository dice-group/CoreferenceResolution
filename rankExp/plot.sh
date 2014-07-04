#!/bin/bash


if [ $# -lt 1 ]
then
	echo "wrong number of arguments."
	echo "usage: plot.sh <corpus_name>"
else
echo "set key below
set datafile separator \"\t\"
set yrange [0:1]
set xlabel 'rank for matrix decomposition'
set terminal pngcairo enhanced color solid font 'Helvetica,10' size 1024,768
set output '"$1"_scores.png'
plot '"$1"_scores_summary.csv' using 1:4 title 'precision' with lines, '"$1"_scores_summary.csv' using 1:5 title 'recall' with lines, '"$1"_scores_summary.csv' using 1:6 title 'f1-score' with lines
set ylabel 'needed time (in ms)'
set autoscale
set output '"$1"_runtimes.png'
plot '"$1"_times_summary.csv' using 1:3 title 'overall' with lines, '"$1"_times_decomp_summary.csv' using 1:3 title 'matrix decomposition' with lines, '"$1"_times_clustering_summary.csv' using 1:3 title 'clustering' with lines
" | gnuplot

fi
