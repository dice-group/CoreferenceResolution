package org.aksw.simba.preprocessing.datatypes;

public class TokenizedDocument {

    /**
     * Tokens of the documents text.
     */
    public String tokens[];
    /**
     * Entities inside the text and their position.
     */
    public Entity[] entities;

    public TokenizedDocument() {
    }

    public TokenizedDocument(String[] tokens, Entity[] entities) {
        this.tokens = tokens;
        this.entities = entities;
    }
}
