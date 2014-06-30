package org.aksw.experiments;

import org.aksw.preprocessing.AxelsPreprocessing;
import org.aksw.preprocessing.AxelsPreprocessing.Corpora;
import org.aksw.preprocessing.datatypes.TokenizedDocument;
import org.aksw.simba.clustering.Clustering;
import org.aksw.simba.decomposition.MatrixDecomposition;
import org.aksw.simba.decomposition.SimpleMatrixDecomposition;
import org.la4j.matrix.Matrix;

public class ClusterExp {

    public static void main(String args[]) {

        TokenizedDocument[] documents;
        MatrixDecomposition decomposition = new SimpleMatrixDecomposition();
        for (Corpora corpus : new Corpora[] { Corpora.REUTERS128, Corpora.RSS500 }) {
            documents = AxelsPreprocessing.getCorpus(corpus);
            for (int windowSize = 3; windowSize < 5; windowSize++) {
                Matrix M = AxelsPreprocessing.createMatrix(documents, windowSize);
                decomposition.decompose(M, M.rank());
                Clustering c = new Clustering();
                System.out.println(c.cluster(M, null, 0.6));

                // TODO do eval
            }
        }
    }
}
