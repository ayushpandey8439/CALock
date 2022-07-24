package com.CALock;

import java.util.HashMap;

public class Main {
    //TODO: the first node created is always considered as the root. So tread carefully.

    public static void main(String[] args) throws Exception {
        String filePath = "";
        //String cleanedFilePath = "/Users/pandey/work/CALock/datasets/cycleOnRoot.txt";
        String cleanedFilePath = "/Users/pandey/work/CALock/datasets/email-Enron-c.txt";

        if (!filePath.equals("")) {
            // Cleanup the data to read from. This set of code creates a graph with connected components.
            dataCleaning cleaner = new dataCleaning();
            HashMap<Integer, int[]> edges = cleaner.loadfile(filePath, ",");
            graphDefinition g = cleaner.navigateConnected(edges);
            cleaner.writeEdgeMapToFile(cleanedFilePath, g);
        }

        testUtils testInstance = new testUtils();
        graphDefinition gdef = testInstance.createEdgeMap(cleanedFilePath, ",");
        long singleCreateStart = System.currentTimeMillis();
        // Preprocessing never assigns labels. Labels are only assigned on a well defined graph. A well defined graph has a root defined.
        graph G1 = new graph(gdef);
        long singleCreateEnd = System.currentTimeMillis();
        System.out.println("Single Insertion took " + (float) (singleCreateEnd - singleCreateStart) / 1000 + "s");
        printer.printGraphInfo(G1);
        if (G1.vertices.size() < 100) {
            testInstance.testAllPairLSCA(G1);
        } else {
            testInstance.testRandomPairLSCA(G1, 5);
        }
        System.out.println("Adding edge to the graph.");
        G1.addVertex(40000, 40000);
        G1.createEdge(726, 40000);
        testInstance.testLSCAForPair(G1, 4000, 726);


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
