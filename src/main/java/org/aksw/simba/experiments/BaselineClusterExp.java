package org.aksw.simba.experiments;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Map;

import org.aksw.simba.clustering.Clustering;
import org.aksw.simba.preprocessing.EntityLabelSimMatrixCreator;
import org.aksw.simba.preprocessing.Preprocessing;
import org.aksw.simba.preprocessing.Preprocessing.Corpora;
import org.aksw.simba.preprocessing.datatypes.TokenizedDocument;
import org.la4j.matrix.Matrix;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;

public class BaselineClusterExp extends ClusterExp {

    private static final double GRAPH_CREATION_SIM_THRESHOLD = 0.1;
    private static final boolean PRINT_PURE_MATRIX = false;

    public static void main(String args[]) throws FileNotFoundException {
        if (args.length < 3) {
            System.out
                    .println("Usage: DiffRankClusterExp <corpus-name> <inclusive-start-window-size> <exclusive-end-window-size>");
            return;
        }
        Corpora corpus = null;
        try {
            corpus = Corpora.valueOf(args[0]);
        } catch (Exception e) {
            System.out.println("Couldn't find this corpus. Aborting.");
            return;
        }
        int startWindowSize, endWindowSize;
        try {
            startWindowSize = Integer.parseInt(args[1]);
            endWindowSize = Integer.parseInt(args[2]);
        } catch (Exception e) {
            System.out.println("Couldn't parse start and end window size. Aborting");
            e.printStackTrace();
            return;
        }

        PrintStream out;
        long startTime, timeNeededPreprocPart1, timeNeededPreprocPart2, timeNeededClustering;

        startTime = System.currentTimeMillis();
        TokenizedDocument[] documents = Preprocessing.getCorpus(corpus);
        Matrix entityStringSimMatrix = EntityLabelSimMatrixCreator.getEntityLabelSimMatrix(documents);
        String[] entityIdUriMapping = createEntityIdUriMapping(documents);
        timeNeededPreprocPart1 = System.currentTimeMillis() - startTime;
        for (int windowSize = startWindowSize; windowSize < endWindowSize; windowSize++) {
            startTime = System.currentTimeMillis();
            Matrix M = Preprocessing.createMatrix(documents, windowSize);
            M = ClusterExp.normalizeMatrix(M);
            timeNeededPreprocPart2 = System.currentTimeMillis() - startTime;
            if (PRINT_PURE_MATRIX) {
                out = new PrintStream("ClusterExp_" + corpus.toString() + "_" + windowSize + "_pure_matrix.txt");
                out.print(M.toString());
                out.close();
            }

            // Create Baseline
            System.out.println("Running baseline experiment...");
            System.out.println("Starting Clustering...");
            startTime = System.currentTimeMillis();
            Clustering c = new Clustering();
            IntOpenHashSet clusters[] = transformSet(c.cluster(M, entityStringSimMatrix,
                    GRAPH_CREATION_SIM_THRESHOLD, "graphs/" + corpus.name() + "_ws_" + windowSize + "_baseline_.tab"));
            timeNeededClustering = System.currentTimeMillis() - startTime;
            System.out.println("Evaluating Clustering...");
            Map<String, int[]> uriClusterMapping = createUriClusterMapping(entityIdUriMapping, clusters);
            ObjectIntOpenHashMap<String> uriCounts = createUriCountMapping(entityIdUriMapping);
            out = new PrintStream("ClusterExp_" + corpus.toString() + "_" + windowSize + "_baseline.tsv");
            calculateAndPrintF1(uriClusterMapping, uriCounts, clusters, out);
            out.println("time needed preprocessing (in ms)\t" + (timeNeededPreprocPart1 + timeNeededPreprocPart2));
            out.println("time needed clustering (in ms)\t" + (timeNeededClustering));
            out.println("time needed (in ms)\t"
                    + (timeNeededPreprocPart1 + timeNeededPreprocPart2 + timeNeededClustering));
        }
    }
}
