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

public class DiffWindowSizeClusterExp extends ClusterExp {

    // private static final double ALPHA = 5.0E-5;
    // private static final double BETA = 0.01;
    private static final double ALPHA = 0.0000005;
    private static final double BETA = 0.001;
    private static final double DECOMPOSITION_THRESHOLD = 0.6;
    private static final double GRAPH_CREATION_SIM_THRESHOLD = 0.1;
    private static final boolean CREATE_BASELINE = true;
    private static final boolean PRINT_PURE_MATRIX = true;
    private static final boolean PRINT_LEFT_DECOMPOSITION_MATRIX = true;

    public static void main(String args[]) throws FileNotFoundException {

        PrintStream out;
        TokenizedDocument[] documents;
        int rank = 100;
        double error;
        String[] entityIdUriMapping;
        Matrix entityStringSimMatrix;
        for (Corpora corpus : new Corpora[] { Corpora.REUTERS128, Corpora.RSS500, Corpora.GOLF }) {
            documents = Preprocessing.getCorpus(corpus);
            entityStringSimMatrix = EntityLabelSimMatrixCreator.getEntityLabelSimMatrix(documents);
            entityIdUriMapping = createEntityIdUriMapping(documents);
            for (int windowSize = 3; windowSize < 11; windowSize++) {
                Matrix M = Preprocessing.createMatrix(documents, windowSize);
                if (PRINT_PURE_MATRIX) {
                    out = new PrintStream("ClusterExp_" + corpus.toString() + "_" + windowSize + "_pure_matrix.txt");
                    out.print(M.toString());
                    out.close();
                }

                if (CREATE_BASELINE) {
                    // Create Baseline
                    System.out.println("Running baseline experiment...");
                    System.out.println("Starting Clustering...");
                    Clustering c = new Clustering();
                    IntOpenHashSet clusters[] = transformSet(c.cluster(M, entityStringSimMatrix,
                            GRAPH_CREATION_SIM_THRESHOLD));
                    System.out.println("Evaluating Clustering...");
                    Map<String, int[]> uriClusterMapping = createUriClusterMapping(entityIdUriMapping, clusters);
                    ObjectIntOpenHashMap<String> uriCounts = createUriCountMapping(entityIdUriMapping);
                    out = new PrintStream("ClusterExp_" + corpus.toString() + "_" + windowSize + "_baseline.tsv");
                    calculateAndPrintF1(uriClusterMapping, uriCounts, clusters, out);
                } else {
                    System.out.println("Running decomposition experiment...");
                    // CorrelationBasedDecomposition decomposition = new CorrelationBasedDecomposition();
                    System.out.println("Starting Decomposition...");
                    SimpleMatrixDecomposition decomposition = new SimpleMatrixDecomposition();
                    error = decomposition.decompose(M, rank, ALPHA, BETA, DECOMPOSITION_THRESHOLD);

                    if (PRINT_LEFT_DECOMPOSITION_MATRIX) {
                        out = new PrintStream("ClusterExp_" + corpus.toString() + "_" + windowSize
                                + "_left_decomp_matrix.txt");
                        out.print(decomposition.getLeftMatrix().toString());
                        out.close();
                    }

                    if ((!Double.isInfinite(error)) && (!Double.isNaN(error))) {
                        System.out.println("Starting Clustering...");
                        Clustering c = new Clustering();
                        IntOpenHashSet clusters[] = transformSet(c.cluster(decomposition.getLeftMatrix(),
                                entityStringSimMatrix,
                                GRAPH_CREATION_SIM_THRESHOLD));
                        System.out.println("Evaluating Clustering...");
                        Map<String, int[]> uriClusterMapping = createUriClusterMapping(entityIdUriMapping, clusters);
                        ObjectIntOpenHashMap<String> uriCounts = createUriCountMapping(entityIdUriMapping);
                        out = new PrintStream("ClusterExp_" + corpus.toString() + "_" + windowSize + ".tsv");
                        calculateAndPrintF1(uriClusterMapping, uriCounts, clusters, out);
                    } else {
                        System.out.println("Decomposition did not work: error=" + error);
                    }
                }
            }
        }
    }
}
