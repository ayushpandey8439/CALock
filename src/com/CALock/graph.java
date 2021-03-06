package com.CALock;

import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.CALock.pathHelper.shortensPrefix;

public class graph {
    HashMap<Integer, vertex> vertices;
    vertex root;
    vertex sentinel;

    graph() {
        this.vertices = new HashMap<>();
        this.sentinel = new vertex(-1, -1);
        this.root = null;
    }

    graph(graphDefinition G) {
        this.vertices = new HashMap<>();
        this.sentinel = new vertex(-1, -1);

        for (int source : G.edges.keySet()) {
            for (int target : G.edges.get(source)) {
                if (this.vertices.get(source) == null) {
                    this.addVertex(source, source);
                }
                if (this.vertices.get(target) == null) {
                    this.addVertex(target, target);
                }
                try {
                    this.createEdgeWithoutUpdatingPaths(source, target);
                } catch (Exception e) {
                    System.out.println("Create Edge failed for " + source + " -> " + target);
                }
            }
        }

        //Assign labels for a new hierarchy with DF Exploration.
        try {
            if (G.root != -2) {
                this.root = this.vertices.get(G.root);
            } else {
                this.findRootVertex();
            }
            preProcessor P = new preProcessor();
            P.assignLabels(this);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void findRootVertex() throws Exception {
        HashMap<Integer, vertex> rootCandidates = new HashMap<>();
        for (vertex v : this.vertices.values()) {
            if (v.parents.size() == 0 && !v.isSentinel()) {
                rootCandidates.put(v.Id, v);
            }
        }
        // If there is a single node with no incoming edges, make it root.
        if (rootCandidates.size() == 1) {
            for (vertex p : this.sentinel.children.values()) {
                p.parents.remove(this.sentinel.Id);
            }
            this.vertices.remove(this.sentinel.Id);
            this.sentinel.initialiseVertexMetadata();
            this.root = this.vertices.values().iterator().next();
        }// If there is are multiple nodes with no incoming edges, create a sentinel.
        else if (rootCandidates.size() > 1) {
            this.vertices.put(this.sentinel.Id, this.sentinel);
            for (vertex r : rootCandidates.values()) {
                try {
                    this.createEdge(this.sentinel.Id, r.Id);
                } catch (Exception e) {
                    System.out.println("Create Edge failed when assigning a new root " + this.sentinel + " -> " + r.Id);
                }
            }
            this.root = this.sentinel;
        }// If there is no definite root, we need to find the node that gives us complete reachability
        else {
            boolean foundRoot = false;
            for (int v : this.vertices.keySet()) {
                HashSet<Integer> connectedSubcomponent = this.dfExplore(v, new HashSet<>());
                if (connectedSubcomponent.size() == this.vertices.size()) {
                    this.root = this.vertices.get(v);
                    foundRoot = true;
                    break;
                }
            }
            if (!foundRoot) {
                throw new RuntimeException("No node in the graph ensures reachability. Please reconfigure the graph");
            }

        }
    }

    private HashSet<Integer> dfExplore(int v, HashSet<Integer> reachable) {
        if (this.vertices.containsKey(v) && !reachable.contains(v)) {
            Set<Integer> targets = this.vertices.get(v).children.keySet();
            reachable.add(v);
            for (int c : targets) {
                dfExplore(c, reachable);
            }
        }
        return reachable;
    }

    public int findPathLSCA(int... V) {
        int[][] studyPaths = new int[][]{};
        for (int v : V) {
            vertex vert = this.vertices.get(v);
            studyPaths = ArrayUtils.addAll(studyPaths, vert.lowPath, vert.highPath);
        }

        int[] shortestPath = studyPaths[0];
        for (int[] p : studyPaths) {
            if (p.length < shortestPath.length) {
                shortestPath = p;
            }
        }

        int lowestNode = -2;
        for (int i = 0; i < shortestPath.length; i++) {
            boolean isCommon = true;
            for (int[] studyPath : studyPaths) {
                if (studyPath[i] != shortestPath[i]) {
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
        if (lowestNode == -2) {
            throw new RuntimeException("LSCA does not exist. Please ensure the graph is rooted.");
        }
        return lowestNode;
    }

    public void addVertex(int id, int data) {
        if (!this.vertices.containsKey(id)) {
            this.vertices.put(id, new vertex(id, data));
        }
    }

    public void createEdge(int s, int t) throws Exception {
        vertex source = this.vertices.get(s);
        vertex target = this.vertices.get(t);

        if (source != null && target != null) {
            source.children.put(t, target);
            target.parents.put(s, source);
            this.updatePath(source, target, false, source.Id, new int[]{}, new int[]{}, new HashSet<>());
        } else {
            throw new Exception("Source or target missing from the graph!");
        }
    }

    public void createEdgeWithoutUpdatingPaths(int s, int t) throws Exception {
        vertex source = this.vertices.get(s);
        vertex target = this.vertices.get(t);

        if (source != null && target != null) {
            source.children.put(t, target);
            target.parents.put(s, source);
        } else {
            throw new Exception("Source or target missing from the graph");
        }
    }

    public void removeEdge(int s, int t) throws Exception {
        //TODO: When removing edges, we need to ensure that a single root remains.
        vertex source = this.vertices.get(s);
        vertex target = this.vertices.get(t);

        source.children.remove(t);
        target.parents.remove(s);
        // Remove both labels from the target and reassign two from the parents
        vertex firstParent = target.parents.values().iterator().next();// A really complex way of getting the first element in a Map.
        target.lowPath = ArrayUtils.addAll(firstParent.lowPath, target.Id);
        target.highPath = ArrayUtils.addAll(firstParent.highPath, target.Id);

        int shortestPrefix = target.lowPath.length;

        for (vertex p : target.parents.values()) {
            int[] comparisionPath = ArrayUtils.addAll(p.highPath, target.Id);
            if (shortensPrefix(comparisionPath, target.lowPath, shortestPrefix)) {
                target.highPath = comparisionPath;
            }
        }

        int commonPathLength = 0;
        for (int i = 0; i < target.lowPath.length; i++) {
            if (target.lowPath[i] == target.highPath[i]) {
                commonPathLength++;
            } else {
                break;
            }
        }

        target.LSCAPathLength = commonPathLength;
        this.findRootVertex();
        preProcessor P = new preProcessor();
        P.assignLabels(this);

        for (vertex c : target.children.values()) {
            updatePath(target, c, false, target.Id, target.lowPath, target.highPath, new HashSet<>());
        }
    }

    private void updatePath(vertex source, vertex target, boolean isInherited, int inheritedFrom, int[] inheritedLow, int[] inheritedHigh, HashSet<Integer> visited) {
        if (visited.contains(source.Id)) {
            return;
        } else {
            visited.add(source.Id);
            boolean updated = false;
            if (isInherited) {
                if (target.lowPath[0] == inheritedFrom) {
                    target.lowPath = ArrayUtils.addAll(ArrayUtils.subarray(inheritedLow, 0, inheritedLow.length - 1), target.lowPath);
                    updated = true;
                }
                if (target.highPath[0] == inheritedFrom) {
                    target.highPath = ArrayUtils.addAll(ArrayUtils.subarray(inheritedHigh, 0, inheritedHigh.length - 1), target.highPath);
                    updated = true;
                }
            } else {
                int[] targetLowNew = ArrayUtils.addAll(source.lowPath, target.Id);
                int[] targetHighNew = ArrayUtils.addAll(source.highPath, target.Id);
                if (target.highPath.length == 1) {
                    target.highPath = targetHighNew;
                    updated = true;
                }
                if (target.lowPath.length == 1) {
                    target.lowPath = targetLowNew;
                    updated = true;
                }
                if (!updated) {
                    if (shortensPrefix(targetLowNew, target.highPath, target.LSCAPathLength)) {
                        target.lowPath = targetLowNew;
                        updated = true;
                    }
                    if (!updated && shortensPrefix(targetHighNew, target.lowPath, target.LSCAPathLength)) {
                        target.highPath = targetHighNew;
                        updated = true;
                    }
                }
            }
            int commonPathLength = 0;
            for (int i = 0; i < target.lowPath.length; i++) {
                if (target.lowPath[i] == target.highPath[i]) {
                    commonPathLength++;
                } else {
                    break;
                }
            }


            target.LSCAPathLength = commonPathLength;

            if (updated) {
                for (vertex child : target.children.values()) {
                    if (isInherited) {
                        updatePath(target, child, true, inheritedFrom, inheritedLow, inheritedHigh, visited);
                    } else {
                        updatePath(target, child, true, target.Id, target.lowPath, target.highPath, visited);
                    }
                }
            }


        }
    }
}
