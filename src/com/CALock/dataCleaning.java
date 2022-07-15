package com.CALock;

import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class dataCleaning {

    public HashMap<Integer, int[]> getConnectedComponents(int v, HashMap<Integer, int[]> edgeMap, HashMap<Integer, int[]> reachableMap) {
        int[] target = edgeMap.get(v);
        if (target != null && reachableMap.put(v, edgeMap.get(v)) == null) {
            for (int c : edgeMap.get(v)) {
                getConnectedComponents(c, edgeMap, reachableMap);
            }
        }
        return reachableMap;
    }

    public graphDefinition navigateConnected(HashMap<Integer, int[]> edgeMap) {
        int maxReachable = 0;
        int root = -2;
        HashMap<Integer, int[]> maxConnectedSubGraph = new HashMap<Integer, int[]>();
        for (int v : edgeMap.keySet()) {
            HashMap<Integer, int[]> connected = getConnectedComponents(v, edgeMap, new HashMap<Integer, int[]>());
            if (connected.size() > maxReachable) {
                maxReachable = connected.size();
                maxConnectedSubGraph = connected;
                root = v;
            }
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
