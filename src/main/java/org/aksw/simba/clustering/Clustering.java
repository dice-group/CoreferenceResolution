/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.simba.clustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;
import org.la4j.vector.Vectors;

import de.uni_leipzig.gk.cluster.BorderFlowHard;

/**
 * Assume a latent feature matrix as well as a known similarity for the entities
 * resources that are to be clustered (for example string similarity for words).
 * 
 * @author ngonga
 */
public class Clustering {
    /**
     * 
     * @param latentFeatures
     *            Latent feature matrix
     * @param similarityMatrix
     *            Can be null
     * @param threshold
     *            Similarity threshold for building the graph
     * @return
     */
    public Set<Set<Integer>> cluster(Matrix latentFeatures, Matrix similarityMatrix, double threshold) {
        return cluster(latentFeatures, similarityMatrix, threshold, null);
    }

    /**
     * 
     * @param latentFeatures
     *            Latent feature matrix
     * @param similarityMatrix
     *            Can be null
     * @param threshold
     *            Similarity threshold for building the graph
     * @return
     */
    public Set<Set<Integer>> cluster(Matrix latentFeatures, Matrix similarityMatrix, double threshold,
            String graphFileName) {
        double norm;
        try {
            File f = null;
            if (graphFileName == null) {
                f = File.createTempFile("aaa", "aaa");
            } else {
                f = new File(graphFileName);
            }
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(f.getAbsolutePath())));
            // norm the latent feature matrix
            for (int i = 0; i < latentFeatures.rows(); i++) {
                Vector v = latentFeatures.getRow(i);
                norm = v.fold(Vectors.mkEuclideanNormAccumulator());
                v = v.divide(norm);
                latentFeatures.setRow(i, v);
            }

            // build graph
            double similarity;
            for (int i = 0; i < latentFeatures.rows(); i++) {
                for (int j = i + 1; j < latentFeatures.rows(); j++) {
                    similarity = 0d;
                    for (int k = 0; k < latentFeatures.getRow(i).length(); k++) {
                        similarity = similarity + latentFeatures.getRow(i).get(k) * latentFeatures.getRow(j).get(k);
                    }
                    if (similarityMatrix != null) {
                        similarity = similarity * similarityMatrix.get(i, j);
                    }
                    if (similarity >= threshold) {
                        writer.println(i + "\t" + j + "\t" + similarity);
                        System.out.println(i + "\t" + j + "\t" + similarity);
                    }
                }
            }
            writer.close();
            // cluster graph
            BorderFlowHard bf = new BorderFlowHard(f.getAbsolutePath());
            // bf.hardPartitioning = true;
            bf.hardPartitioning = false;
            Map<Set<String>, Set<String>> output = bf.cluster(-1d, true, true, true);
            // convert results and return
            Set<Set<Integer>> result = new HashSet<Set<Integer>>();
            for (Set<String> key : output.keySet()) {
                Set<String> value = output.get(key);
                Set<Integer> cluster = new HashSet<Integer>();
                for (String entry : value) {
                    cluster.add(Integer.parseInt(entry));
                }
                result.add(cluster);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
