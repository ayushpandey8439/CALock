package com.CALock;

import org.apache.commons.lang3.ArrayUtils;

import static com.CALock.pathHelper.shortensPrefix;

public class preProcessor {
    public graph assignLabels(graph G) {
        G.root.highPath = new int[]{G.root.Id};
        G.root.lowPath = new int[]{G.root.Id};
        for (vertex c : G.root.children.values()) {
            dfExploreUpdate(c, G.root.lowPath, G.root.highPath);
        }
        return G;
    }


    private void dfExploreUpdate(vertex current, int[] IlowPath, int[] IhighPath) {
        if (current == null) {
            return;
        } else {
            int[] newLowPath = ArrayUtils.addAll(IlowPath, current.Id);
            int[] newHighPath = ArrayUtils.addAll(IhighPath, current.Id);
            boolean updated = false;
            if (shortensPrefix(newHighPath, current.highPath, current.LSCAPathLength) || current.highPath.length == 1) {
                current.highPath = newHighPath;
                updated = true;
            }
            if ((!updated && shortensPrefix(newLowPath, current.lowPath, current.LSCAPathLength)) || current.lowPath.length == 1) {
                current.lowPath = newLowPath;
            }
            int commonPathLength = 0;
            for (int i = 0; i < current.lowPath.length; i++) {
                if (current.lowPath[i] == current.highPath[i]) {
                    commonPathLength++;
                } else {
                    break;
                }
            }

            current.LSCAPathLength = commonPathLength;
            for (vertex child : current.children.values()) {
                dfExploreUpdate(child, current.lowPath, current.highPath);
            }

        }
    }

}
