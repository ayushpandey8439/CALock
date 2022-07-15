package com.CALock;

import java.util.HashMap;

public class graphDefinition {
    HashMap<Integer, int[]> edges;
    int root = -2;

    graphDefinition(HashMap<Integer, int[]> e, int r) {
        this.edges = e;
        this.root = r;
    }
}
