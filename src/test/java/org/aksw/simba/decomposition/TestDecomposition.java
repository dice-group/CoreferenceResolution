/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.simba.decomposition;

import org.aksw.simba.decomposition.AscendingCorrelationBasedDecomposition;
import org.aksw.simba.decomposition.CorrelationBasedDecomposition;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;

/**
 * 
 * @author ngonga
 */
public class TestDecomposition {

    static Matrix M;

    public static void run() {
        M = new Basic2DMatrix(new double[][] { { 5, 3, 0, 1 }, { 4, 0, 0, 1 }, { 1, 1, 0, 5 }, { 0, 1, 5, 4 } });
        SimpleMatrixDecomposition smd = new SimpleMatrixDecomposition();
        SimpleMatrixDecomposition.MAX_STEPS = 100;
        smd.decompose(M, 2);
        System.out.println(M);
        System.out.println(smd.getApproximation());

        CorrelationBasedDecomposition cbd = new CorrelationBasedDecomposition();
        CorrelationBasedDecomposition.MAX_STEPS = 100;
        cbd.decompose(M, 2);
        System.out.println(M);
        System.out.println(smd.getApproximation());

        AscendingCorrelationBasedDecomposition acd = new AscendingCorrelationBasedDecomposition();
        AscendingCorrelationBasedDecomposition.MAX_STEPS = 100;
        acd.decompose(M, 2);
        System.out.println(M);
        System.out.println(smd.getApproximation());
    }

    public static void testScale(int n, int m, int r) {
        M = new Basic2DMatrix(n, m);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                M.set(i, j, Math.random());
            }
        }

        SimpleMatrixDecomposition smd = new SimpleMatrixDecomposition();
        SimpleMatrixDecomposition.MAX_STEPS = 100;
        smd.decompose(M, r);
        // System.out.println(M);
        // System.out.println(smd.getApproximation());

        System.out.println("\n >>>>>>>>>>>> \n");
        CorrelationBasedDecomposition cbd = new CorrelationBasedDecomposition();
        CorrelationBasedDecomposition.MAX_STEPS = 100;
        cbd.decompose(M, r);
        // System.out.println(M);
        // System.out.println(smd.getApproximation());

        System.out.println("\n >>>>>>>>>>>> \n");

        AscendingCorrelationBasedDecomposition acd = new AscendingCorrelationBasedDecomposition();
        AscendingCorrelationBasedDecomposition.MAX_STEPS = 100;
        acd.decompose(M, r);
        // System.out.println(M);
        // System.out.println(smd.getApproximation());
    }

    public static void main(String args[]) {
        run();
        testScale(1000, 500, 10);
    }
}
