package com.CALock;

import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;

public class testUtils {

    private int[][] DFSPaths;
    private int[] currentPath;


    public static HashMap<Integer, int[]> createRandomDAG(int numNodes) {
        HashMap<Integer, int[]> edgeMap = new HashMap<Integer, int[]>();

        edgeMap.put(1, new int[]{2, 6});
        edgeMap.put(4, new int[]{5});
        edgeMap.put(2, new int[]{3, 8, 5});
        edgeMap.put(3, new int[]{4});
        edgeMap.put(6, new int[]{7});
        edgeMap.put(7, new int[]{5});

        return edgeMap;
    }

    public static int findPathLSCA(graph G, int... V) {
        int[][] studyPaths = new int[][]{};
        for (int v : V) {
            vertex vert = G.vertices.get(v);
            studyPaths = ArrayUtils.addAll(studyPaths, vert.lowPath, vert.highPath);
        }

        int[] shortestPath = studyPaths[0];
        for (int[] p : studyPaths) {
            if (p.length < shortestPath.length) {
                shortestPath = p;
            }
        }

        int lowestNode = 0;
        for (int i = 0; i < shortestPath.length; i++) {
            boolean isCommon = true;
            for (int j = 0; j < studyPaths.length; j++) {
                if (studyPaths[j][i] != shortestPath[i]) {
                    isCommon = false;
                    break;
                }
            }

            if (!isCommon) {
                break;
            } else {
                lowestNode = shortestPath[i];
            }
        }
        return lowestNode;
    }


    private int findTraversalLSCA(graph G, int... V) {
        this.currentPath = new int[]{G.root.Id};
        this.DFSPaths = new int[][]{};
        for (int v : V) {
            dfs(G.root, G.vertices.get(v));
            //AllDFSPaths = ArrayUtils.addAll(AllDFSPaths, );
        }


        int[] shortestPath = DFSPaths[0];
        for (int[] p : DFSPaths) {
            if (p.length < shortestPath.length) {
                shortestPath = p;
            }
        }

        int lowestNode = 0;
        for (int i = 0; i < shortestPath.length; i++) {
            boolean isCommon = true;
            for (int j = 0; j < DFSPaths.length; j++) {
                if (DFSPaths[j][i] != shortestPath[i]) {
                    isCommon = false;
                    break;
                }
            }

            if (!isCommon) {
                break;
            } else {
                lowestNode = shortestPath[i];
            }
        }
        return lowestNode;

    }
    //Make this private in this class

    private void dfs(vertex source, vertex target) {
        if (source.Id == target.Id) {
            DFSPaths = ArrayUtils.addAll(DFSPaths, currentPath);
            return;
        } else {
            for (vertex v : source.children.values()) {
                currentPath = ArrayUtils.addAll(currentPath, v.Id);
                dfs(v, target);
                currentPath = ArrayUtils.remove(currentPath, currentPath.length - 1);
            }
        }
    }


    public String[] testAllPairLSCA(graph G, int numNodes) {
        String[] failedPairs = new String[]{};
        for (int i = 1; i <= numNodes; i++) {
            for (int j = i + 1; j <= numNodes; j++) {
                int PLSCA = findPathLSCA(G, i, j);
                int TLSCA = findTraversalLSCA(G, i, j);
                if (PLSCA != TLSCA) {
                    failedPairs = ArrayUtils.addAll(failedPairs, "" + i + " " + j);
                }
            }
        }
        return failedPairs;
    }
}
