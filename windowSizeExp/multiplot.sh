#!/bin/bash

#set style line <index> {{linetype | lt} <line_type> | <colorspec>}
#{{linecolor | lc} <colorspec>}
#{{linewidth | lw} <line_width>}
#{{pointtype | pt} <point_type>}
#{{pointsize | ps} <point_size>}
#{{pointinterval | pi} <interval>}
#{palette}

echo "set terminal pdfcairo enhanced color solid font 'Helvetica,10' size 12cm,16cm
set output 'windowSizeExp.pdf'
set datafile separator \"\t\"
set style line 1 lw 3
set style line 2 lw 3
set style line 3 lw 3
set style line 4 lw 3
set style line 5 lw 3
set style line 6 lw 3

set multiplot layout 3,2 columnsfirst

set yrange [0:1]
set grid

set title 'a)'
set key above
plot 'GOLF_scores_summary.csv' using 1:4 title 'Precision' with lines ls 1, 'GOLF_scores_summary.csv' using 1:5 title 'Recall' with lines ls 2, 'GOLF_scores_summary.csv' using 1:6 title 'F1-score' with lines ls 3, 'GOLF_baseline_scores_summary.csv' using 1:4 title 'BL Precision' with lines ls 6, 'GOLF_baseline_scores_summary.csv' using 1:5 title 'BL Recall' with lines ls 5, 'GOLF_baseline_scores_summary.csv' using 1:6 title 'BL F1-score' with lines ls 4
set title 'c)'
set key off
plot 'REUTERS128_scores_summary.csv' using 1:4 title 'Precision' with lines ls 1, 'REUTERS128_scores_summary.csv' using 1:5 title 'Recall' with lines ls 2, 'REUTERS128_scores_summary.csv' using 1:6 title 'F1-score' with lines ls 3, 'REUTERS128_baseline_scores_summary.csv' using 1:4 title 'BL Precision' with lines ls 6, 'REUTERS128_baseline_scores_summary.csv' using 1:5 title 'BL Recall' with lines ls 5, 'REUTERS128_baseline_scores_summary.csv' using 1:6 title 'BL F1-score' with lines ls 4
set title 'e)'
set xlabel 'window size'
plot 'RSS500_scores_summary.csv' using 1:4 title 'Precision' with lines ls 1, 'RSS500_scores_summary.csv' using 1:5 title 'Recall' with lines ls 2, 'RSS500_scores_summary.csv' using 1:6 title 'F1-score' with lines ls 3, 'RSS500_baseline_scores_summary.csv' using 1:4 title 'BL Precision' with lines ls 6, 'RSS500_baseline_scores_summary.csv' using 1:5 title 'BL Recall' with lines ls 5, 'RSS500_baseline_scores_summary.csv' using 1:6 title 'BL F1-score' with lines ls 4

unset xlabel
set ylabel 'needed time (in s)'
set autoscale
set logscale y

set title 'b)'
set key above
plot 'GOLF_times_summary.csv' using 1:(\$3/1000) title 'Overall' with lines ls 1, 'GOLF_times_decomp_summary.csv' using 1:(\$3/1000) title 'Matrix Decomposition' with lines ls 2, 'GOLF_times_clustering_summary.csv' using 1:(\$3/1000) title 'Clustering' with lines ls 3, 'GOLF_baseline_times_summary.csv' using 1:(\$3/1000) title 'Baseline' with lines ls 4
set title 'd)'
set key off
plot 'REUTERS128_times_summary.csv' using 1:(\$3/1000) title 'Overall' with lines ls 1, 'REUTERS128_times_decomp_summary.csv' using 1:(\$3/1000) title 'Matrix Decomposition' with lines ls 2, 'REUTERS128_times_clustering_summary.csv' using 1:(\$3/1000) title 'Clustering' with lines ls 3, 'REUTERS128_baseline_times_summary.csv' using 1:(\$3/1000) title 'Baseline' with lines ls 4
set title 'f)'
set xlabel 'window size'
plot 'RSS500_times_summary.csv' using 1:(\$3/1000) title 'Overall' with lines ls 1, 'RSS500_times_decomp_summary.csv' using 1:(\$3/1000) title 'Matrix Decomposition' with lines ls 2, 'RSS500_times_clustering_summary.csv' using 1:(\$3/1000) title 'Clustering' with lines ls 3, 'RSS500_baseline_times_summary.csv' using 1:(\$3/1000) title 'Baseline' with lines ls 4
unset multiplot
" | gnuplot
