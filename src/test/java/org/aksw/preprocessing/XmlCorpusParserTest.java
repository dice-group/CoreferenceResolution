package org.aksw.preprocessing;

import org.aksw.preprocessing.io.XmlCorpusParser;
import org.junit.Assert;
import org.junit.Test;

public class XmlCorpusParserTest {

    private static final String expectedTexts[] = new String[] { "Dieser Text ist ein Testtext.",
            "Der neue Jaguar ist kein Golf.",
            "Am persischen Golf wird wieder Golf gespielt!" };

    @Test
    public void test() {
        XmlCorpusParser reader = new XmlCorpusParser();
        String texts[] = reader.parse(this.getClass().getClassLoader().getResourceAsStream("test_corpus.xml"));
        Assert.assertArrayEquals(expectedTexts, texts);
    }
}
