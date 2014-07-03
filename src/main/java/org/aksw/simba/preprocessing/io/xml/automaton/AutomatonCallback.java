package org.aksw.simba.preprocessing.io.xml.automaton;

public interface AutomatonCallback {

    public void foundPattern(int patternId, int startPos, int length);
}
