/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.simba.decomposition;

import org.la4j.matrix.Matrix;

/**
 * Interface for matrix decomposition. Idea is the following: Given a matrix
 * M(n, m), compute L(n, r) and R(m, r) such that M = L*R' (where R' is the
 * transposition of R). The parameter r is the key parameter here as it controls
 * how much information we can lose. We can prevent overfitting by mimimizing
 * any error, for example ||M - LR'||^2 - \lambda * (||L||^2 + ||R||^2)/2.
 * Papers suggest to set \lambda to 0.002.
 * 
 * @author ngonga
 */
public interface MatrixDecomposition {

    public double decompose(Matrix m, int rank);

    /**
     * Should return null if no decomposition has been carried out
     * 
     * @return
     */
    public Matrix getLeftMatrix();

    public Matrix getRightMatrix();

    public Matrix getApproximation();
}
