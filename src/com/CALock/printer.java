package com.CALock;

public class printer {
    public static void printGraphInfo(graph G) {
        System.out.println("Number of Vertices: " + G.vertices.size());
    }

    public static void printGraph(graph G) {
        System.out.println("Root: " + G.root.Id);
        for (vertex V : G.vertices.values()) {
            System.out.print("Node: " + V.Id + "\n\tChildren: [");
            for (int c : V.children.keySet()) {
                System.out.print(c + ", ");
            }
            System.out.print("]\n\tLowPath " + V.lowPath.length + ": ");
            for (int p : V.lowPath) {
                System.out.print(p + ",");
            }
            System.out.print("\n\tHighPath " + V.highPath.length + " : ");
            for (int p : V.highPath) {
                System.out.print(p + ",");
            }
            System.out.print("\n\tLSCA Length: " + V.LSCAPathLength);
            System.out.println("\n");
        }
    }
}
