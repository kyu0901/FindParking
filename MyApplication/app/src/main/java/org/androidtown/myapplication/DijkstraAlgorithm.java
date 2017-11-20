package org.androidtown.myapplication;

import android.util.Log;

import java.util.*;

public class DijkstraAlgorithm {
    private Node[] nodes;
    private int noOfNodes;
    private Edge[] edges;
    private int noOfEdges;
    private Stack<Integer> S;
    private int[] parkdes;
    private int[] path;
    private int visit;
    public DijkstraAlgorithm(Edge[] edges, int[] des) {
        this.edges = edges;
        parkdes = des;
        this.noOfEdges = calculateNoOfEdges(this.edges); //제일 숫자가 높은 엣지 계산(노드 총 수)
        this.nodes = new Node[this.noOfEdges]; //엣지 총 개수 만큼 노드 배열 선언
        this.path = new int[this.noOfEdges];
        //각 노드 초기화
        for (int n = 0; n < this.noOfEdges; n++) {
            this.nodes[n] = new Node();
        }

        // add all the edges to the nodes, each edge added to two nodes (to and from)
        //노드 수
        this.noOfNodes = edges.length;

        for (int edgeToAdd = 0; edgeToAdd < this.noOfNodes; edgeToAdd++) {
            this.nodes[edges[edgeToAdd].getFromEdgeIndex()].getEdges().add(edges[edgeToAdd]);
            this.nodes[edges[edgeToAdd].getToEdgeIndex()].getEdges().add(edges[edgeToAdd]);
        }

    }

    private int calculateNoOfEdges(Edge[] edges) {
        int noOfEdges = 0;
        Log.i("calculateEdge : "," yeees");
        for (Edge e : edges) {
            if (e.getToEdgeIndex() > noOfEdges)
                noOfEdges = e.getToEdgeIndex();
            if (e.getFromEdgeIndex() > noOfEdges)
                noOfEdges = e.getFromEdgeIndex();
        }
        noOfEdges++;
        //엣지 총 개수
        return noOfEdges;
    }

    public void calculateShortestDistances() {
        // node 0 as source
        this.nodes[0].setDistanceFromSource(0); //시작점의 거리 0
        int nextEdge = 0;
        visit = 0;
        // visit every edge
        for (int i = 0; i < this.nodes.length; i++) {
            // loop around the nodes of current edge
            ArrayList<Edge> currentEdgeNodes = this.nodes[nextEdge].getEdges();
            Log.i("i="+i,""+currentEdgeNodes.get(0).getToEdgeIndex());
            for (int joinedNode = 0; joinedNode < currentEdgeNodes.size(); joinedNode++) {
                int neighborIndex = currentEdgeNodes.get(joinedNode).getNeighborIndex(nextEdge);

                // only if not visited
                if (!this.nodes[neighborIndex].isVisited()) {
                    int tentative = this.nodes[nextEdge].getDistanceFromSource() + currentEdgeNodes.get(joinedNode).getLength();
                    visit = nextEdge;
                    Log.i("i 1 : ",""+neighborIndex);
                    Log.i("i 1 eged : ",""+nextEdge);
                    if (tentative < nodes[neighborIndex].getDistanceFromSource()) {
                        nodes[neighborIndex].setDistanceFromSource(tentative);
                        path[neighborIndex] = visit;
                        Log.i("i 2 : ",""+neighborIndex);
                        Log.i("i 2 edge : ",""+nextEdge);
                    }
                }
            }

            // all neighbours checked so Edge visited
            nodes[nextEdge].setVisited(true);
            // next Edge must be with shortest distance
            nextEdge = getEdgeShortestDistanced();

        }
    }

    // now we're going to implement this method in next part !
    private int getEdgeShortestDistanced() {
        int storedNodeIndex = 0;
        int storedDist = Integer.MAX_VALUE;

        for (int i = 0; i < this.nodes.length; i++) {
            int currentDist = this.nodes[i].getDistanceFromSource();

            if (!this.nodes[i].isVisited() && currentDist < storedDist) {
                storedDist = currentDist;
                storedNodeIndex = i;
            }
        }
        return storedNodeIndex;
    }

    private void BackTracking(int[] path, int start, int end){
        S = new Stack<Integer>();
        int temp = end;
        while(temp != start){
            S.push(temp);
            temp = path[temp];
        }
    }
    // display result
    public Stack<Integer> printResult() {
        int min = 9999;
        int minEdge = 0;
        for(int i = 0; i<parkdes.length; i++){
            if(min > nodes[parkdes[i]].getDistanceFromSource()) {
                min = nodes[parkdes[i]].getDistanceFromSource();
                minEdge = parkdes[i];
            }
        }
        BackTracking(path, 0, minEdge);

        return S;
        /*while(!S.isEmpty()){
            int i = S.pop();
            System.out.println("NOde :::::: "+i);

        }*/
        /*
        for(int i =0 ; i < path.length;i++){
            System.out.println("path ["+i+"] = "+ path[i]);
        }

        for (int i = 0; i < this.nodes.length; i++) {
            output += ("\nThe shortest distance from edge 0 to edge " + i + " is " + nodes[i].getDistanceFromSource());
            Log.i("shortest : ", ""+nodes[i].getDistanceFromSource());
        }
        */
    }

    public Node[] getNodes() {
        return nodes;
    }

    public int getNoOfNodes() {
        return noOfNodes;
    }

    public Edge[] getEdges() {
        return edges;
    }

    public int getNoOfEdges() {
        return noOfEdges;
    }

}