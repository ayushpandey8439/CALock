package com.CALock;

import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class dataCleaning {
    HashMap<Integer, int[]> reachableMap = new HashMap<>();

    public void getConnectedComponents(int v, HashMap<Integer, int[]> edgeMap) {
        if (edgeMap.containsKey(v) && !this.reachableMap.containsKey(v)) {
            int[] target = edgeMap.get(v);
            //System.out.print(v + ", ");
            this.reachableMap.put(v, target);
            for (int c : target) {
                this.getConnectedComponents(c, edgeMap);
            }
        }
    }

    public graphDefinition navigateConnected(HashMap<Integer, int[]> edgeMap) {
        int maxReachable = 0;
        int root = -2;
        HashMap<Integer, int[]> maxConnectedSubGraph = new HashMap<Integer, int[]>();
        for (int v : edgeMap.keySet()) {
            System.out.println("Exploring vertex "+v);
            getConnectedComponents(v, edgeMap);
            if (reachableMap.size() > maxReachable) {
                maxReachable = reachableMap.size();
                maxConnectedSubGraph = reachableMap;
                root = v;
            }
            this.reachableMap = new HashMap<>();
        }

        return new graphDefinition(maxConnectedSubGraph, root);
    }

    public void writeEdgeMapToFile(String filepath, graphDefinition g) throws IOException {
        boolean result = Files.deleteIfExists(Path.of(filepath));
        BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, true));
        writer.append("R,").append(String.valueOf(g.root)).append("\n");
        for (int s : g.edges.keySet()) {
            for (int t : g.edges.get(s)) {
                writer.append(String.valueOf(s)).append(",").append(String.valueOf(t)).append("\n");
            }
        }
        writer.close();
    }

    public HashMap<Integer, int[]> loadfile(String filepath, String separator) {
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

}
