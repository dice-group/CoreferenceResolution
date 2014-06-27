package org.aksw.experiments;

import org.aksw.preprocessing.AxelsPreprocessing;
import org.aksw.preprocessing.AxelsPreprocessing.Corpora;
import org.aksw.simba.clustering.Clustering;
import org.la4j.matrix.Matrix;

public class ClusterExp {

	public static void main(String args[]) {

		for (Corpora corpus : new Corpora[] { Corpora.REUTERS128, Corpora.RSS500 }) {
			for (int windowSize = 3; windowSize < 5; windowSize++) {
				Matrix M = AxelsPreprocessing.getCorpusAsMatrix(corpus, windowSize);

				Clustering c = new Clustering();

				System.out.println(c.cluster(M, null, 0.6));
				
				//TODO do eval
			}
		}
	}
}
