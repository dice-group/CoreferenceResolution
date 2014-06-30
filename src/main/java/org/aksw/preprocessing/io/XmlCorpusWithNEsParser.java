package org.aksw.preprocessing.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.aksw.preprocessing.datatypes.DocumentWithPositions;
import org.aksw.preprocessing.datatypes.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlCorpusWithNEsParser implements XMLParserObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlCorpusWithNEsParser.class);

    public static final String DOCUMENT_TAG_NAME = "Document";
    public static final String TEXT_WITH_NAMED_ENTITIES_TAG_NAME = "TextWithNamedEntities";
    public static final String TEXT_PART_TAG_NAME = "SimpleTextPart";
    public static final String NAMED_ENTITY_IN_TEXT_TAG_NAME = "NamedEntityInText";
    public static final String SIGNED_NAMED_ENTITY_IN_TEXT_TAG_NAME = "SignedNamedEntityInText";
    public static final String URI_ATTRIBUTE_NAME = "uri";

    private BricsBasedXmlParser parser = new BricsBasedXmlParser(this);
    private StringBuilder textBuffer = new StringBuilder();
    private String data;
    private List<Entity> currentEntities = new ArrayList<Entity>();
    private List<DocumentWithPositions> documents;

    /**
     * Returns the documents and the named entities which are tagged inside the documents. Note that the tokens array of
     * the {@link DocumentWithPositions} objects is set to null.
     * 
     * @param input
     * @return
     */
    public DocumentWithPositions[] parse(InputStream input) {
        StringBuilder text = new StringBuilder();
        byte buffer[] = new byte[256];
        int length;
        try {
            while (input.available() > 0) {
                length = input.read(buffer);
                text.append(new String(buffer, 0, length));
            }
        } catch (IOException e) {
            LOGGER.error("Couldn't read input. Returning null.", e);
            return null;
        }
        return parse(text.toString());
    }

    public DocumentWithPositions[] parse(String input) {
        documents = new ArrayList<DocumentWithPositions>();
        parser.parse(input);
        DocumentWithPositions parsedTexts[] = documents.toArray(new DocumentWithPositions[documents.size()]);
        documents = null;
        return parsedTexts;
    }

    @Override
    public void handleOpeningTag(String tagString) {
        // delete data of former tags
        data = "";
        if (tagString.startsWith(NAMED_ENTITY_IN_TEXT_TAG_NAME)
                || tagString.startsWith(SIGNED_NAMED_ENTITY_IN_TEXT_TAG_NAME)) {
            Entity entity = parseNamedEntityInText(tagString);
            if (entity != null) {
                currentEntities.add(entity);
            } else {
                LOGGER.warn("Found an entity but couldn't parse it!");
            }
        }
    }

    @Override
    public void handleClosingTag(String tagString) {
        switch (tagString) {
        case DOCUMENT_TAG_NAME: {
            documents.add(new DocumentWithPositions(textBuffer.toString(), null, currentEntities
                    .toArray(new Entity[currentEntities.size()])));
            textBuffer.delete(0, textBuffer.capacity());
            currentEntities.clear();
            break;
        }
        case SIGNED_NAMED_ENTITY_IN_TEXT_TAG_NAME: // falls through
        case NAMED_ENTITY_IN_TEXT_TAG_NAME: {
            Entity entity = currentEntities.get(currentEntities.size() - 1);
            entity.start = textBuffer.length();
            entity.end = entity.start + data.length();
            entity.label = data;
            // falls through
        }
        case TEXT_PART_TAG_NAME: {
            textBuffer.append(data);
            data = "";
            break;
        }
        default: {
            // nothing to do;
        }
        }
    }

    protected Entity parseNamedEntityInText(String tag) {
        String uri = null;
        int start = 0, end = 0;
        try {
            start = tag.indexOf(' ') + 1;
            end = tag.indexOf('=', start);
            String key, value;
            while (end > 0) {
                key = tag.substring(start, end).trim();
                end = tag.indexOf('"', end);
                start = tag.indexOf('"', end + 1);
                value = tag.substring(end + 1, start);
                if (key.equals(URI_ATTRIBUTE_NAME)) {
                    uri = value;
                }
                ++start;
                end = tag.indexOf('=', start);
            }
            return new Entity(-1, -1, uri, null);
        } catch (Exception e) {
            LOGGER.error("Couldn't parse NamedEntityInText tag (" + tag + "). Returning null.", e);
        }
        return null;
    }

    @Override
    public void handleData(String data) {
        this.data = data;
    }

    @Override
    public void handleEmptyTag(String tagString) {
        // nothing to do
    }

}
