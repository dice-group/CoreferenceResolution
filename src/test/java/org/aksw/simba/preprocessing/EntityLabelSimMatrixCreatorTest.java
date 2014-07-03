package org.aksw.simba.preprocessing;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.Assert;

import org.aksw.simba.preprocessing.EntityLabelSimMatrixCreator;
import org.aksw.simba.preprocessing.datatypes.Entity;
import org.aksw.simba.preprocessing.datatypes.TokenizedDocument;
import org.apache.lucene.search.spell.NGramDistance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;

@RunWith(Parameterized.class)
public class EntityLabelSimMatrixCreatorTest {

    private static final NGramDistance distance = new NGramDistance(3);

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays
                .asList(new Object[][] { {
                        new TokenizedDocument[] {
                                new TokenizedDocument(new String[] { "Der", "neue", "Jaguar", "ist", "kein", "Golf" },
                                        new Entity[] { new Entity(2, 3, "http://car/Jaguar", "Jaguar"),
                                                new Entity(5, 6, "http://car/Golf", "Golf") }),
                                new TokenizedDocument(new String[] { "Am", "persischen", "Golf", "wird", "wieder",
                                        "Golf",
                                        "gefahren" }, new Entity[] {
                                        new Entity(1, 3, "http://geo/persianGolf", "persischen Golf"),
                                        new Entity(5, 6, "http://car/Golf", "Golf") }) },
                        new double[][] {
                                { 1, distance.getDistance("Jaguar", "Golf"),
                                        distance.getDistance("Jaguar", "persischen Golf"),
                                        distance.getDistance("Jaguar", "Golf") },
                                { distance.getDistance("Golf", "Jaguar"), 1,
                                        distance.getDistance("Golf", "persischen Golf"), 1 },
                                { distance.getDistance("persischen Golf", "Jaguar"),
                                        distance.getDistance("persischen Golf", "Golf"), 1,
                                        distance.getDistance("persischen Golf", "Golf") },
                                { distance.getDistance("Golf", "Jaguar"), 1,
                                        distance.getDistance("Golf", "persischen Golf"), 1 } } } });
    }

    private TokenizedDocument documents[];
    private double expectedMatrix[][];

    public EntityLabelSimMatrixCreatorTest(TokenizedDocument documents[], double expectedMatrix[][]) {
        this.documents = documents;
        this.expectedMatrix = expectedMatrix;
    }

    @Test
    public void test() {
        Matrix matrix = EntityLabelSimMatrixCreator.getEntityLabelSimMatrix(documents);
        for (int i = 0; i < expectedMatrix.length; ++i) {
            for (int k = 0; k < expectedMatrix[i].length; ++k) {
                Assert.assertEquals(matrix.toString() + " does not equal the expected "
                        + (new Basic2DMatrix(expectedMatrix)).toString(), expectedMatrix[i][k], matrix.get(i, k));
            }
        }
    }
}
