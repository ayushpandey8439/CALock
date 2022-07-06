package com.CALock;

import static com.CALock.printer.printGraph;

public class Main {

    public static void main(String[] args) throws Exception {
        int numNodes = 5;
        testUtils testInstance = new testUtils();
        graph G = testInstance.createDAG(numNodes, true);
        printGraph(G);
        testInstance.testAllPairLSCA(G);

        //G.createEdge(1, 4);
        //G.createEdge(1, 3);
        //printGraph(G);
        //G.removeEdge(7, 5);
        //printGraph(G);
        //testUtils testInstance1 = new testUtils();
        //testInstance1.testAllPairLSCA(G);

    }
}
