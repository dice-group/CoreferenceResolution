package org.aksw.preprocessing;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.Assert;

import org.aksw.preprocessing.AxelsPreprocessing;
import org.aksw.preprocessing.datatypes.DocumentWithPositions;
import org.aksw.preprocessing.datatypes.Entity;
import org.aksw.preprocessing.datatypes.TokenizedDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class AxelsPreprocessingEntityMappingTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays
                .asList(new Object[][] {
                        { "Dieser Text ist ein Testtext.", new String[] { "Dieser", "Text", "ist", "ein", "Testtext" },
                                new Entity[] {},
                                new int[][] {} },
                        { "Der neue Jaguar ist kein Golf.",
                                new String[] { "Der", "neue", "Jaguar", "ist", "kein", "Golf" },
                                new Entity[] { new Entity(9, 15, "http://car/Jaguar", "Jaguar"),
                                        new Entity(25, 29, "http://car/Golf", "Golf") },
                                new int[][] { { 2, 3 }, { 5, 6 } } },
                        { "Am persischen Golf wird wieder Golf gespielt!",
                                new String[] { "Am", "persischen", "Golf", "wird", "wieder", "Golf", "gespielt" },
                                new Entity[] { new Entity(3, 18, "http://geo/persianGolf", "persischen Golf"),
                                        new Entity(31, 35, "http://car/Golf", "Golf") },
                                new int[][] { { 1, 3 }, { 5, 6 } } },
                        // Let's check all 3 sentences together
                        {
                                "Dieser Text ist ein Testtext. Der neue Jaguar ist kein Golf. Am persischen Golf wird wieder Golf gespielt! Entity",
                                new String[] { "Dieser", "Text", "ist", "ein", "Testtext", "Der", "neue", "Jaguar",
                                        "ist", "kein", "Golf", "Am", "persischen", "Golf", "wird", "wieder", "Golf",
                                        "gespielt", "Entity" },
                                new Entity[] { new Entity(39, 45, "http://car/Jaguar", "Jaguar"),
                                        new Entity(55, 59, "http://car/Golf", "Golf"),
                                        new Entity(64, 79, "http://geo/persianGolf", "persischen Golf"),
                                        new Entity(92, 96, "http://car/Golf", "Golf"),
                                        new Entity(107, 113, "http://entity", "Entity") },
                                new int[][] { { 7, 8 }, { 10, 11 }, { 12, 14 }, { 16, 17 }, { 18, 19 } }

                        } });
    }

    private String text;
    private String tokens[];
    private Entity entities[];
    private int expectedNewPositions[][];

    public AxelsPreprocessingEntityMappingTest(String text, String[] tokens, Entity[] entities,
            int[][] expectedNewPositions) {
        this.text = text;
        this.tokens = tokens;
        this.entities = entities;
        this.expectedNewPositions = expectedNewPositions;
    }

    @Test
    public void test() {
        TokenizedDocument documents[] = AxelsPreprocessing
                .mapEntitiesToTokens(new DocumentWithPositions[] { new DocumentWithPositions(text, tokens,
                        entities) });
        Assert.assertEquals(expectedNewPositions.length, documents[0].entities.length);
        for (int i = 0; i < documents[0].entities.length; ++i) {
            Assert.assertEquals(expectedNewPositions[i][0], documents[0].entities[i].start);
            Assert.assertEquals(expectedNewPositions[i][1], documents[0].entities[i].end);
        }
    }
}
