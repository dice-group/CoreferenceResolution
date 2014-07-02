package org.aksw.experiments;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.aksw.preprocessing.EntityLabelSimMatrixCreator;
import org.aksw.preprocessing.Preprocessing;
import org.aksw.preprocessing.Preprocessing.Corpora;
import org.aksw.preprocessing.datatypes.TokenizedDocument;
import org.aksw.simba.clustering.Clustering;
import org.aksw.simba.decomposition.CorrelationBasedDecomposition;
import org.aksw.simba.decomposition.SimpleMatrixDecomposition;
import org.la4j.matrix.Matrix;

public class ClusterExp {

    // private static final double ALPHA = 5.0E-5;
    // private static final double BETA = 0.01;
    private static final double ALPHA = 0.0000005;
    private static final double BETA = 0.001;

    public static void main(String args[]) throws FileNotFoundException {

        PrintStream out;
        TokenizedDocument[] documents;
        int rank = 100;
        double error;
        String[] entityIdUriMapping;
        Matrix entityStringSimMatrix;
        for (Corpora corpus : new Corpora[] { Corpora.REUTERS128, Corpora.RSS500 }) {
            documents = Preprocessing.getCorpus(corpus);
            entityStringSimMatrix = EntityLabelSimMatrixCreator.getEntityLabelSimMatrix(documents);
            entityIdUriMapping = createEntityIdUriMapping(documents);
            for (int windowSize = 3; windowSize < 5; windowSize++) {
                Matrix M = Preprocessing.createMatrix(documents, windowSize);
                out = new PrintStream("ClusterExp_" + corpus.toString() + "_" + windowSize + ".tsv");
                // CorrelationBasedDecomposition decomposition = new CorrelationBasedDecomposition();
                SimpleMatrixDecomposition decomposition = new SimpleMatrixDecomposition();
                error = decomposition.decompose(M, rank, ALPHA, BETA, SimpleMatrixDecomposition.DEFAULT_THRESHOLD);
                if ((!Double.isInfinite(error)) && (!Double.isNaN(error))) {
                    Clustering c = new Clustering();
                    Set<Set<Integer>> clusters = c.cluster(decomposition.getLeftMatrix(), entityStringSimMatrix, 0.6);
                    Map<String, int[]> uriClusterMapping = createUriClusterMapping(entityIdUriMapping, clusters);
                    calculateAndPrintPrecisions(uriClusterMapping, clusters.size(), out);
                } else {
                    System.out.println("Decomposition did not work: error=" + error);
                }
            }
        }
    }

    protected static String[] createEntityIdUriMapping(TokenizedDocument[] documents) {
        int entityCount = 0;
        for (int i = 0; i < documents.length; ++i) {
            entityCount += documents[i].entities.length;
        }
        String uris[] = new String[entityCount];
        entityCount = 0;
        for (int d = 0; d < documents.length; ++d) {
            for (int e = 0; e < documents[d].entities.length; ++e) {
                uris[entityCount] = documents[d].entities[e].URI.intern();
                ++entityCount;
            }
        }
        return uris;
    }

    protected static Map<String, int[]> createUriClusterMapping(String[] entityIdUriMapping,
            Set<Set<Integer>> clusters) {
        int clusterId = 0;
        Map<String, int[]> mapping = new HashMap<String, int[]>();
        String currentURI;
        int entitiesPerCluster[];
        for (Set<Integer> cluster : clusters) {
            for (Integer entityId : cluster) {
                currentURI = entityIdUriMapping[entityId];
                if (mapping.containsKey(currentURI)) {
                    entitiesPerCluster = mapping.get(currentURI);
                } else {
                    entitiesPerCluster = new int[clusters.size()];
                    mapping.put(currentURI, entitiesPerCluster);
                }
                ++entitiesPerCluster[clusterId];
            }
            ++clusterId;
        }
        return mapping;
    }

    protected static void calculateAndPrintPrecisions(Map<String, int[]> uriClusterMapping, int numberOfClusters,
            PrintStream out) {
        out.print("URI");
        for (int i = 0; i < numberOfClusters; ++i) {
            out.print("\tcluster_" + i);
        }
        out.println("\tprecision");
        String maxCountURI[] = new String[numberOfClusters];
        int clusterCounts[];
        int maxCount[] = new int[numberOfClusters];
        int sum[] = new int[numberOfClusters];
        int maxCountInLine, sumInLine;
        for (String uri : uriClusterMapping.keySet()) {
            out.print(uri);
            maxCountInLine = 0;
            sumInLine = 0;
            clusterCounts = uriClusterMapping.get(uri);
            for (int c = 0; c < clusterCounts.length; ++c) {
                out.print("\t" + clusterCounts[c]);
                sumInLine += clusterCounts[c];
                if (maxCountInLine < clusterCounts[c]) {
                    maxCountInLine = clusterCounts[c];
                }
                sum[c] += clusterCounts[c];
                if (maxCount[c] < clusterCounts[c]) {
                    maxCount[c] = clusterCounts[c];
                    maxCountURI[c] = uri;
                }
            }
            out.println("\t" + (maxCountInLine / (double) sumInLine));
        }

        out.print("max_URI");
        for (int i = 0; i < sum.length; ++i) {
            out.print("\t" + maxCountURI[i]);
        }
        out.println("\t");

        out.print("precision");
        for (int i = 0; i < sum.length; ++i) {
            out.print("\t" + (maxCount[i] / (double) sum[i]));
        }
        out.println("\t");
    }
}
