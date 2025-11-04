package com.pampang.nav.MapNav;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.pampang.nav.R;

import java.util.List;

public class MapActivty2 extends AppCompatActivity {

    private PathView2 pathView2;
    private Graph graph;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activty2);

        pathView2 = findViewById(R.id.pathView2);
        Button btnGo = findViewById(R.id.btnGo);
        Button btnClear = findViewById(R.id.btnClear); // ✅ new button

        setupGraph();
        testDijkstra();

        // 🟢 Start Dijkstra Path Animation
        btnGo.setOnClickListener(v -> {
            List<String> path = Dijkstra.findShortestPath(graph, "A", "E");
            pathView2.showAnimatedPath(path);
        });

        // 🔴 Clear / Cancel Path
        btnClear.setOnClickListener(v -> {
            pathView2.clearPath();
        });
    }

    private void testDijkstra() {
        List<String> path = Dijkstra.findShortestPath(graph, "A", "E");
        Log.d("DijkstraResult", "Shortest Path: " + path);
    }

    private void setupGraph() {
        graph = new Graph();
        graph.addEdge("A", "O", 6);
        graph.addEdge("A", "M", 5);
        graph.addEdge("B", "O", 6);
        graph.addEdge("B", "I", 3);
        graph.addEdge("B", "G", 5);
        graph.addEdge("C", "P", 0.5);
        graph.addEdge("C", "K", 1);
        graph.addEdge("D", "F", 6);
        graph.addEdge("D", "H", 6);
        graph.addEdge("D", "N", 2);
        graph.addEdge("E", "K", 1);
        graph.addEdge("E", "R", 0.5);
        graph.addEdge("F", "M", 6);
        graph.addEdge("F", "D", 6);
        graph.addEdge("G", "H", 2);
        graph.addEdge("G", "T", 1);
        graph.addEdge("G", "Q", 3);
        graph.addEdge("G", "B", 5);
        graph.addEdge("H", "S", 2);
        graph.addEdge("H", "G", 2);
        graph.addEdge("H", "D", 6);
        graph.addEdge("I", "L", 1);
        graph.addEdge("I", "B", 3);
        graph.addEdge("I", "Q", 5);
        graph.addEdge("J", "K", 1);
        graph.addEdge("J", "S", 2);
        graph.addEdge("K", "E", 1);
        graph.addEdge("K", "J", 1);
        graph.addEdge("K", "C", 1);
        graph.addEdge("L", "I", 1);
        graph.addEdge("L", "P", 6);
        graph.addEdge("M", "F", 6);
        graph.addEdge("M", "A", 5);
        graph.addEdge("M", "N", 6);
        graph.addEdge("N", "M", 6);
        graph.addEdge("N", "D", 2);
        graph.addEdge("N", "T", 5);
        graph.addEdge("N", "O", 5);
        graph.addEdge("O", "A", 6);
        graph.addEdge("O", "B", 6);
        graph.addEdge("O", "N", 5);
        graph.addEdge("P", "C", 0.5);
        graph.addEdge("P", "Q", 1);
        graph.addEdge("P", "L", 5);
        graph.addEdge("Q", "P", 1);
        graph.addEdge("Q", "R", 1);
        graph.addEdge("Q", "I", 5);
        graph.addEdge("Q", "G", 3);
        graph.addEdge("R", "Q", 1);
        graph.addEdge("R", "S", 1);
        graph.addEdge("R", "E", 0.5);
        graph.addEdge("S", "J", 2);
        graph.addEdge("S", "R", 1);
        graph.addEdge("S", "H", 2);
        graph.addEdge("T", "N", 5);
        graph.addEdge("T", "G", 1);
    }
}
