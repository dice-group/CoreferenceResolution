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
public class SimpleMatrixDecomposition implements MatrixDecomposition {

    Matrix L; // left matrix
    Matrix R; // right matrix
    Matrix M;
    public static double DEFAULT_ALPHA = 0.0002;
    public static double DEFAULT_BETA = 0.02;
    public static double DEFAULT_THRESHOLD = 0.1;
    public static int MAX_STEPS = 10000;

    public double decompose(Matrix Ma, int r) {
        return decompose(Ma, r, DEFAULT_ALPHA, DEFAULT_BETA, DEFAULT_THRESHOLD, MAX_STEPS);
    }

    public double decompose(Matrix Ma, int r, double alpha, double beta, double threshold) {
        return decompose(Ma, r, DEFAULT_ALPHA, DEFAULT_BETA, DEFAULT_THRESHOLD, MAX_STEPS);
    }

    public Matrix init(int rows, int columns) {
        Matrix M = new Basic2DMatrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // M.assign(1d);
                M.set(i, j, Math.random());
            }
        }
        return M;
    }

    public void init(int r)
    {
        L = init(M.rows(), r);
        R = init(M.columns(), r);
    }

    /**
     * 
     * @param M
     * @param r
     * @param alpha
     *            Controls the step for learning
     * @param beta
     *            Controls the effect of the regularization
     * @return returns the error
     */
    public double decompose(Matrix Ma, int r, double alpha, double beta, double threshold, double maxSteps) {
        M = Ma;
        Matrix E, M2;
        // 1. Initialize L and R
        init(r);
        Matrix L2, R2;
        int steps = 0;
        M2 = L.multiply(R.transpose());
        E = M.subtract(M2);
        double error = 0d;
        while (steps < maxSteps) {
            // increment steps
            steps++;
            // update L
            L2 = L.copy();
            for (int i = 0; i < M.rows(); i++) {
                for (int j = 0; j < M.columns(); j++) {
                    for (int k = 0; k < r; k++) {
                        L2.set(i, k, L2.get(i, k) + alpha * (2 * E.get(i, j) * R.get(j, k) - beta * L.get(i, k)));
                    }
                }
            }

            R2 = R.copy();
            for (int i = 0; i < M.rows(); i++) {
                for (int j = 0; j < M.columns(); j++) {
                    for (int k = 0; k < r; k++) {
                        R2.set(j, k, R2.get(j, k) + alpha * (2 * E.get(i, j) * L.get(i, k) - beta * R.get(j, k)));
                    }
                }
            }

            // overwrite L and R
            L = L2;
            R = R2;

            // compute error
            M2 = L.multiply(R.transpose());

            E = M.subtract(M2);

            // System.out.println ("Left=\n"+L);
            // System.out.println ("Right=\n"+R);
            // System.out.println ("Approximation=\n"+M2);
            // System.out.println ("Error=\n"+E);

            error = 0d;
            // compute total error
            for (int i = 0; i < M.rows(); i++) {
                for (int j = 0; j < M.columns(); j++) {
                    error = error + E.get(i, j) * E.get(i, j) / (M.rows() * M.columns());
                }
            }
            // System.out.println(error);
            if (error < threshold) {
                break;
            }
            if (Double.isInfinite(error) || Double.isNaN(error)) {
                break;
            }
        }
        return error;
    }

    public Matrix getLeftMatrix() {
        return L;
    }

    public Matrix getRightMatrix() {
        return R;
    }

    public Matrix getApproximation() {
        return L.multiply(R.transpose());
    }
}
