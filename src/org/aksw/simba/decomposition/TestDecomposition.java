/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.simba.decomposition;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;

/**
 *
 * @author ngonga
 */
public class TestDecomposition {
    static Matrix M;
    public static void run()
    {
        M = new Basic2DMatrix(new double[][]{{5, 3, 0, 1}, {4, 0, 0, 1}, {1, 1, 0, 5}, {0, 1, 5, 4}});
        SimpleMatrixDecomposition smd = new SimpleMatrixDecomposition();
        smd.decompose(M, 2);
        System.out.println(M);
        System.out.println(smd.getApproximation());
        
    }
    
    public static void testScale(int n, int m, int r)
    {
        M = new Basic2DMatrix(n, m);
        for(int i=0; i<n; i++)
            for(int j=0; j<m; j++)
                M.set(i, j, Math.random());
        MatrixDecomposition smd = new SimpleMatrixDecomposition();        
        smd.decompose(M, r);
        //System.out.println(M);
        //System.out.println(smd.getApproximation());
    }
    public static void main(String args[])
    {
        testScale(1000,500,10);
    }
}
