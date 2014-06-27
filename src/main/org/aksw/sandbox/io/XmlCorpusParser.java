package org.aksw.sandbox.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlCorpusParser implements XMLParserObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlCorpusParser.class);

    public static final String DOCUMENT_TAG_NAME = "Document";
    public static final String TEXT_WITH_NAMED_ENTITIES_TAG_NAME = "TextWithNamedEntities";
    public static final String TEXT_PART_TAG_NAME = "SimpleTextPart";
    public static final String NAMED_ENTITY_IN_TEXT_TAG_NAME = "NamedEntityInText";
    public static final String SIGNED_NAMED_ENTITY_IN_TEXT_TAG_NAME = "SignedNamedEntityInText";

    private BricsBasedXmlParser parser = new BricsBasedXmlParser(this);
    private StringBuilder textBuffer = new StringBuilder();
    private String data;
    private List<String> texts;

    public String[] parse(InputStream input) {
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

    public String[] parse(String input) {
        texts = new ArrayList<String>();
        parser.parse(input);
        String parsedTexts[] = texts.toArray(new String[texts.size()]);
        texts = null;
        return parsedTexts;
    }

    @Override
    public void handleOpeningTag(String tagString) {
        // delete data of former tags
        data = "";
    }

    @Override
    public void handleClosingTag(String tagString) {
        switch (tagString) {
        case DOCUMENT_TAG_NAME: {
            texts.add(textBuffer.toString());
            textBuffer.delete(0, textBuffer.capacity());
            break;
        }
        case TEXT_PART_TAG_NAME: // falls through
        case NAMED_ENTITY_IN_TEXT_TAG_NAME:
        case SIGNED_NAMED_ENTITY_IN_TEXT_TAG_NAME: {
            textBuffer.append(data);
            data = "";
            break;
        }
        default: {
            // nothing to do;
        }
        }
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
