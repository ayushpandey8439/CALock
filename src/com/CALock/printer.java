package com.CALock;

public class printer {
    public static void printGraphInfo(graph G) {
        System.out.println("Number of Vertices: " + G.vertices.size());
    }

    public static void printGraph(graph G) {
        for (vertex V : G.vertices.values()) {
            System.out.print("Node: " + V.Id + " Children: [");
            for (int c : V.children.keySet()) {
                System.out.print(c + ", ");
            }

            System.out.print("] Parents: [");
            for (int p : V.parents.keySet()) {
                System.out.print(p + ", ");
            }
            System.out.print("] LowPath: ");
            for (int p : V.lowPath) {
                System.out.print(p + ",");
            }
            System.out.print(" HighPath: ");
            for (int p : V.highPath) {
                System.out.print(p + ",");
            }
            System.out.println();
        }
    }
}
