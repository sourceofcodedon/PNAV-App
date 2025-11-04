package com.pampang.nav.MapNav;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.pampang.nav.R;

import java.util.List;

public class FirstMeatMapView extends AppCompatActivity {

    private FirstMeatPathView firstMeatPathView;
    private Graph graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstmeat);

        firstMeatPathView = findViewById(R.id.firstMeatPath);
        Button btnGo = findViewById(R.id.btnGo);
        Button btnClear = findViewById(R.id.btnClear); // ✅ new button

        setupGraph();
        testDijkstra();

        // 🟢 Start Dijkstra Path Animation
        btnGo.setOnClickListener(v -> {
            List<String> path = Dijkstra.findShortestPath(graph, "A", "J");
            firstMeatPathView.showAnimatedPath(path);
        });

        // 🔴 Clear / Cancel Path
        btnClear.setOnClickListener(v -> {
            firstMeatPathView.clearPath();
        });
    }

    private void testDijkstra() {
        List<String> path = Dijkstra.findShortestPath(graph, "A", "J");
        Log.d("DijkstraResult", "Shortest Path: " + path);
    }

    private void setupGraph() {
        graph = new Graph();
        graph.addEdge("A", "B", 5);
        graph.addEdge("B", "C", 4);
        graph.addEdge("B", "E", 3);
        graph.addEdge("C", "D", 2);
        graph.addEdge("D", "E", 5);
        graph.addEdge("D", "G", 1);
        graph.addEdge("E", "F", 0.5);
        graph.addEdge("F", "G", 4);
        graph.addEdge("F", "I", 0.5);
        graph.addEdge("G", "H", 0.2);
        graph.addEdge("H", "K", 0.1);
        graph.addEdge("K", "J", 0.5);
        graph.addEdge("I", "J", 3);

    }
}
