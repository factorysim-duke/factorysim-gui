package edu.duke.ece651.factorysim;

public interface PooledObject {
    void onBorrowed();
    void onReleased();
}
