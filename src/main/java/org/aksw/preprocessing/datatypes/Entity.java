package org.aksw.preprocessing.datatypes;

public class Entity {
    public int start;
    public int end;
    public String URI;
    public String label;

    public Entity() {
    }

    public Entity(int start, int end, String uRI, String label) {
        this.start = start;
        this.end = end;
        URI = uRI;
        this.label = label;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String uRI) {
        URI = uRI;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
