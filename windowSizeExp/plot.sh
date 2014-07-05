#!/bin/bash


if [ $# -lt 1 ]
then
	echo "wrong number of arguments."
	echo "usage: plot.sh <corpus_name>"
else
echo "set key below
set datafile separator \"\t\"
set yrange [0:1]
set xlabel 'window size'
set terminal pngcairo enhanced color solid font 'Helvetica,10' size 1024,768
set output '"$1"_scores.png'
plot '"$1"_scores_summary.csv' using 1:4 title 'Precision' with lines, '"$1"_scores_summary.csv' using 1:5 title 'Recall' with lines, '"$1"_scores_summary.csv' using 1:6 title 'F1-score' with lines, '"$1"_baseline_scores_summary.csv' using 1:4 title 'BL Precision' with lines, '"$1"_baseline_scores_summary.csv' using 1:5 title 'BL Recall' with lines, '"$1"_baseline_scores_summary.csv' using 1:6 title 'BL F1-score' with lines
set ylabel 'needed time (in ms)'
set autoscale
set logscale y
set output '"$1"_runtimes.png'
plot '"$1"_times_summary.csv' using 1:3 title 'Overall' with lines, '"$1"_times_decomp_summary.csv' using 1:3 title 'Matrix Decomposition' with lines, '"$1"_times_clustering_summary.csv' using 1:3 title 'Clustering' with lines, '"$1"_baseline_times_summary.csv' using 1:3 title 'Baseline' with lines
" | gnuplot

fi
