package com.pampang.nav.MapNav;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.pampang.nav.R;

import java.util.List;

public class MapActivityAF extends AppCompatActivity {

    private PathView pathView;
    private Graph graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainaf);

        pathView = findViewById(R.id.pathView);
        Button btnGo = findViewById(R.id.btnGo);
        Button btnClear = findViewById(R.id.btnClear); // ✅ new button

        setupGraph();
        testDijkstra();

        // 🟢 Start Dijkstra Path Animation
        btnGo.setOnClickListener(v -> {
            List<String> path = Dijkstra.findShortestPath(graph, "A", "F");
            pathView.showAnimatedPath(path);
        });

        // 🔴 Clear / Cancel Path
        btnClear.setOnClickListener(v -> {
            pathView.clearPath();
        });
    }

    private void testDijkstra() {
        List<String> path = Dijkstra.findShortestPath(graph, "A", "F");
        Log.d("DijkstraResult", "Shortest Path: " + path);
    }

    private void setupGraph() {
        graph = new Graph();
        graph.addEdge("A", "B", 7);
        graph.addEdge("A", "C", 10);
        graph.addEdge("B", "E", 4);
        graph.addEdge("B", "G", 5);
        graph.addEdge("C", "A", 10);
        graph.addEdge("C", "D", 12);
        graph.addEdge("D", "C", 12);
        graph.addEdge("D", "G", 2);
        graph.addEdge("D", "H", 3);
        graph.addEdge("E", "B", 4);
        graph.addEdge("E", "H", 10);
        graph.addEdge("F", "H", 0.5);
        graph.addEdge("F", "D", 4);
        graph.addEdge("G", "B", 5);
        graph.addEdge("G", "D", 2);
        graph.addEdge("H", "E", 10);
        graph.addEdge("H", "F", 0.5);
    }
}
