package com.CALock;

import java.util.HashMap;

public class vertex {
    int Id;
    HashMap<Integer, vertex> children;
    HashMap<Integer, vertex> parents;
    int[] lowPath;
    int[] highPath;
    int LSCAPathLength;
    int data;

    public vertex(int id, int data) {
        this.Id = id;
        this.children = new HashMap<Integer, vertex>();
        this.parents = new HashMap<Integer, vertex>();
        this.lowPath = new int[]{id};
        this.highPath = new int[]{id};
        this.LSCAPathLength = 0;
        this.data = data;
    }
}
