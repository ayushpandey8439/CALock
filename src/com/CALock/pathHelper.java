package com.CALock;

import java.util.Arrays;

public class pathHelper {
    public static boolean shortensPrefix(int[] path1, int[] path2, int LSCALength) {
        String P1 = Arrays.toString(path1);
        String P2 = Arrays.toString(path2);
        if (P1.startsWith(P2) || P2.startsWith(P1)) {
            return true;
        }
        int commonPathLength = 0;
        int[] shorterPath = path1;
        if (!(path1.length <= path2.length)) {
            shorterPath = path2;
        }

        for (int i = 0; i < shorterPath.length; i++) {
            if (path1[i] == path2[i]) {
                commonPathLength++;
                if (commonPathLength >= LSCALength) {
                    break;
                }
            } else {
                break;
            }
        }
        return commonPathLength < LSCALength; // This doesnt work with equality. Why?
    }
}
