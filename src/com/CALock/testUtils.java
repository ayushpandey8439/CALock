package com.CALock;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

class LSCAResult {
    public int[] nodes;

    public int[] LSCAs;

    public boolean status;

}

public class testUtils {

    private int[][] DFSPaths;
    private int[] currentPath;

    public HashMap<Integer, int[]> createEdgeMap() {
        HashMap<Integer, int[]> edgeMap = new HashMap<>();
        edgeMap.put(1, new int[]{2});
        edgeMap.put(2, new int[]{3});
        edgeMap.put(3, new int[]{2, 4});
        edgeMap.put(4, new int[]{5});
        edgeMap.put(5, new int[]{3});

        return edgeMap;
    }

    public graph createDAG(int numNodes, boolean assignLabelsDuringCreation) {
        graph G = new graph();
        for (int i = 1; i <= numNodes; i++) {
            try {
                G.addVertex(i);
            } catch (Exception e) {
                System.out.println("Encountered Exception " + e.getMessage());
            }
        }

        G.root = G.vertices.get(1);

        HashMap<Integer, int[]> edgeMap = createEdgeMap();

        for (int source : edgeMap.keySet()) {
            for (int target : edgeMap.get(source)) {
                try {
                    if (assignLabelsDuringCreation) {
                        G.createEdge(source, target);
                    } else {
                        G.createEdgeWithoutUpdatingPaths(source, target);
                    }
                } catch (Exception e) {
                    System.out.println("Create Edge failed for " + source + " -> " + target);
                }
            }
        }

        //Assign labels for a new hierarchy with DF Exploration.
        if (!assignLabelsDuringCreation) {
            preProcessor P = new preProcessor();
            G = P.assignLabels(G);
        }

        // Any node without a parent is considered a root.
        // If there is more than one root node then create a sentinel root above all of them.
        vertex[] roots = new vertex[]{};
        for (vertex v : G.vertices.values()) {
            if (v.parents.size() == 0 && !v.isSentinel()) {
                roots = ArrayUtils.addAll(roots, v);
            }
        }

        if (roots.length > 1) {
            G.vertices.put(G.sentinel.Id, G.sentinel);
            G.root = G.sentinel;
            for (vertex r : roots) {
                try {
                    G.createEdge(G.sentinel.Id, r.Id);
                } catch (Exception e) {
                    System.out.println("Create Edge failed for " + G.sentinel + " -> " + r.Id);
                }
            }
        } else {
            G.root = roots[0];
            G.sentinel.initialiseVertexMetadata();
            G.root.parents = new HashMap<>();
            G.vertices.remove(G.sentinel.Id);
        }

        return G;
    }

    private int findTraversalLSCA(graph G, int... V) {
        this.currentPath = new int[]{G.root.Id};
        this.DFSPaths = new int[][]{};
        for (int v : V) {
            dfs(G.root, G.vertices.get(v), new HashMap<Integer, vertex>());
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

    private void dfs(vertex source, vertex target, HashMap<Integer, vertex> visited) {
        visited.put(source.Id, source);
        if (source.Id == target.Id) {
            DFSPaths = ArrayUtils.addAll(DFSPaths, currentPath);
        } else {
            for (vertex v : source.children.values()) {
                currentPath = ArrayUtils.addAll(currentPath, v.Id);
                if (visited.get(v.Id) == null) {
                    dfs(v, target, visited);
                }
                currentPath = ArrayUtils.remove(currentPath, currentPath.length - 1);
            }
        }
    }


    public void testAllPairLSCA(graph G) {
        Map<Pair<Integer, Integer>, LSCAResult> lscaResults = new HashMap<>();
        for (vertex a : G.vertices.values()) {
            for (vertex b : G.vertices.values()) {
                Pair<Integer, Integer> key = Pair.of(a.Id, b.Id);
                Pair<Integer, Integer> keyR = Pair.of(b.Id, a.Id);
                if (lscaResults.get(key) == null && lscaResults.get(keyR) == null) {
                    int PLSCA = G.findPathLSCA(G, a.Id, b.Id);
                    int TLSCA = findTraversalLSCA(G, a.Id, b.Id);
                    LSCAResult result = new LSCAResult();
                    result.nodes = new int[]{a.Id, b.Id};
                    result.LSCAs = new int[]{PLSCA, TLSCA};
                    result.status = PLSCA == TLSCA; // This was set to zero Imagine why?
                    lscaResults.put(key, result);
                }

            }
        }

        System.out.println("Total Pairs examined: " + lscaResults.size());
        for (LSCAResult p : lscaResults.values()) {
            System.out.print("Nodes: " + p.nodes[0] + " " + p.nodes[1] + " LSCA:");
            for (int n : p.LSCAs) {
                System.out.print(n + " ");
            }
            System.out.println(" Status: " + p.status);
        }
    }
}
