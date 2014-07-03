package org.aksw.simba.preprocessing;

import org.aksw.simba.preprocessing.datatypes.TokenizedDocument;
import org.apache.lucene.search.spell.NGramDistance;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;

public class EntityLabelSimMatrixCreator {

    public static Matrix getEntityLabelSimMatrix(TokenizedDocument[] documents) {
        int entityCount = 0;
        for (int i = 0; i < documents.length; ++i) {
            entityCount += documents[i].entities.length;
        }
        String labels[] = new String[entityCount];
        entityCount = 0;
        for (int d = 0; d < documents.length; ++d) {
            for (int e = 0; e < documents[d].entities.length; ++e) {
                labels[entityCount] = documents[d].entities[e].label;
                ++entityCount;
            }
        }
        Matrix stringSimMatrix = new Basic2DMatrix(entityCount, entityCount);
        NGramDistance nGramDistance = new NGramDistance(3);
        double similarity;
        for (int i = 0; i < labels.length; ++i) {
            stringSimMatrix.set(i, i, 1);
            for (int j = i + 1; j < labels.length; ++j) {
                similarity = nGramDistance.getDistance(labels[i], labels[j]);
                stringSimMatrix.set(i, j, similarity);
                stringSimMatrix.set(j, i, similarity);
            }
        }
        return stringSimMatrix;
    }
}
