package com.CALock;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class LSCAResult {
    public int[] nodes;

    public int[] LSCAs;

    public boolean status;

}

public class testUtils {

    private int[][] DFSPaths;
    private int[] currentPath;

    public static graph createDAG(int numNodes, boolean assignLabelsDuringCreation) {
        graph G = new graph();
        for (int i = 1; i <= numNodes; i++) {
            try {
                G.addVertex(i);
            } catch (Exception e) {
                System.out.println("Encountered Exception " + e.getMessage());
            }
        }

        G.root = G.vertices.get(1);

        HashMap<Integer, int[]> edgeMap = new HashMap<>();
        edgeMap.put(1, new int[]{2});
        edgeMap.put(3, new int[]{2});
        edgeMap.put(4, new int[]{2});

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
            if (v.parents.size() == 0) {
                roots = ArrayUtils.addAll(roots, v);
            }
        }

        if (roots.length > 1) {
            int newRoot = G.addVertex(0);
            G.root = G.vertices.get(newRoot);

            for (vertex r : roots) {
                try {
                    G.createEdge(newRoot, r.Id);
                } catch (Exception e) {
                    System.out.println("Create Edge failed for " + newRoot + " -> " + r.Id);
                }
            }
        }

        return G;
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


    public void testAllPairLSCA(graph G) {
        List<LSCAResult> lscaRestults = new ArrayList<>();
        int numNodes = G.vertices.size();

        for (int i = 1; i <= numNodes; i++) {
            for (int j = i + 1; j <= numNodes; j++) {
                int PLSCA = G.findPathLSCA(G, i, j);
                int TLSCA = findTraversalLSCA(G, i, j);
                if (PLSCA == TLSCA && PLSCA != 0) {
                    LSCAResult result = new LSCAResult();
                    result.nodes = new int[]{i, j};
                    result.LSCAs = new int[]{PLSCA, TLSCA};
                    result.status = true;
                    lscaRestults.add(result);
                } else {
                    LSCAResult result = new LSCAResult();
                    result.nodes = new int[]{i, j};
                    result.LSCAs = new int[]{PLSCA, TLSCA};
                    result.status = false;
                    lscaRestults.add(result);
                }
            }
        }

        System.out.println("Total Pairs examined: " + lscaRestults.size());
        for (LSCAResult p : lscaRestults) {
            System.out.print("Nodes: " + p.nodes[0] + " " + p.nodes[1] + " LSCA:");
            for (int n : p.LSCAs) {
                System.out.print(n + " ");
            }
            System.out.println(" Status: " + p.status);
        }
    }
}
