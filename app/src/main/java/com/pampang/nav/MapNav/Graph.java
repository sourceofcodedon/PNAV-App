package com.pampang.nav.MapNav;

import java.util.*;

public class Graph {
    private final Map<String, List<Edge>> adjList = new HashMap<>();

    public void addEdge(String from, String to, double distance) {
        adjList.computeIfAbsent(from, k -> new ArrayList<>()).add(new Edge(to, distance));
        adjList.computeIfAbsent(to, k -> new ArrayList<>()).add(new Edge(from, distance)); // undirected
    }

    public Map<String, List<Edge>> getAdjList() {
        return adjList;
    }
}
