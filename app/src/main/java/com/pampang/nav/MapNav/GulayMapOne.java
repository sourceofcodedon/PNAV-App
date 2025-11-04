package com.pampang.nav.MapNav;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.pampang.nav.R;

import java.util.List;

public class GulayMapOne extends AppCompatActivity {

    private FirstGulayPath firstGulayPath;
    private Graph graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstgulay);

        firstGulayPath = findViewById(R.id.firstgulay);
        Button btnGo = findViewById(R.id.btnGo);
        Button btnClear = findViewById(R.id.btnClear); // ✅ new button

        setupGraph();
        testDijkstra();

        // 🟢 Start Dijkstra Path Animation
        btnGo.setOnClickListener(v -> {
            List<String> path = Dijkstra.findShortestPath(graph, "A", "E");
            firstGulayPath.showAnimatedPath(path);
        });

        // 🔴 Clear / Cancel Path
        btnClear.setOnClickListener(v -> {
            firstGulayPath.clearPath();
        });
    }

    private void testDijkstra() {
        List<String> path = Dijkstra.findShortestPath(graph, "A", "E");
        Log.d("DijkstraResult", "Shortest Path: " + path);
    }

    private void setupGraph() {
        graph = new Graph();
        graph.addEdge("A", "B", 3);
        graph.addEdge("A", "C", 8);
        graph.addEdge("B", "D", 6);
        graph.addEdge("C", "D", 4);
        graph.addEdge("D", "E", 3);
        graph.addEdge("C", "F", 5);
        graph.addEdge("F", "H", 3);
        graph.addEdge("H", "E", 1);


    }
}
