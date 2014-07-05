package org.aksw.simba.clustering;

import java.util.Random;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.Vector;

/**
 * We have some problems with the performance of the cosinus calculation. I want to try out some possibilities to
 * enhance the calculation.
 */
public class SimplePerformanceTest {

    private static final int COLUMNS = 200;
    private static final int ROWS = 100;

    public static void main(String[] args) {
        long time;
        double origSim, sim;
        Matrix latentFeatures = createRandomMatrix(ROWS, COLUMNS);
        Matrix similarityMatrix = createRandomMatrix(ROWS, ROWS);

        time = System.currentTimeMillis();
        origSim = originalApproach(latentFeatures, similarityMatrix);
        time = System.currentTimeMillis() - time;
        System.out.println(String.format("%20s: %20d", new Object[] { "original approach", time }));

        time = System.currentTimeMillis();
        sim = askForVectorsOnce(latentFeatures, similarityMatrix);
        time = System.currentTimeMillis() - time;
        System.out.println(String.format("%20s: %20d", new Object[] { "vector once", time }));
        if (sim != origSim) {
            System.out.println("DIFFERENT RESULTS! last approach " + sim + " != original approach " + origSim);
        }

        time = System.currentTimeMillis();
        sim = hadamardProduct(latentFeatures, similarityMatrix);
        time = System.currentTimeMillis() - time;
        System.out.println(String.format("%20s: %20d", new Object[] { "hadamard product", time }));
        if (sim != origSim) {
            System.out.println("DIFFERENT RESULTS! last approach " + sim + " != original approach " + origSim);
        }

        System.out.println("... and again ...");

        time = System.currentTimeMillis();
        origSim = originalApproach(latentFeatures, similarityMatrix);
        time = System.currentTimeMillis() - time;
        System.out.println(String.format("%20s: %20d", new Object[] { "original approach", time }));

        time = System.currentTimeMillis();
        sim = askForVectorsOnce(latentFeatures, similarityMatrix);
        time = System.currentTimeMillis() - time;
        System.out.println(String.format("%20s: %20d", new Object[] { "vector once", time }));
        if (sim != origSim) {
            System.out.println("DIFFERENT RESULTS! last approach " + sim + " != original approach " + origSim);
        }

        time = System.currentTimeMillis();
        sim = hadamardProduct(latentFeatures, similarityMatrix);
        time = System.currentTimeMillis() - time;
        System.out.println(String.format("%20s: %20d", new Object[] { "hadamard product", time }));
        if (sim != origSim) {
            System.out.println("DIFFERENT RESULTS! last approach " + sim + " != original approach " + origSim);
        }
    }

    private static Matrix createRandomMatrix(int rows, int columns) {
        double matrix[][] = new double[rows][columns];
        Random random = new Random();
        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix[i].length; ++j) {
                matrix[i][j] = random.nextDouble();
            }
        }
        return new Basic2DMatrix(matrix);
    }

    private static double originalApproach(Matrix latentFeatures, Matrix similarityMatrix) {
        double similarity, similaritySum = 0;
        for (int i = 0; i < latentFeatures.rows(); i++) {
            for (int j = i + 1; j < latentFeatures.rows(); j++) {
                similarity = 0d;
                for (int k = 0; k < latentFeatures.getRow(i).length(); k++) {
                    similarity = similarity + latentFeatures.getRow(i).get(k) * latentFeatures.getRow(j).get(k);
                }
                if (similarityMatrix != null) {
                    similarity = similarity * similarityMatrix.get(i, j);
                }
                similaritySum += similarity;
            }
        }
        return similaritySum;
    }

    private static double askForVectorsOnce(Matrix latentFeatures, Matrix similarityMatrix) {
        double similarity, similaritySum = 0;
        int rows = latentFeatures.rows(), columns = latentFeatures.columns();
        Vector v1, v2;
        for (int i = 0; i < rows; i++) {
            v1 = latentFeatures.getRow(i);
            for (int j = i + 1; j < rows; j++) {
                similarity = 0d;
                v2 = latentFeatures.getRow(j);
                for (int k = 0; k < columns; k++) {
                    similarity = similarity + v1.get(k) * v2.get(k);
                }
                if (similarityMatrix != null) {
                    similarity = similarity * similarityMatrix.get(i, j);
                }
                similaritySum += similarity;
            }
        }
        return similaritySum;
    }

    private static double hadamardProduct(Matrix latentFeatures, Matrix similarityMatrix) {
        double similarity, similaritySum = 0;
        int rows = latentFeatures.rows();
        Vector v1;
        for (int i = 0; i < rows; i++) {
            v1 = latentFeatures.getRow(i);
            for (int j = i + 1; j < rows; j++) {
                similarity = latentFeatures.getRow(j).hadamardProduct(v1).sum();
                if (similarityMatrix != null) {
                    similarity = similarity * similarityMatrix.get(i, j);
                }
                similaritySum += similarity;
            }
        }
        return similaritySum;
    }
}
