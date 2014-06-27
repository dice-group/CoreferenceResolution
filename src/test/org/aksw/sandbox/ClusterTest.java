package org.aksw.sandbox;

import org.aksw.simba.clustering.Clustering;
import org.junit.Test;
import org.la4j.matrix.dense.Basic2DMatrix;

public class ClusterTest {

	@Test
	public void test() {
		Basic2DMatrix M = new Basic2DMatrix(new double[][] { { 5, 3, 0, 1 }, { 5, 2, 0, 1 }, { 1, 0, 0, 7 }, { 1, 0, 0, 6 } });
		Clustering c = new Clustering();
		System.out.println(c.cluster(M, null, 0.6));
	}

}
