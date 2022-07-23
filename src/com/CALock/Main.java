package com.CALock;

import java.util.HashMap;

public class Main {
    //TODO: the first node created is always considered as the root. So tread carefully.

    public static void main(String[] args) throws Exception {
        String filePath = "";
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
        graph G1 = new graph(gdef, true);
        long singleCreateEnd = System.currentTimeMillis();
        System.out.println("Single Insertion took " + (float) (singleCreateEnd - singleCreateStart) / 1000 + "s");
        printer.printGraphInfo(G1);
        if(G1.vertices.size() < 100){
            testInstance.testAllPairLSCA(G1);
        }else{
            testInstance.testRandomPairLSCA(G1, 5);
        }


        long massiveCreateStart = System.currentTimeMillis();
        graph G2 = new graph(gdef, true);
        long massiveCreateEnd = System.currentTimeMillis();
        System.out.println("Massive Insertion took " + (float) (massiveCreateEnd - massiveCreateStart) / 1000 + "s");
        printer.printGraphInfo(G1);
        if(G2.vertices.size() < 100){
            testInstance.testAllPairLSCA(G1);
        }else{
            testInstance.testRandomPairLSCA(G1, 5);
        }


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
