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
            List<String> path = Dijkstra.findShortestPath(graph, "A", "F");
            pathView2.showAnimatedPath(path);
        });

        // 🔴 Clear / Cancel Path
        btnClear.setOnClickListener(v -> {
            pathView2.clearPath();
        });
    }

    private void testDijkstra() {
        List<String> path = Dijkstra.findShortestPath(graph, "A", "F");
        Log.d("DijkstraResult", "Shortest Path: " + path);
    }

    private void setupGraph() {
        graph = new Graph();
        graph.addEdge("A", "B", 3);
        graph.addEdge("A", "C", 7);
        graph.addEdge("B", "D", 12);
        graph.addEdge("B", "H", 7);
        graph.addEdge("C", "H", 3);
        graph.addEdge("D", "G", 6);
        graph.addEdge("E", "F", 1);
        graph.addEdge("E", "G", 4);



    }
}
