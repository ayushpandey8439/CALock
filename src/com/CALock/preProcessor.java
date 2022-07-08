package com.CALock;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static com.CALock.pathHelper.shortensPrefix;

public class preProcessor {
    public graph assignLabels(@NotNull graph G) {
        G.root.highPath = new int[]{G.root.Id};
        G.root.lowPath = new int[]{G.root.Id};
        HashMap<Integer, vertex> visited = new HashMap<Integer, vertex>();
        visited.put(G.root.Id, G.root);
        for (vertex c : G.root.children.values()) {
            dfExploreUpdate(c, G.root.lowPath, G.root.highPath, visited);
        }
        return G;
    }


    private void dfExploreUpdate(vertex current, int[] IlowPath, int[] IhighPath, HashMap<Integer, vertex> visited) {
        if (current == null || visited.get(current.Id) != null) {
            return;
        } else {
            visited.put(current.Id, current);
            int[] newLowPath = ArrayUtils.addAll(IlowPath, current.Id);
            int[] newHighPath = ArrayUtils.addAll(IhighPath, current.Id);
            boolean updated = false;
            if (shortensPrefix(newHighPath, current.highPath, current.LSCAPathLength) || current.highPath.length == 1) {
                current.highPath = newHighPath;
                updated = true;
            }
            if ((shortensPrefix(newLowPath, current.lowPath, current.LSCAPathLength)) || current.lowPath.length == 1) {
                current.lowPath = newLowPath;
            }
            int commonPathLength = 0;
            int checkLength = Math.min(current.lowPath.length, current.highPath.length);
            for (int i = 0; i < checkLength; i++) {
                if (current.lowPath[i] == current.highPath[i]) {
                    commonPathLength++;
                } else {
                    break;
                }
            }

            current.LSCAPathLength = commonPathLength;
            for (vertex child : current.children.values()) {
                dfExploreUpdate(child, current.lowPath, current.highPath, visited);
            }

        }
    }

}
