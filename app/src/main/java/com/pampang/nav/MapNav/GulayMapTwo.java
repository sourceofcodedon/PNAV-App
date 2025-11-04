package com.pampang.nav.MapNav;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.pampang.nav.R;

import java.util.List;

public class GulayMapTwo extends AppCompatActivity {

    private SecondGulayPath secondGulayPath;
    private Graph graph;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondgulay);

        secondGulayPath = findViewById(R.id.secondgulay);
        Button btnGo = findViewById(R.id.btnGo);
        Button btnClear = findViewById(R.id.btnClear); // ✅ new button

        setupGraph();
        testDijkstra();

        // 🟢 Start Dijkstra Path Animation
        btnGo.setOnClickListener(v -> {
            List<String> path = Dijkstra.findShortestPath(graph, "A", "M");
            secondGulayPath.showAnimatedPath(path);
        });

        // 🔴 Clear / Cancel Path
        btnClear.setOnClickListener(v -> {
            secondGulayPath.clearPath();
        });
    }

    private void testDijkstra() {
        List<String> path = Dijkstra.findShortestPath(graph, "A", "M");
        Log.d("DijkstraResult", "Shortest Path: " + path);
    }

    private void setupGraph() {
        graph = new Graph();
        graph.addEdge("A", "B", 2);
        graph.addEdge("A", "H", 6);
        graph.addEdge("B", "C", 1);
        graph.addEdge("B", "G", 4);
        graph.addEdge("C", "D", 1);
        graph.addEdge("C", "F", 4);
        graph.addEdge("D", "E", 4);
        graph.addEdge("E", "M", 0.5);
        graph.addEdge("E", "N", 0.5);
        graph.addEdge("N", "F", 0.5);
        graph.addEdge("F", "G", 1);
        graph.addEdge("G", "H", 0.5);

    }
}
