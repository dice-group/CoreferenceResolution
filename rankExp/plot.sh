#!/bin/bash

if [ $# -lt 1 ]
then
	echo "wrong number of arguments."
	echo "usage: plot.sh <corpus_name> [f1-score-baseline] [runtime-baseline]"
else
cmd="set key below\n
set datafile separator \"\t\"\n
set yrange [0:1]\n
set xlabel 'rank for matrix decomposition'\n
set grid\n
set terminal pdfcairo enhanced color solid font 'Helvetica,10' size 12cm,8cm\n
set output '"$1"_rankExp_scores.pdf'"
	if [ $# -gt 1 ]
	then
		cmd=$cmd"\n
f(x)="$2"\n
plot '"$1"_scores_summary.csv' using 1:4 title 'precision' with lines, '"$1"_scores_summary.csv' using 1:5 title 'recall' with lines, '"$1"_scores_summary.csv' using 1:6 title 'f1-score' with lines, f(x) title 'baseline' with lines"
	else
		cmd=$cmd"\n
plot '"$1"_scores_summary.csv' using 1:4 title 'precision' with lines, '"$1"_scores_summary.csv' using 1:5 title 'recall' with lines, '"$1"_scores_summary.csv' using 1:6 title 'f1-score' with lines"
	fi

cmd=$cmd"\n
set ylabel 'needed time (in s)'\n
set autoscale\n
set logscale y\n
set output '"$1"_rankExp_runtimes.pdf'"
	if [ $# -gt 2 ]
	then
		cmd=$cmd"\n
f(x)="$3"/1000\n
plot '"$1"_times_summary.csv' using 1:(\$3/1000) title 'overall' with lines, '"$1"_times_decomp_summary.csv' using 1:(\$3/1000) title 'matrix decomposition' with lines, '"$1"_times_clustering_summary.csv' using 1:(\$3/1000) title 'clustering' with lines, f(x) title 'baseline' with lines\n
"
	else
		cmd=$cmd"\n
plot '"$1"_times_summary.csv' using 1:3 title 'overall' with lines, '"$1"_times_decomp_summary.csv' using 1:3 title 'matrix decomposition' with lines, '"$1"_times_clustering_summary.csv' using 1:3 title 'clustering' with lines\n
"
	fi
echo -e $cmd | gnuplot

fi

#set terminal pngcairo enhanced color solid font 'Helvetica,10' size 1024,768
