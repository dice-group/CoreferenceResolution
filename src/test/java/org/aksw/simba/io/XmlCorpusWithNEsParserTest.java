package org.aksw.simba.io;

import org.aksw.simba.io.XmlCorpusWithNEsParser;
import org.aksw.simba.preprocessing.datatypes.DocumentWithPositions;
import org.aksw.simba.preprocessing.datatypes.Entity;
import org.junit.Assert;
import org.junit.Test;

public class XmlCorpusWithNEsParserTest {

    private static final DocumentWithPositions expectedTexts[] = new DocumentWithPositions[] {
            new DocumentWithPositions("Dieser Text ist ein Testtext.", null, new Entity[] {}),
            new DocumentWithPositions("Der neue Jaguar ist kein Golf.", null, new Entity[] {
                    new Entity(9, 15, "http://animal/Jaguar", "Jaguar"),
                    new Entity(25, 29, "http://car/VWGolf", "Golf") }),
            new DocumentWithPositions("Am persischen Golf wird wieder Golf gespielt!", null, new Entity[] {
                    new Entity(3, 18, "http://geo/PersianGulf", "persischen Golf"),
                    new Entity(31, 35, "http://sport/Golf", "Golf") }) };

    @Test
    public void test() {
        XmlCorpusWithNEsParser reader = new XmlCorpusWithNEsParser();
        DocumentWithPositions texts[] = reader.parse(this.getClass().getClassLoader()
                .getResourceAsStream("test_corpus.xml"));
        for (int i = 0; i < expectedTexts.length; ++i) {
            Assert.assertEquals(expectedTexts[i].text, texts[i].text);
            Assert.assertEquals(expectedTexts[i].entities.length, texts[i].entities.length);
            for (int j = 0; j < expectedTexts[i].entities.length; ++j) {
                Assert.assertEquals(expectedTexts[i].entities[j].start, texts[i].entities[j].start);
                Assert.assertEquals(expectedTexts[i].entities[j].end, texts[i].entities[j].end);
                Assert.assertEquals(expectedTexts[i].entities[j].label, texts[i].entities[j].label);
                Assert.assertEquals(expectedTexts[i].entities[j].URI, texts[i].entities[j].URI);
            }
        }
    }
}
