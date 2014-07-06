#!/bin/bash

#set style line <index> {{linetype | lt} <line_type> | <colorspec>}
#{{linecolor | lc} <colorspec>}
#{{linewidth | lw} <line_width>}
#{{pointtype | pt} <point_type>}
#{{pointsize | ps} <point_size>}
#{{pointinterval | pi} <interval>}
#{palette}

echo "set terminal pdfcairo enhanced color solid font 'Helvetica,10' size 12cm,16cm
set output 'rankExp.pdf'
set datafile separator \"\t\"
set style line 1 lw 3
set style line 2 lw 3
set style line 3 lw 3
set style line 4 lw 3

set multiplot layout 3,2 rowsfirst

set key above
set title 'a)'
set yrange [0:1]
set grid
f(x)=0.5678073510773131
plot 'GOLF_scores_summary.csv' using 1:4 title 'precision' with lines ls 1, 'GOLF_scores_summary.csv' using 1:5 title 'recall' with lines ls 2, 'GOLF_scores_summary.csv' using 1:6 title 'f1-score' with lines ls 3, f(x) title 'baseline' with lines ls 4
set title 'b)'
set ylabel 'needed time (in s)'
set autoscale
set logscale y
f(x)=36.514
plot 'GOLF_times_summary.csv' using 1:(\$3/1000) title 'overall' with lines ls 1, 'GOLF_times_decomp_summary.csv' using 1:(\$3/1000) title 'matrix decomp.' with lines ls 2, 'GOLF_times_clustering_summary.csv' using 1:(\$3/1000) title 'clustering' with lines ls 3, f(x) title 'baseline' with lines ls 4

set key off
set title 'c)'
unset logscale y
unset ylabel
set yrange [0:1]
set grid
f(x)=0.6822742474916388
plot 'REUTERS128_scores_summary.csv' using 1:4 title 'precision' with lines ls 1, 'REUTERS128_scores_summary.csv' using 1:5 title 'recall' with lines ls 2, 'REUTERS128_scores_summary.csv' using 1:6 title 'f1-score' with lines ls 3, f(x) title 'baseline' with lines ls 4
set title 'd)'
set ylabel 'needed time (in s)'
set autoscale
set logscale y
f(x)=5.727
plot 'REUTERS128_times_summary.csv' using 1:(\$3/1000) title 'overall' with lines ls 1, 'REUTERS128_times_decomp_summary.csv' using 1:(\$3/1000) title 'matrix decomp.' with lines ls 2, 'REUTERS128_times_clustering_summary.csv' using 1:(\$3/1000) title 'clustering' with lines ls 3, f(x) title 'baseline' with lines ls 4

set title 'e)'
unset logscale y
unset ylabel
set yrange [0:1]
set xlabel 'rank for matrix decomp.'
set grid
f(x)=0.7930682976554536
plot 'RSS500_scores_summary.csv' using 1:4 title 'precision' with lines ls 1, 'RSS500_scores_summary.csv' using 1:5 title 'recall' with lines ls 2, 'RSS500_scores_summary.csv' using 1:6 title 'f1-score' with lines ls 3, f(x) title 'baseline' with lines ls 4
set title 'f)'
set ylabel 'needed time (in s)'
set autoscale
set logscale y
f(x)=8.593
plot 'RSS500_times_summary.csv' using 1:(\$3/1000) title 'overall' with lines ls 1, 'RSS500_times_decomp_summary.csv' using 1:(\$3/1000) title 'matrix decomp.' with lines ls 2, 'RSS500_times_clustering_summary.csv' using 1:(\$3/1000) title 'clustering' with lines ls 3, f(x) title 'baseline' with lines ls 4

unset multiplot
" | gnuplot
