package org.aksw.sandbox.datatypes;

public class DocumentWithPositions {

    /**
     * Text of the document as String.
     */
    public String text;
    /**
     * Tokens of the documents text.
     */
    public String tokens[];
    /**
     * Entities inside the text and their position.
     */
    public Entity[] entities;

    public DocumentWithPositions() {
    }

    public DocumentWithPositions(String text, String[] tokens, Entity[] entities) {
        this.text = text;
        this.tokens = tokens;
        this.entities = entities;
    }

}
