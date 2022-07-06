package com.CALock;

import static com.CALock.printer.printGraph;
import static com.CALock.testUtils.createDAG;

public class Main {

    public static void main(String[] args) throws Exception {
        int numNodes = 5;
        graph G = createDAG(numNodes, true);
        printGraph(G);
        testUtils testInstance = new testUtils();
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
