package org.androidtown.myapplication;

/**
 * Created by hyk on 2017-05-24.
 */
public class Edge {

    private int fromEdgeIndex;
    private int toEdgeIndex;
    private int length;
    public Edge(){

    }
    public Edge(int fromEdgeIndex, int toEdgeIndex, int length) {
        this.fromEdgeIndex = fromEdgeIndex;
        this.toEdgeIndex = toEdgeIndex;
        this.length = length;
    }
    public void setFromEdgeIndex(int fromEdgeIndex) {
        this.fromEdgeIndex = fromEdgeIndex;
    }

    public void setToEdgeIndex(int toEdgeIndex) {
        this.toEdgeIndex = toEdgeIndex;
    }

    public void setLength(int length){
        this.length = length;
    }
    public int getFromEdgeIndex() {
        return fromEdgeIndex;
    }

    public int getToEdgeIndex() {
        return toEdgeIndex;
    }

    public int getLength() {
        return length;
    }

    // determines the neighboring node of a supplied node, based on the two nodes connected by this edge
    public int getNeighborIndex(int nodeIndex) {
        if (this.fromEdgeIndex == nodeIndex) {
            return this.toEdgeIndex;
        } else {
            return this.fromEdgeIndex;
        }
    }

}