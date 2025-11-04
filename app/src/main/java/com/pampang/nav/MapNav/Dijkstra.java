package com.pampang.nav.MapNav;

import java.util.*;

public class Dijkstra {

    public static List<String> findShortestPath(Graph graph, String start, String end) {
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));

        for (String node : graph.getAdjList().keySet()) {
            distances.put(node, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        pq.add(new Node(start, 0.0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();

            if (current.name.equals(end)) break;

            List<Edge> edges = graph.getAdjList().get(current.name);
            if (edges == null) continue;

            for (Edge edge : edges) {
                double newDist = distances.get(current.name) + edge.distance;
                if (newDist < distances.get(edge.to)) {
                    distances.put(edge.to, newDist);
                    previous.put(edge.to, current.name);
                    pq.add(new Node(edge.to, newDist));
                }
            }
        }

        // reconstruct path
        List<String> path = new ArrayList<>();
        String at = end;
        while (at != null) {
            path.add(at);
            at = previous.get(at);
        }
        Collections.reverse(path);

        return path;
    }

    static class Node {
        String name;
        double distance;

        Node(String name, double distance) {
            this.name = name;
            this.distance = distance;
        }
    }
}
