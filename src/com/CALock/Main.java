package com.CALock;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Main {
    //TODO: the first node created is always considered as the root. So tread carefully.

    public static void main(String[] args) throws Exception {
        bulkCreateTest();
    }

    public static void singleCreateTest() throws IOException {
        String filePath = "";
        //String cleanedFilePath = "/Users/pandey/work/CALock/datasets/cycleOnRoot.txt";
        String cleanedFilePath = "/Users/pandey/work/CALock/datasets/cycleOnRoot.txt";
        if (!filePath.equals("")) {
            // Cleanup the data to read from. This set of code creates a graph with connected components.
            dataCleaning cleaner = new dataCleaning();
            HashMap<Integer, int[]> edges = cleaner.loadfile(filePath, ",");
            graphDefinition g = cleaner.navigateConnected(edges);
            cleaner.writeEdgeMapToFile(cleanedFilePath, g);
        }
        graph G = new graph();
        long singleCreateStart = System.currentTimeMillis();
        try (BufferedReader br = new BufferedReader(new FileReader(cleanedFilePath))) {
            String line = br.readLine();
            while (line != null) {
                if (line.charAt(0) != 'R') {
                    String[] edge = line.split(",");
                    int source = Integer.parseInt(edge[0]);
                    int target = Integer.parseInt(edge[1]);
                    G.addVertex(source, source);
                    G.addVertex(target, target);
                    G.createEdge(source, target);
                } else if (line.charAt(0) == 'R') {
                    int Root = Integer.parseInt(line.split(",")[1]);
                    G.addVertex(Root, Root);
                    G.root = G.vertices.get(Root);
                }
                line = br.readLine();
            }

            if (G.root == null) {
                G.findRootVertex();
            }
        } catch (Exception e) {
            System.out.println("Could not add line to the edgemap");
        }
        long singleCreateEnd = System.currentTimeMillis();
        System.out.println("Bulk Insertion took " + (float) (singleCreateEnd - singleCreateStart) / 1000 + "s");

        testUtils testInstance = new testUtils();

        if (G.vertices.size() < 100) {
            printer.printGraph(G);
            testInstance.testAllPairLSCA(G);
        } else {
            printer.printGraphInfo(G);
            testInstance.testRandomPairLSCA(G, 5);
        }
    }

    public static void bulkCreateTest() throws IOException {
        String filePath = "";
        //String cleanedFilePath = "/Users/pandey/work/CALock/datasets/cycleOnRoot.txt";
        String cleanedFilePath = "/Users/pandey/work/CALock/datasets/intersectingCycles.txt";

        if (!filePath.equals("")) {
            // Cleanup the data to read from. This set of code creates a graph with connected components.
            dataCleaning cleaner = new dataCleaning();
            HashMap<Integer, int[]> edges = cleaner.loadfile(filePath, ",");
            graphDefinition g = cleaner.navigateConnected(edges);
            cleaner.writeEdgeMapToFile(cleanedFilePath, g);
        }

        testUtils testInstance = new testUtils();
        graphDefinition gdef = testInstance.createEdgeMap(cleanedFilePath, ",");
        long bulkCreateStart = System.currentTimeMillis();
        // Preprocessing never assigns labels. Labels are only assigned on a well defined graph. A well defined graph has a root defined.
        graph G1 = new graph(gdef);
        long bulkCreateEnd = System.currentTimeMillis();
        System.out.println("Bulk Insertion took " + (float) (bulkCreateEnd - bulkCreateStart) / 1000 + "s");
        if (G1.vertices.size() < 100) {
            printer.printGraph(G1);
            testInstance.testAllPairLSCA(G1);
        } else {
            printer.printGraphInfo(G1);
            testInstance.testRandomPairLSCA(G1, 5);
        }
    }
}
