package com.CALock;

import java.util.HashMap;

import static com.CALock.printer.printGraph;
import static com.CALock.testUtils.createRandomDAG;

public class Main {

    public static void main(String[] args) throws Exception {
        graph G = new graph();
        int numNodes = 8;
        for (int i = 1; i <= numNodes; i++) {
            try {
                G.addVertex(i);
            } catch (Exception e) {
                System.out.println("Encountered Exception " + e.getMessage());
            }

        }

        G.root = G.vertices.get(1);

        HashMap<Integer, int[]> edgeMap = createRandomDAG(numNodes);
        for (int source : edgeMap.keySet()) {
            for (int target : edgeMap.get(source)) {
                try {
                    G.createEdge(source, target);
                } catch (Exception e) {
                    System.out.println("Create Edge failed for " + source + " -> " + target);
                }
            }
        }

        printGraph(G);

        testUtils testInstance = new testUtils();
        String[] failedParis = testInstance.testAllPairLSCA(G, numNodes);
        if (failedParis.length > 0) {
            for (String p : failedParis) {
                System.out.println(p);
            }
            System.out.println();
            throw new Exception("Some case failed for the TLSCA and PLSCA check ");
        }

    }
}
