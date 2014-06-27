package org.aksw.sandbox;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.Assert;

import org.aksw.preprocessing.AxelsPreprocessing;
import org.aksw.preprocessing.datatypes.Entity;
import org.aksw.preprocessing.datatypes.TokenizedDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;

@RunWith(Parameterized.class)
public class AxelsPreprocessingMatrixCreationTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { {
                new TokenizedDocument[] {
                        /*
                         * tokenIds = "Der"=0, "neue"=1, "Jaguar"=2, "ist"=3,
                         * "kein"=4, "Golf"=5, "Am"=6, "persischen"=7, "wird"=8,
                         * "wieder"=9, "gefahren"=10
                         * 
                         * entityIds = Jaguar=0, Golf=1, persianGolf=2
                         */
                        new TokenizedDocument(new String[] { "Der", "neue", "Jaguar", "ist", "kein", "Golf" },
                                new Entity[] { new Entity(2, 3, "http://car/Jaguar", "Jaguar"),
                                        new Entity(5, 6, "http://car/Golf", "Golf") }),
                        new TokenizedDocument(new String[] { "Am", "persischen", "Golf", "wird", "wieder", "Golf",
                                "gefahren" }, new Entity[] {
                                new Entity(1, 3, "http://geo/persianGolf", "persischen Golf"),
                                new Entity(5, 6, "http://car/Golf", "Golf") }) },
                new double[][] { { 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0 }, { 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1 },
                        { 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0 } } } });
    }

    private TokenizedDocument documents[];
    private double expectedMatrix[][];

    public AxelsPreprocessingMatrixCreationTest(TokenizedDocument documents[], double expectedMatrix[][]) {
        this.documents = documents;
        this.expectedMatrix = expectedMatrix;
    }

    @Test
    public void test() {
        Matrix matrix = AxelsPreprocessing.createMatrix(documents, 3);
        for (int i = 0; i < expectedMatrix.length; i++) {
            for (int k = 0; k < expectedMatrix[i].length; k++) {
                Assert.assertEquals(matrix.toString() + " does not equal the expected "
                        + (new Basic2DMatrix(expectedMatrix)).toString(), expectedMatrix[i][k], matrix.get(i, k));
            }
        }
    }
}
