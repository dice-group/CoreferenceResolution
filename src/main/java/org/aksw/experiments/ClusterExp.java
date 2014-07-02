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
import org.aksw.simba.decomposition.SimpleMatrixDecomposition;
import org.la4j.matrix.Matrix;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;

public class ClusterExp {

    // private static final double ALPHA = 5.0E-5;
    // private static final double BETA = 0.01;
    private static final double ALPHA = 0.0000005;
    private static final double BETA = 0.001;
    private static final double DECOMPOSITION_THRESHOLD = 0.6;
    private static final double GRAPH_CREATION_SIM_THRESHOLD = 0.1;
    private static final boolean CREATE_BASELINE = false;
    private static final boolean PRINT_PURE_MATRIX = true;
    private static final boolean PRINT_LEFT_DECOMPOSITION_MATRIX = true;

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

    private static IntOpenHashSet[] transformSet(Set<Set<Integer>> clusters) {
        IntOpenHashSet[] newClusters = new IntOpenHashSet[clusters.size()];
        int pos = 0;
        for (Set<Integer> cluster : clusters) {
            newClusters[pos] = new IntOpenHashSet(cluster.size());
            for (Integer entityId : cluster) {
                newClusters[pos].add(entityId);
            }
            ++pos;
        }
        return newClusters;
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

    protected static ObjectIntOpenHashMap<String> createUriCountMapping(String[] entityIdUriMapping) {
        ObjectIntOpenHashMap<String> uriCounts = new ObjectIntOpenHashMap<String>();
        for (int i = 0; i < entityIdUriMapping.length; ++i) {
            uriCounts.putOrAdd(entityIdUriMapping[i], 1, 1);
        }
        return uriCounts;
    }

    protected static Map<String, int[]> createUriClusterMapping(String[] entityIdUriMapping, IntOpenHashSet[] clusters) {
        Map<String, int[]> mapping = new HashMap<String, int[]>();
        String currentURI;
        int entitiesPerCluster[];
        for (int i = 0; i < clusters.length; ++i) {
            for (int j = 0; j < clusters[i].allocated.length; ++j) {
                if (clusters[i].allocated[j]) {
                    currentURI = entityIdUriMapping[clusters[i].keys[j]];
                    if (mapping.containsKey(currentURI)) {
                        entitiesPerCluster = mapping.get(currentURI);
                    } else {
                        entitiesPerCluster = new int[clusters.length];
                        mapping.put(currentURI, entitiesPerCluster);
                    }
                    ++entitiesPerCluster[i];
                }
            }
        }
        return mapping;
    }

    protected static void calculateAndPrintF1(Map<String, int[]> uriClusterMapping,
            ObjectIntOpenHashMap<String> uriCounts, IntOpenHashSet[] clusters, PrintStream out) {
        out.print("URI\tinstances\tmicro-precision\tmicro-recall\tmicro-F1");

        // Determine Micro Precision, Recall and F1 for every URI
        int clusterCounts[];
        int maxCount[] = new int[clusters.length];
        int maxCountUriId[] = new int[clusters.length];
        int bestClusterForUri[] = new int[uriCounts.size()];
        int bestClusterCount, instanceCount = 0;
        String uri;
        int uriId = 0;
        double precision, recall;
        // Go through all URIs
        for (int i = 0; i < uriCounts.allocated.length; ++i) {
            if (uriCounts.allocated[i]) {
                uri = (String) (((Object[]) uriCounts.keys)[i]);
                out.print(uri + "\t" + uriCounts.values[i] + "\t");
                instanceCount += uriCounts.values[i];

                if (uriClusterMapping.containsKey(uri)) {
                    clusterCounts = uriClusterMapping.get(uri);
                    bestClusterCount = 0;
                    // clusteredInstancesCount = 0;
                    // find the best cluster for this URI
                    for (int c = 0; c < clusterCounts.length; ++c) {
                        if (clusterCounts[c] > 0) {
                            // clusteredInstancesCount += clusterCounts[c];
                            // If this cluster is better than the one we thought it would be best (= if there are more
                            // instances in this cluster than in the other OR there are the same number of instances but
                            // the current cluster is smaller)
                            if ((bestClusterCount < clusterCounts[c])
                                    || ((bestClusterCount == clusterCounts[c]) && (clusters[bestClusterForUri[uriId]].assigned > clusters[c].assigned))) {
                                bestClusterCount = clusterCounts[c];
                                bestClusterForUri[uriId] = c;
                            }

                            if (maxCount[c] < clusterCounts[c]) {
                                maxCount[c] = clusterCounts[c];
                                maxCountUriId[c] = uriId;
                            }
                        }
                    }
                    precision = (double) bestClusterCount / (double) clusters[bestClusterForUri[uriId]].assigned;
                    recall = (double) bestClusterCount / (double) uriCounts.values[i];
                } else {
                    bestClusterForUri[uriId] = -1;
                    precision = 1.0;
                    recall = 1.0 / (double) uriCounts.values[i];
                }
                out.println(precision + "\t" + recall + "\t" + (2 * precision * recall / (precision + recall)));
                ++uriId;
            }
        }

        // Determine Macro Precision, Recall and F1
        int clusteredCorrect, truePos = 0, falsePos = 0, falseNeg = 0;
        uriId = 0;
        for (int i = 0; i < uriCounts.allocated.length; ++i) {
            if (uriCounts.allocated[i]) {
                uri = (String) (((Object[]) uriCounts.keys)[i]);
                out.print(uri + "\t" + uriCounts.values[i] + "\t");
                instanceCount += uriCounts.values[i];
                // if this URI has been clustered
                if (bestClusterForUri[uriId] >= 0) {
                    // If this cluster is for this URI
                    if (maxCountUriId[bestClusterForUri[uriId]] == uriId) {
                        clusteredCorrect = uriClusterMapping.get(uri)[bestClusterForUri[uriId]];
                        truePos += clusteredCorrect;
                        falsePos += clusters[bestClusterForUri[uriId]].assigned - clusteredCorrect;
                        falseNeg += uriCounts.values[i] - clusteredCorrect;
                    } else {
                        // This URI has been clustered but into the cluster of another URI --> all instances have been
                        // clustered into the wrong cluster
                        falseNeg += uriCounts.values[i];
                    }
                } else {
                    truePos += 1;
                    falseNeg += uriCounts.values[i] - 1;
                }
                ++uriId;
            }
        }
        precision = (double) truePos / (double) (truePos + falsePos);
        recall = (double) truePos / (double) (truePos + falseNeg);
        out.println("macro measures\t" + instanceCount + "\t" + precision + "\t" + recall + "\t"
                + (2 * precision * recall / (precision + recall)));

    }
}
