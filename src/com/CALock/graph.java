package com.CALock;

import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;

import static com.CALock.pathHelper.shortensPrefix;

public class graph {
    HashMap<Integer, vertex> vertices;
    vertex root;
    vertex sentinel;
    private int nodecounter;

    public graph() {
        this.vertices = new HashMap<>();
        this.sentinel = new vertex(0, 0);
        this.nodecounter = 1;

    }

    public int findPathLSCA(graph G, int... V) {
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
        return lowestNode;
    }

    public void addVertex(int data) {
        this.vertices.put(nodecounter, new vertex(nodecounter, data));
        this.nodecounter++;

    }

    public void createEdge(int s, int t) throws Exception {
        vertex source = this.vertices.get(s);
        vertex target = this.vertices.get(t);

        if (source != null && target != null) {
            source.children.put(t, target);
            target.parents.put(s, source);
            updatePath(source, target, false, source.Id, new int[]{}, new int[]{});
        } else {
            throw new Exception("Source or target missing from te graph");
        }
    }

    public void createEdgeWithoutUpdatingPaths(int s, int t) throws Exception {
        vertex source = this.vertices.get(s);
        vertex target = this.vertices.get(t);

        if (source != null && target != null) {
            source.children.put(t, target);
            target.parents.put(s, source);
        } else {
            throw new Exception("Source or target missing from te graph");
        }
    }

    public void removeEdge(int s, int t) {
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

        for (vertex c : target.children.values()) {
            updatePath(target, c, false, target.Id, target.lowPath, target.highPath);
        }
    }

    private void updatePath(vertex source, vertex target, boolean isInherited, int inheritedFrom, int[] inheritedLow, int[] inheritedHigh) {
        boolean updated = false;
        if (isInherited) {
            if (target.lowPath[0] == inheritedFrom) {
                target.lowPath = ArrayUtils.addAll(ArrayUtils.subarray(inheritedLow, 0, inheritedLow.length), target.lowPath);
            }
            if (target.highPath[0] == inheritedFrom) {
                target.highPath = ArrayUtils.addAll(ArrayUtils.subarray(inheritedHigh, 0, inheritedHigh.length), target.highPath);
            }
            updated = true;
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
                if (shortensPrefix(targetHighNew, target.lowPath, target.LSCAPathLength)) {
                    target.highPath = targetHighNew;
                    updated = true;
                } else if (shortensPrefix(targetLowNew, target.highPath, target.LSCAPathLength)) {
                    target.lowPath = targetLowNew;
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
                    updatePath(target, child, true, inheritedFrom, inheritedLow, inheritedHigh);
                } else {
                    updatePath(target, child, true, target.Id, target.lowPath, target.highPath);
                }

            }
        }

    }


}
