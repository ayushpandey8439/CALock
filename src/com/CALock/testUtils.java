package com.CALock;

import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;

import static org.apache.commons.lang3.RandomUtils.nextInt;

public class testUtils {

    private int[][] DFSPaths;
    private int[] currentPath;


    //TODO: Fix random graph generation.
    public static HashMap<Integer, int[]> createRandomDAG(int numNodes) {
        HashMap<Integer, int[]> edgeMap = new HashMap<Integer, int[]>();

        for (int i = 1; i <= numNodes; i++) {
            int childCount = nextInt(i, numNodes + 1);
            int[] childList = new int[]{};
            for (int j = 1; j <= childCount; j++) {
                int child = nextInt(i, numNodes);
                if (child != i) {
                    childList = ArrayUtils.addAll(childList, child);
                }
            }
            edgeMap.put(i, childList);
        }

        /*
        edgeMap.put(1, new int[]{2, 6});
        edgeMap.put(4, new int[]{5});
        edgeMap.put(2, new int[]{3, 8, 5});
        edgeMap.put(3, new int[]{4});
        edgeMap.put(6, new int[]{7});
        edgeMap.put(7, new int[]{5});
         */

        return edgeMap;
    }


    private int findTraversalLSCA(graph G, int... V) {
        this.currentPath = new int[]{G.root.Id};
        this.DFSPaths = new int[][]{};
        for (int v : V) {
            dfs(G.root, G.vertices.get(v));
            //AllDFSPaths = ArrayUtils.addAll(AllDFSPaths, );
        }


        int[] shortestPath = DFSPaths.length > 0 ? DFSPaths[0] : new int[]{};// For non-reachable pairs, DFS Paths will be empty.
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
                int PLSCA = G.findPathLSCA(G, i, j);
                int TLSCA = findTraversalLSCA(G, i, j);
                if ((PLSCA != TLSCA) && (PLSCA != 0 && TLSCA != 0)) {
                    failedPairs = ArrayUtils.addAll(failedPairs, "" + i + " " + j);
                }
            }
        }
        return failedPairs;
    }
}
