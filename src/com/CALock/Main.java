package com.CALock;

import java.util.HashMap;

public class Main {
    //TODO: the first node created is always considered as the root. So tread carefully.

    public static void main(String[] args) throws Exception {
        testUtils testInstance = new testUtils();
        HashMap<Integer, int[]> edgeMap = testUtils.createEdgeMap("/Users/pandey/work/CALock/datasets/webNotreDame.txt", ",");
        long singleCreateStart = System.currentTimeMillis();
        graph G1 = testInstance.createDAG(edgeMap, true);
        long singleCreateEnd = System.currentTimeMillis();
        System.out.println("Single Insertion took " + (float) (singleCreateEnd - singleCreateStart) / 1000 + "s");
        printer.printGraphInfo(G1);
        testInstance.testRandomPairLSCA(G1, 1);

        /*
        long massiveCreateStart = System.currentTimeMillis();
        graph G2 = testInstance.createDAG(edgeMap, false);
        long massiveCreateEnd = System.currentTimeMillis();
        System.out.println("Massive Insertion took " + (float) (massiveCreateEnd - massiveCreateStart) / 1000 + "s");
        printGraphInfo(G2);


        /*

        long startTime = System.currentTimeMillis();
        System.out.println("All pair started at: " + startTime);
        testInstance.testAllPairLSCA(G);
        long endTime = System.currentTimeMillis();
        System.out.println("All pair finished at: " + endTime);
        long duration = (endTime - startTime);
        System.out.println("All pair LSCA took: " + duration);
        //G.createEdge(1, 4);
        //G.createEdge(1, 3);
        //printGraph(G);
        //G.removeEdge(7, 5);
        //printGraph(G);
        //testUtils testInstance1 = new testUtils();
        //testInstance1.testAllPairLSCA(G);

         */

    }
}
