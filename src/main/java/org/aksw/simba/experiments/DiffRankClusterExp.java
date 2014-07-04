package org.aksw.simba.experiments;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Map;

import org.aksw.simba.clustering.Clustering;
import org.aksw.simba.decomposition.SimpleMatrixDecomposition;
import org.aksw.simba.preprocessing.EntityLabelSimMatrixCreator;
import org.aksw.simba.preprocessing.Preprocessing;
import org.aksw.simba.preprocessing.Preprocessing.Corpora;
import org.aksw.simba.preprocessing.datatypes.TokenizedDocument;
import org.la4j.matrix.Matrix;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;

public class DiffRankClusterExp extends ClusterExp {

    // private static final double ALPHA = 5.0E-5;
    // private static final double BETA = 0.01;
    private static final double ALPHA = 0.0000005;
    private static final double BETA = 0.001;
    private static final double DECOMPOSITION_THRESHOLD = 0.6;
    private static final double GRAPH_CREATION_SIM_THRESHOLD = 0.1;
    private static final boolean PRINT_PURE_MATRIX = false;
    private static final boolean PRINT_LEFT_DECOMPOSITION_MATRIX = false;

    public static void main(String args[]) throws FileNotFoundException {
        if (args.length < 3) {
            System.out.println("Usage: DiffRankClusterExp <corpus-name> <inclusive-start-rank> <exclusive-end-rank>");
            return;
        }
        Corpora corpus = null;
        try {
            corpus = Corpora.valueOf(args[0]);
        } catch (Exception e) {
            System.out.println("Couldn't find this corpus. Aborting.");
            return;
        }
        int startRank, endRank;
        try {
            startRank = Integer.parseInt(args[1]);
            endRank = Integer.parseInt(args[2]);
        } catch (Exception e) {
            System.out.println("Couldn't parse start and end rank. Aborting");
            e.printStackTrace();
            return;
        }

        PrintStream out;
        TokenizedDocument[] documents;
        // int rank = 100;
        double error;
        long startTime, timeNeededPreproc, timeNeededDecomp, timeNeededClustering;
        int windowSize = 4;
        String[] entityIdUriMapping;
        Matrix entityStringSimMatrix;
        // for (Corpora corpus : new Corpora[] { Corpora.REUTERS128, Corpora.RSS500, Corpora.GOLF }) {
        startTime = System.currentTimeMillis();
        documents = Preprocessing.getCorpus(corpus);
        entityStringSimMatrix = EntityLabelSimMatrixCreator.getEntityLabelSimMatrix(documents);
        entityIdUriMapping = createEntityIdUriMapping(documents);
        Matrix M = Preprocessing.createMatrix(documents, windowSize);
        M = ClusterExp.normalizeMatrix(M);
        timeNeededPreproc = System.currentTimeMillis() - startTime;
        System.out.println("Time needed for preprocessing: " + timeNeededPreproc);
        if (PRINT_PURE_MATRIX) {
            out = new PrintStream("ClusterExp_" + corpus.toString() + "_" + windowSize + "_pure_matrix.txt");
            out.print(M.toString());
            out.close();
        }

        for (int rank = startRank; rank < endRank; ++rank) {
            System.out.println("Running decomposition experiment...");
            // CorrelationBasedDecomposition decomposition = new CorrelationBasedDecomposition();
            System.out.println("Starting Decomposition...");
            startTime = System.currentTimeMillis();
            SimpleMatrixDecomposition decomposition = new SimpleMatrixDecomposition();
            error = decomposition.decompose(M, rank, ALPHA, BETA, DECOMPOSITION_THRESHOLD);
            timeNeededDecomp = System.currentTimeMillis() - startTime;

            if (PRINT_LEFT_DECOMPOSITION_MATRIX) {
                out = new PrintStream("ClusterExp_" + corpus.toString() + "_" + windowSize
                        + "_left_decomp_matrix.txt");
                out.print(decomposition.getLeftMatrix().toString());
                out.close();
            }

            if ((!Double.isInfinite(error)) && (!Double.isNaN(error))) {
                System.out.println("Starting Clustering...");
                startTime = System.currentTimeMillis();
                Clustering c = new Clustering();
                IntOpenHashSet clusters[] = transformSet(c.cluster(decomposition.getLeftMatrix(),
                        entityStringSimMatrix,
                        GRAPH_CREATION_SIM_THRESHOLD, "graphs/" + corpus.name() + "_ws_" + windowSize + "_rank_"
                                + rank + ".tab"));
                timeNeededClustering = System.currentTimeMillis() - startTime;
                System.out.println("Evaluating Clustering...");
                Map<String, int[]> uriClusterMapping = createUriClusterMapping(entityIdUriMapping, clusters);
                ObjectIntOpenHashMap<String> uriCounts = createUriCountMapping(entityIdUriMapping);
                out = new PrintStream("ClusterExp_" + corpus.toString() + "_rank_" + rank + ".tsv");
                calculateAndPrintF1(uriClusterMapping, uriCounts, clusters, out);
                out.println("time needed preprocessing (in ms)\t" + (timeNeededPreproc));
                out.println("time needed decomposition (in ms)\t" + (timeNeededDecomp));
                out.println("time needed clustering (in ms)\t" + (timeNeededClustering));
                out.println("time needed (in ms)\t" + (timeNeededPreproc + timeNeededDecomp + timeNeededClustering));
                out.close();
            } else {
                System.out.println("Decomposition did not work: error=" + error);
            }
            // }
        }
    }
}
