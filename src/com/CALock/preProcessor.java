package com.CALock;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;

import static com.CALock.pathHelper.shortensPrefix;

public class preProcessor {
    public graph assignLabels(@NotNull graph G) {
        G.root.highPath = new int[]{G.root.Id};
        G.root.lowPath = new int[]{G.root.Id};
        HashSet<Integer> visited = new HashSet<>();
        visited.add(G.root.Id);
        for (vertex c : G.root.children.values()) {
            dfExploreUpdate(c, G.root.lowPath, G.root.highPath, visited);
        }
        return G;
    }


    private void dfExploreUpdate(vertex current, int[] IlowPath, int[] IhighPath, HashSet<Integer> visited) {
        if (current == null || visited.contains(current.Id)) {
            return;
        } else {
            visited.add(current.Id);
            int[] newLowPath = ArrayUtils.addAll(IlowPath, current.Id);
            int[] newHighPath = ArrayUtils.addAll(IhighPath, current.Id);
            boolean updated = false;
            //If the paths were default or even after assigning the paths, the LSCA length remains zero.
            if (current.highPath.length == 1 || current.LSCAPathLength ==0) {
                current.highPath = newHighPath;
                updated = true;
            }
            if (current.lowPath.length == 1 || current.LSCAPathLength == 0) {
                current.lowPath = newLowPath;
                updated = true;
            }

            if (!updated) {
                if (shortensPrefix(newHighPath, current.lowPath, current.LSCAPathLength)) {
                    current.highPath = newHighPath;
                    updated = true;
                }
                if (!updated && shortensPrefix(newLowPath, current.highPath, current.LSCAPathLength)) {
                    current.lowPath = newLowPath;
                }
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
            if(updated){
                for (vertex child : current.children.values()) {
                    dfExploreUpdate(child, current.lowPath, current.highPath, visited);
                }
            }


        }
    }

}
