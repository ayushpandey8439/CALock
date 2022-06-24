package com.CALock;

import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;

import static com.CALock.pathHelper.shortensPrefix;

public class graph {
    HashMap<Integer, vertex> vertices;
    vertex root;
    private int nodecounter;

    public graph() {
        this.vertices = new HashMap<Integer, vertex>();
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

    public void addVertex(int data) throws Exception {
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

    public void removeEdge(int s, int t) throws Exception {
        vertex source = this.vertices.get(s);
        vertex target = this.vertices.get(t);

        source.children.remove(t);
        target.parents.remove(s);

        target.lowPath = target.parents.values().iterator().next().lowPath; // A really complex way of getting the first element in a Map.

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
        if (isInherited) {
            if (target.lowPath[0] == inheritedFrom) {
                target.lowPath = ArrayUtils.addAll(ArrayUtils.subarray(inheritedLow, 0, inheritedLow.length), target.lowPath);
            }
            if (target.highPath[0] == inheritedFrom) {
                target.highPath = ArrayUtils.addAll(ArrayUtils.subarray(inheritedHigh, 0, inheritedHigh.length), target.highPath);
            }
        } else {
            boolean updated = false;
            int[] targetLowNew = ArrayUtils.addAll(source.lowPath, target.Id);
            int[] targetHighNew = ArrayUtils.addAll(source.highPath, target.Id);
            if (shortensPrefix(targetHighNew, target.highPath, target.LSCAPathLength) || target.highPath.length == 1) {
                target.highPath = targetHighNew;
                updated = true;
            }
            if ((!updated && shortensPrefix(targetLowNew, target.lowPath, target.LSCAPathLength)) || target.lowPath.length == 1) {
                target.lowPath = targetLowNew;
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


        for (vertex child : target.children.values()) {
            if (isInherited) {
                updatePath(target, child, true, inheritedFrom, inheritedLow, inheritedHigh);
            } else {
                updatePath(target, child, true, target.Id, target.lowPath, target.highPath);
            }

        }
    }


}
