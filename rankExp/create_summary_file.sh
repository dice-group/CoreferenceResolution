#!/bin/bash


if [ $# -lt 3 ]
then
	echo "wrong number of arguments."
	echo "usage: create_summary_file.sh <corpus_name> <startRankId_inclusiv> <endRankId_exclusiv>"
else
	CORPUS=$1
	SCORE_SUMMARY_FILE=$CORPUS"_scores_summary.csv"
	TIME_SUMMARY_FILE=$CORPUS"_times_summary.csv"
	TIME_DECOMP_SUMMARY_FILE=$CORPUS"_times_decomp_summary.csv"
	TIME_CLUSTER_SUMMARY_FILE=$CORPUS"_times_clustering_summary.csv"

	echo -e "rank\t\tinstances\tprecision\trecall\tf1-score" > $SCORE_SUMMARY_FILE
	echo -e "rank\t\tneeded time" > $TIME_SUMMARY_FILE
	echo -e "rank\t\tneeded time" > $TIME_DECOMP_SUMMARY_FILE
	echo -e "rank\t\tneeded time" > $TIME_CLUSTER_SUMMARY_FILE

	i=$2
	#for i in { $2..$3 }
	while [ $i -lt $3 ]
	do
		file="ClusterExp_"$CORPUS"_rank_"$i".tsv"
		if [ -f $file ]; then
			tail -n 5 $file | head -n 1 | sed s/^/$i\\t/ >> $SCORE_SUMMARY_FILE
			tail -n 3 $file | head -n 1 | sed s/^/$i\\t/ >> $TIME_DECOMP_SUMMARY_FILE
			tail -n 2 $file | head -n 1 | sed s/^/$i\\t/ >> $TIME_CLUSTER_SUMMARY_FILE
			tail -n 1 $file | sed s/^/$i\\t/ >> $TIME_SUMMARY_FILE
		fi
		i=`expr $i + 1`
	done
fi
