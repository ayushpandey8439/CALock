package com.CALock;

import java.util.HashMap;
import java.util.List;

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
                    //G.createEdgeWithoutUpdatingPaths(source, target);
                    G.createEdge(source, target);
                    //TODO: Change this to see if Preprocessing and dynamic graphs have the same result.
                } catch (Exception e) {
                    System.out.println("Create Edge failed for " + source + " -> " + target);
                }
            }
        }


        //Assign labels for a new hierarchy with DF Exploration.
        //preProcessor P = new preProcessor();
        //G = P.assignLabels(G);

        printGraph(G);

        testUtils testInstance = new testUtils();
        List<LSCAResult> failedParis = testInstance.testAllPairLSCA(G, numNodes);
        if (failedParis.size() > 0) {
            for (LSCAResult p : failedParis) {
                System.out.print("Nodes: " + p.nodes[0] + " " + p.nodes[1] + " LSCA:");
                for (int n : p.LSCAs) {
                    System.out.print(n);
                }
                System.out.println(" Status: " + p.status);
            }
            System.out.println();
        }

    }
}
