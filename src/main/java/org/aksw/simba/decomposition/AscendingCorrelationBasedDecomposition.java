/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.org.aksw.simba.decomposition;

import java.util.*;
import org.aksw.simba.decomposition.SimpleMatrixDecomposition;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;

/**
 *
 * @author ngonga
 */
public class AscendingCorrelationBasedDecomposition extends SimpleMatrixDecomposition {

    public Matrix init(Matrix M, int rank) {
        Matrix A = M.multiply(M.transpose());
        List<Double> correlation = new ArrayList<Double>();
        Map<Double, Set<Integer>> index = new HashMap<Double, Set<Integer>>();
        double sum;
        for (int i = 0; i < A.rows(); i++) {
            sum = 0d;
            for (int j = 0; j < A.columns(); j++) {
                sum = sum + A.get(i, j);
            }
            correlation.add(sum);
            if (!index.containsKey(sum)) {
                index.put(sum, new HashSet<Integer>());
            }
            index.get(sum).add(i);
        }
        Collections.sort(correlation);
        //sorted list of word indexes by descending correlation
        List<Integer> wordIndex = new ArrayList<Integer>();
        Set<Double> seen = new HashSet<Double>();
        for (int i = 0; i < correlation.size(); i++) {
            double score = correlation.get(i);
            if (!seen.contains(score)) {
                wordIndex.addAll(index.get(score));
                seen.add(score);
            }
        }

        Matrix result = new Basic2DMatrix(A.rows(), rank);
        for (int i = 0; i < rank; i++) {
            //get index to add
            result.setColumn(i, A.getColumn(wordIndex.get(wordIndex.size() - i -1)));
        }
        return result.divide(result.max());
    }
}
