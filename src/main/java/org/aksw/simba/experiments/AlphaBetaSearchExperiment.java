package org.aksw.simba.experiments;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.aksw.simba.decomposition.CorrelationBasedDecomposition;
import org.aksw.simba.decomposition.SimpleMatrixDecomposition;
import org.aksw.simba.preprocessing.Preprocessing;
import org.aksw.simba.preprocessing.Preprocessing.Corpora;
import org.aksw.simba.preprocessing.datatypes.TokenizedDocument;
import org.la4j.matrix.Matrix;

public class AlphaBetaSearchExperiment {

    public static void main(String args[]) throws FileNotFoundException {

        PrintStream out = null;
        TokenizedDocument[] documents;
        int rank = 10;
        double error;
        double threshold = 0.0;
        double maxSteps = 100;
        double alpha, beta;
        for (Corpora corpus : new Corpora[] { Corpora.REUTERS128, Corpora.RSS500 }) {
            documents = Preprocessing.getCorpus(corpus);
            for (int windowSize = 3; windowSize < 5; windowSize++) {
                Matrix M = Preprocessing.createMatrix(documents, windowSize);
                M = ClusterExp.normalizeMatrix(M);
                out = new PrintStream("DecompositionTest_" + corpus.toString() + "_" + windowSize + ".tsv");
                out.println("alpha\tbeta\terror");
                for (int alphaExp = -10; alphaExp < 0; ++alphaExp) {
                    alpha = Math.pow(1, alphaExp);
                    for (int betaExp = -10; betaExp < 0; ++betaExp) {
                        beta = Math.pow(1, betaExp);
                        SimpleMatrixDecomposition decomposition = new CorrelationBasedDecomposition();
                        // SimpleMatrixDecomposition decomposition = new SimpleMatrixDecomposition();
                        error = decomposition.decompose(M, rank, alpha, beta, threshold, maxSteps);
                        out.println(alpha + "\t" + beta + "\t" + error);
                    }
                }
            }
        }
    }
}
