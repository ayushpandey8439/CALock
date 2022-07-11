package com.CALock;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

class LSCAResult {
    public int[] nodes;
    public int[] LSCAs;
    public boolean status;
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

    public static HashMap<Integer, int[]> createEdgeMap(String filepath, String separator) {
        HashMap<Integer, int[]> edgeMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line = br.readLine();
            while (line != null) {
                if (line.charAt(0) != '#') {
                    String[] edge = line.split(separator);
                    int source = Integer.parseInt(edge[0]);
                    int target = Integer.parseInt(edge[1]);
                    int[] edgeSet = edgeMap.get(source);
                    if (edgeSet == null) {
                        edgeMap.put(source, new int[]{target});
                    } else {
                        edgeMap.put(source, ArrayUtils.addAll(edgeSet, target));
                    }
                }

                line = br.readLine();
            }
        } catch (Exception e) {
            System.out.println("Could not add line to the edgemap");
        }
        return edgeMap;
    }

    // Function to find the root vertex of a graph
    public void createRootVertex(graph G) {
        HashMap<Integer, vertex> rootCandidates = new HashMap<>();
        for (vertex v : G.vertices.values()) {
            if (v.parents.size() == 0 && !v.isSentinel()) {
                rootCandidates.put(v.Id, v);
            }
        } //If no roots are detected, then find a node that has paths leading to all the vertices except itself.
        if (rootCandidates.size() == 0) {
            int reachabilityCount = G.vertices.size();
            for (vertex v : G.vertices.values()) {
                Set<vertex> visitedInExploration = new HashSet<>(G.vertices.values());
                Set<vertex> unreachable = dfExplore(v, visitedInExploration);
                if (unreachable.size() == 0) {
                    rootCandidates = new HashMap<>();
                    rootCandidates.put(v.Id, v);
                    break;

                } else if (reachabilityCount > unreachable.size()) {
                    reachabilityCount = unreachable.size();
                    rootCandidates = new HashMap<>();
                    rootCandidates.put(v.Id, v);
                }
            }
        } // If there is a single node with no incoming edges, make it root.
        if (rootCandidates.size() == 1) {
            for (vertex p : G.sentinel.children.values()) {
                p.parents.remove(G.sentinel.Id);
            }
            G.vertices.remove(G.sentinel.Id);
            G.sentinel.initialiseVertexMetadata();
            G.root = G.vertices.values().iterator().next();
        } // If there is are multiple nodes with no incoming edges, create a sentinel.

        else {
            rootCandidates.size();
            G.vertices.put(G.sentinel.Id, G.sentinel);
            for (vertex r : rootCandidates.values()) {
                try {
                    G.createEdge(G.sentinel.Id, r.Id);
                } catch (Exception e) {
                    System.out.println("Create Edge failed for " + G.sentinel + " -> " + r.Id);
                }
            }
            G.root = G.sentinel;
        }
    }

    private Set<vertex> dfExplore(vertex v, Set<vertex> visitedInExploration) {
        if (!visitedInExploration.remove(v)) {
            return visitedInExploration;
        }
        for (vertex c : v.children.values()) {
            dfExplore(c, visitedInExploration);
        }
        return visitedInExploration;
    }

    public graph createDAG(HashMap<Integer, int[]> edgeMap, boolean assignLabelsDuringCreation) {
        graph G = new graph();
        for (int source : edgeMap.keySet()) {
            for (int target : edgeMap.get(source)) {
                if (G.vertices.get(source) == null) {
                    G.addVertex(source, source);
                }
                if (G.vertices.get(target) == null) {
                    G.addVertex(target, target);
                }
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
        createRootVertex(G);
        if (!assignLabelsDuringCreation) {
            preProcessor P = new preProcessor();
            G = P.assignLabels(G);
        }


        /*else if (roots.length == 0) {
            G.root = G.vertices.get(1);//TODO: Either get the user to manually define the root or have the root discovery algorithm run its course.
            HashMap<Integer, vertex> possibleRoots = discoverRoots(G);

        } else {
            G.sentinel.initialiseVertexMetadata();
            G.root.parents = new HashMap<>();
            G.vertices.remove(G.sentinel.Id);
            G.root = G.vertices.values().iterator().next();// TODO: I am making the first node the root of the tree which might be a bad idea.
            HashMap<Integer, vertex> possibleRoots = discoverRoots(G);
        }

         */
        return G;
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
        visited.put(source.Id, source);
        if (source.Id == target.Id) {
            DFSPaths = ArrayUtils.addAll(DFSPaths, currentPath);
        } else {
            for (vertex v : source.children.values()) {
                currentPath = ArrayUtils.addAll(currentPath, v.Id);
                if (visited.get(v.Id) == null) {
                    dfs(v, target, visited);
                }
                visited.remove(v.Id);
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
                System.out.print("Node: " + key1);
                System.out.print(" LowPath: ");
                for (int p : G.vertices.get(key1).lowPath) {
                    System.out.print(p + ",");
                }
                System.out.print(" HighPath: ");
                for (int p : G.vertices.get(key1).highPath) {
                    System.out.print(p + ",");
                }
                System.out.print("\nNode: " + key2);
                System.out.print(" LowPath: ");
                for (int p : G.vertices.get(key2).lowPath) {
                    System.out.print(p + ",");
                }
                System.out.print(" HighPath: ");
                for (int p : G.vertices.get(key2).highPath) {
                    System.out.print(p + ",");
                }
                int PLSCA = G.findPathLSCA(G, key1, key2);
                int TLSCA = findTraversalLSCA(G, key1, key2);
                LSCAResult result = new LSCAResult();
                result.nodes = new int[]{key1, key2};
                result.LSCAs = new int[]{PLSCA, TLSCA};
                result.status = PLSCA == TLSCA; // This was set to zero Imagine why?
                System.out.println("\n\tPLSCA:" + PLSCA + " TLSCA: " + TLSCA + " Status: " + result.status);
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
                    result.status = PLSCA == TLSCA; // This was set to zero Imagine why?
                    System.out.println("Nodes: " + a.Id + " " + b.Id + " LSCA:" + PLSCA + " TLSCA: " + TLSCA + " Status: " + result.status);
                    lscaResults.put(key, result);
                }

            }
        }

        System.out.println("Total Pairs examined: " + lscaResults.size());
        /*for (LSCAResult p : lscaResults.values()) {
            System.out.print("Nodes: " + p.nodes[0] + " " + p.nodes[1] + " LSCA:");
            for (int n : p.LSCAs) {
                System.out.print(n + " ");
            }
            System.out.println(" Status: " + p.status);
        }*/
    }
}
