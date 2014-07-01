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
        int rank = 10;
        double error;
        double threshold = 0.0;
        double maxSteps = 100;
        for (Corpora corpus : new Corpora[] { Corpora.REUTERS128, Corpora.RSS500 }) {
            documents = Preprocessing.getCorpus(corpus);
            for (int windowSize = 3; windowSize < 5; windowSize++) {
                Matrix M = Preprocessing.createMatrix(documents, windowSize);
                System.err.println("matrix=" + M.rows() + "x"
                        + M.columns());
                out = new PrintStream("DecompositionTest_" + corpus.toString() + "_" + windowSize + ".tsv");
                out.println("alpha\tbeta\terror");
                for (double alpha = 0.00005; alpha < 0.01; alpha += 0.00005) {
                    for (double beta = 0.005; beta < 0.1; beta += 0.005) {
                        SimpleMatrixDecomposition decomposition = new SimpleMatrixDecomposition();
                        error = decomposition.decompose(M, rank, alpha, beta, threshold, maxSteps);
                        System.err.println("decomposition=" + decomposition.getLeftMatrix().rows() + "x"
                                + decomposition.getLeftMatrix().columns());
                        out.println(alpha + "\t" + beta + "\t" + error);
                    }
                }
            }
        }
    }
}
