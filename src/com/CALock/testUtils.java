package com.CALock;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

class LSCAResult {
    public int[] nodes;
    public int[] LSCAs;
    public boolean pass;
}

public class testUtils {

    private int[][] DFSPaths;
    private int[] currentPath;


    public static HashMap<Integer, int[]> createEdgeMap() {
        HashMap<Integer, int[]> edgeMap = new HashMap<>();
        edgeMap.put(1, new int[]{2});
        edgeMap.put(2, new int[]{3});
        edgeMap.put(3, new int[]{4});
        edgeMap.put(4, new int[]{5});
        edgeMap.put(5, new int[]{1});
        return edgeMap;
    }

    public graphDefinition createEdgeMap(String filepath, String separator) {
        HashMap<Integer, int[]> edgeMap = new HashMap<>();
        int root = -2;
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line = br.readLine();
            while (line != null) {
                if (line.charAt(0) != 'R') {
                    String[] edge = line.split(separator);
                    int source = Integer.parseInt(edge[0]);
                    int target = Integer.parseInt(edge[1]);
                    int[] edgeSet = edgeMap.get(source);
                    if (edgeSet == null) {
                        edgeMap.put(source, new int[]{target});
                    } else {
                        edgeMap.put(source, ArrayUtils.addAll(edgeSet, target));
                    }
                } else if (line.charAt(0) == 'R') {
                    root = Integer.parseInt(line.split(separator)[1]);
                }
                line = br.readLine();
            }


        } catch (Exception e) {
            System.out.println("Could not add line to the edgemap");
        }

        return new graphDefinition(edgeMap, root);
    }


    private int findTraversalLSCA(graph G, int... V) {
        this.currentPath = new int[]{G.root.Id};
        this.DFSPaths = new int[][]{};
        for (int v : V) {
            dfs(G.root, G.vertices.get(v), new HashMap<>());
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
            for (int[] dfsPath : DFSPaths) {
                if (dfsPath[i] != shortestPath[i]) {
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
        //System.out.print(source.Id + ", ");
        visited.put(source.Id, source);
        if (source.Id == target.Id) {
            DFSPaths = ArrayUtils.addAll(DFSPaths, currentPath);
        } else {
            for (vertex v : source.children.values()) {
                currentPath = ArrayUtils.addAll(currentPath, v.Id);
                if (visited.get(v.Id) == null) {
                    dfs(v, target, visited);
                }
                //visited.remove(v.Id);
                currentPath = ArrayUtils.remove(currentPath, currentPath.length - 1);
            }
        }
    }

    public void testRandomPairLSCA(graph G, int numPairs) {
        Map<Pair<Integer, Integer>, LSCAResult> lscaResults = new HashMap<>();
        List<Integer> keys = new ArrayList<Integer>(G.vertices.keySet());

        Random rand = new Random();
        for (int i = 0; i < numPairs; i++) {
            int key1 = keys.get(rand.nextInt(keys.size()));
            int key2 = keys.get(rand.nextInt(keys.size()));
            Pair<Integer, Integer> key = Pair.of(key1, key2);
            if (lscaResults.get(key) == null) {
                System.out.print("Node: " + key1+ " Node: "+ key2);
                int PLSCA = G.findPathLSCA(G, key1, key2);
                int TLSCA = findTraversalLSCA(G, key1, key2);
                LSCAResult result = new LSCAResult();
                result.nodes = new int[]{key1, key2};
                result.LSCAs = new int[]{PLSCA, TLSCA};
                result.pass = PLSCA == TLSCA; // This was set to zero Imagine why?
                System.out.println("\n\tPLSCA:" + PLSCA + " TLSCA: " + TLSCA + " Status: " + result.pass);
                lscaResults.put(key, result);
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
                    result.pass = PLSCA == TLSCA; // This was set to zero Imagine why?
                    if(!result.pass){
                        System.out.println("Nodes: " + a.Id + " " + b.Id + " LSCA:" + PLSCA + " TLSCA: " + TLSCA + " Status: " + result.pass);
                    }
                    lscaResults.put(key, result);
                }
            }
        }
        System.out.println("Total Pairs examined: " + lscaResults.size());
    }
}
