package org.aksw.experiments;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.aksw.preprocessing.Preprocessing;
import org.aksw.preprocessing.Preprocessing.Corpora;
import org.aksw.preprocessing.datatypes.TokenizedDocument;
import org.aksw.simba.decomposition.SimpleMatrixDecomposition;
import org.la4j.matrix.Matrix;

public class AlphaBetaSearchExperiment {

    public static void main(String args[]) throws FileNotFoundException {

        PrintStream out = null;
        TokenizedDocument[] documents;
        int rank = 100;
        double error;
        double threshold = 0.1;
        double maxSteps = 100;
        for (Corpora corpus : new Corpora[] { Corpora.REUTERS128, Corpora.RSS500 }) {
            documents = Preprocessing.getCorpus(corpus);
            for (int windowSize = 3; windowSize < 5; windowSize++) {
                Matrix M = Preprocessing.createMatrix(documents, windowSize);
                out = new PrintStream("DecompositionTest_" + corpus.toString() + "_" + windowSize + ".tsv");
                out.println("alpha\tbeta\terror");
                for (double alpha = 0.0000005; alpha < 0.01; alpha += 0.0000005) {
                    for (double beta = 0.001; beta < 0.1; beta += 0.01) {
                        // SimpleMatrixDecomposition decomposition = new CorrelationBasedDecomposition();
                        SimpleMatrixDecomposition decomposition = new SimpleMatrixDecomposition();
                        error = decomposition.decompose(M, rank, alpha, beta, threshold, maxSteps);
                        out.println(alpha + "\t" + beta + "\t" + error);
                    }
                }
            }
        }
    }
}
