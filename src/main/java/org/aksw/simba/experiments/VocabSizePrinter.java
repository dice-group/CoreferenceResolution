package org.aksw.simba.experiments;

import java.io.FileNotFoundException;

import org.aksw.simba.preprocessing.Preprocessing;
import org.aksw.simba.preprocessing.Preprocessing.Corpora;
import org.aksw.simba.preprocessing.datatypes.TokenizedDocument;
import org.la4j.matrix.Matrix;

public class VocabSizePrinter {

    public static void main(String args[]) throws FileNotFoundException {
        
        TokenizedDocument[] documents;
        for (Corpora corpus : new Corpora[] { Corpora.REUTERS128, Corpora.RSS500, Corpora.GOLF }) {
            documents = Preprocessing.getCorpus(corpus);
            Matrix M = Preprocessing.createMatrix(documents, 4);
            System.out.println(corpus.toString() + " vocabularysize=" + M.columns());
        }
    }
}
