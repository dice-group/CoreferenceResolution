package org.aksw.sandbox;

import java.io.InputStream;
import java.util.List;

import org.aksw.sandbox.datatypes.DocumentWithPositions;
import org.aksw.sandbox.datatypes.Entity;
import org.aksw.sandbox.datatypes.TokenizedDocument;
import org.aksw.sandbox.io.XmlCorpusParser;
import org.aksw.sandbox.nlp.Fox;
import org.aksw.sandbox.nlp.Tokenizer;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.ObjectIntOpenHashMap;

public class AxelsPreprocessing {

    private static final Logger LOGGER = LoggerFactory.getLogger(AxelsPreprocessing.class);

    /**
     * This method loads the given corpus, searches named entities using FOX and creates context vectors for these named
     * entities. The context vectors are generated using a context window which cuts out all tokens +- s around the
     * named entity.
     * 
     * @param corpus
     *            the name of the corpus
     * @param windowSize
     *            the size s of the window (note that it is doubled since we are looking at +-s tokens!)
     * @return the entity word matrix
     */
    public static Matrix getCorpusAsMatrix(Corpora corpus, int windowSize) {
        String texts[] = readCorpus(corpus);
        DocumentWithPositions[] documentsWithPos = getTokenizedDocuments(texts);
        texts = null;
        performNER(documentsWithPos);

        TokenizedDocument[] tokenizedDocuments = mapEntitiesToTokens(documentsWithPos);
        documentsWithPos = null;
        return createMatrix(tokenizedDocuments, windowSize);
    }

    protected static DocumentWithPositions[] getTokenizedDocuments(String[] texts) {
        // for every token store its start and end (exclusive) position
        Tokenizer tokenizer = new Tokenizer();
        DocumentWithPositions[] docs = new DocumentWithPositions[texts.length];
        for (int i = 0; i < texts.length; ++i) {
            String[] tokens = tokenizer.tokenize(texts[i]);
            DocumentWithPositions doc = new DocumentWithPositions();
            doc.text = texts[i];
            doc.tokens = tokens;
            docs[i] = doc;
        }
        return docs;
    }

    protected static void performNER(DocumentWithPositions documentsWithPos[]) {
        // for every entity store its start and end (exclusive) position
        Fox fox = new Fox();
        for (int i = 0; i < documentsWithPos.length; ++i) {
            String text = documentsWithPos[i].text;
            List<Entity> entities = fox.getEntities(text);
            documentsWithPos[i].entities = entities.toArray(new Entity[entities.size()]);
        }
    }

    protected static String[] readCorpus(Corpora corpus) {
        /*
         * NOTE: If there are corpora with another format than Micha's wired XML, you can decide how they should be
         * loaded inside this method
         */
        return readCorpusFromXML(corpus);
    }

    protected static String[] readCorpusFromXML(Corpora corpus) {
        InputStream resource = AxelsPreprocessing.class.getClassLoader().getResourceAsStream(corpus.fileName);
        if (resource == null) {
            LOGGER.error("Couldn't find a corpus with the name \"" + corpus + "\". Returning null.");
            return null;
        }
        XmlCorpusParser parser = new XmlCorpusParser();
        String texts[] = parser.parse(resource);
        try {
            resource.close();
        } catch (Exception e) {
            // nothing to do
        }
        return texts;
    }

    public static TokenizedDocument[] mapEntitiesToTokens(DocumentWithPositions documentsWithPos[]) {
        TokenizedDocument documents[] = new TokenizedDocument[documentsWithPos.length];
        String tokens[];
        String text;
        Entity entities[];
        int currentEntity;
        int newStart, newEnd;
        int tokenStart, tokenEnd;
        for (int i = 0; i < documentsWithPos.length; ++i) {
            documents[i] = new TokenizedDocument();
            entities = documentsWithPos[i].entities;
            documents[i].entities = entities;
            tokens = documentsWithPos[i].tokens;
            documents[i].tokens = tokens;

            // the entities have a position in the text (= counted in chars) which has to be translated to the position
            // in the tokenized text (= counted in tokens)
            text = documentsWithPos[i].text;
            currentEntity = 0;
            newStart = -1;
            newEnd = -1;
            tokenEnd = 0;
            for (int j = 0; (j < tokens.length) && (currentEntity < entities.length); ++j) {
                // search the token inside the text
                tokenStart = text.indexOf(tokens[j], tokenEnd);
                if (tokenStart < 0) {
                    LOGGER.error("couldn't find the token \"" + tokens[j] + "\" in the remaining text (\""
                            + text.substring(tokenEnd) + "\"). Ignoring this token.");
                } else {
                    tokenEnd = tokenStart + tokens[j].length();
                    // If we are searching for the start of the current entity and the current token is this start
                    if ((newStart < 0) && (tokenStart >= entities[currentEntity].start)) {
                        newStart = j;
                    }
                    // If we are searching for the end of the current entity and the end of the current token is not a
                    // part of the current entity
                    if ((newEnd < 0) && (tokenEnd >= entities[currentEntity].end)) {
                        newEnd = (tokenStart > entities[currentEntity].end) ? j : (j + 1);

                        entities[currentEntity].start = newStart;
                        entities[currentEntity].end = newEnd;
                        ++currentEntity;
                        newStart = -1;
                        newEnd = -1;
                    }
                }
            }
            // If there are entities which couldn't be found inside the tokens
            if (currentEntity < entities.length) {
                if (newStart >= 0) {
                    entities[currentEntity].start = newStart;
                    entities[currentEntity].end = tokens.length;
                    ++currentEntity;
                }
                // If there should be more than one single entity, set there positions "behind" the end of the text and
                // their length to 0
                for (int j = 0; j < entities.length; ++j) {
                    entities[currentEntity].start = tokens.length;
                    entities[currentEntity].end = tokens.length;
                }
            }
        }
        return documents;
    }

    public static Matrix createMatrix(TokenizedDocument[] tokenizedDocuments, int windowSize) {
        // create vocabularies
        ObjectIntOpenHashMap<String> tokenVocabulary = new ObjectIntOpenHashMap<String>();
        int tokenIds[][] = new int[tokenizedDocuments.length][];
        for (int i = 0; i < tokenizedDocuments.length; ++i) {
            tokenIds[i] = new int[tokenizedDocuments[i].tokens.length];
            for (int j = 0; j < tokenizedDocuments[i].tokens.length; ++j) {
                if (tokenVocabulary.containsKey(tokenizedDocuments[i].tokens[j])) {
                    tokenIds[i][j] = tokenVocabulary.lget();
                } else {
                    tokenIds[i][j] = tokenVocabulary.size();
                    tokenVocabulary.put(tokenizedDocuments[i].tokens[j], tokenIds[i][j]);
                }
            }
        }

        ObjectIntOpenHashMap<String> entityVocabulary = new ObjectIntOpenHashMap<String>();
        int entityIds[][] = new int[tokenizedDocuments.length][];
        for (int i = 0; i < tokenizedDocuments.length; ++i) {
            entityIds[i] = new int[tokenizedDocuments[i].entities.length];
            for (int j = 0; j < tokenizedDocuments[i].entities.length; ++j) {
                if (entityVocabulary.containsKey(tokenizedDocuments[i].entities[j].URI)) {
                    entityIds[i][j] = entityVocabulary.lget();
                } else {
                    entityIds[i][j] = entityVocabulary.size();
                    entityVocabulary.put(tokenizedDocuments[i].entities[j].URI, entityIds[i][j]);
                }
            }
        }

        Matrix matrix = new Basic2DMatrix(entityVocabulary.size(), tokenVocabulary.size());
        // go through every document ...
        int entityId, end;
        for (int i = 0; i < tokenizedDocuments.length; ++i) {
            // ...and through every entity occurring inside the documents...
            for (int j = 0; j < tokenizedDocuments[i].entities.length; ++j) {
                entityId = entityIds[i][j];
                // ...and count the tokens before...
                end = tokenizedDocuments[i].entities[j].start;
                for (int t = Math.max(0, tokenizedDocuments[i].entities[j].start - windowSize); t < end; ++t) {
                    matrix.set(entityId, tokenIds[i][t], matrix.get(entityId, tokenIds[i][t]) + 1);
                }
                // ...and after the entity
                end = Math.min(tokenIds[i].length, tokenizedDocuments[i].entities[j].end + windowSize);
                for (int t = tokenizedDocuments[i].entities[j].end; t < end; ++t) {
                    matrix.set(entityId, tokenIds[i][t], matrix.get(entityId, tokenIds[i][t]) + 1);
                }
            }
        }

        return matrix;
    }

    public static enum Corpora {
        RSS500("500newsgoldstandard.xml"),
        REUTERS128("reuters.xml");

        public String fileName;

        private Corpora(String fileName) {
            this.fileName = fileName;
        }
    }
}
