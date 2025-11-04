package com.pampang.nav.MapNav;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.pampang.nav.R;

import java.util.List;

public class SecondMeatMapView extends AppCompatActivity {

    private SecondMeatPathView secondMeatPathView;
    private Graph graph;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondmeat);

        secondMeatPathView = findViewById(R.id.secondmeatpath);
        Button btnGo = findViewById(R.id.btnGo);
        Button btnClear = findViewById(R.id.btnClear); // ✅ new button

        setupGraph();
        testDijkstra();

        // 🟢 Start Dijkstra Path Animation
        btnGo.setOnClickListener(v -> {
            List<String> path = Dijkstra.findShortestPath(graph, "A", "X");
            secondMeatPathView.showAnimatedPath(path);
        });

        // 🔴 Clear / Cancel Path
        btnClear.setOnClickListener(v -> {
            secondMeatPathView.clearPath();
        });
    }

    private void testDijkstra() {
        List<String> path = Dijkstra.findShortestPath(graph, "A", "X");
        Log.d("DijkstraResult", "Shortest Path: " + path);
    }

    private void setupGraph() {
        graph = new Graph();
        graph.addEdge("A", "B", 3);
        graph.addEdge("A", "F", 2);
        graph.addEdge("B", "C", 3);
        graph.addEdge("B", "E", 4);
        graph.addEdge("C", "D", 4);
        graph.addEdge("D", "E", 3);
        graph.addEdge("D", "I", 0.5);
        graph.addEdge("E", "H", 0.5);
        graph.addEdge("E", "F", 3);
        graph.addEdge("F", "G", 0.5);
        graph.addEdge("G", "H", 3);
        graph.addEdge("G", "L", 0.5);
        graph.addEdge("H", "I", 3);
        graph.addEdge("H", "K", 0.5);
        graph.addEdge("I", "J", 0.5);
        graph.addEdge("J", "O", 0.5);
        graph.addEdge("J", "K", 3);
        graph.addEdge("K", "N", 0.5);
        graph.addEdge("K", "L", 3);
        graph.addEdge("L", "M", 0.5);
        graph.addEdge("M", "N", 3);
        graph.addEdge("M", "R", 0.5);
        graph.addEdge("N", "Q", 0.5);
        graph.addEdge("N", "O", 3);
        graph.addEdge("O", "P", 0.5);
        graph.addEdge("P", "Q", 3);
        graph.addEdge("Q", "T", 0.5);
        graph.addEdge("Q", "R", 3);
        graph.addEdge("R", "U", 0.5);
        graph.addEdge("U", "V", 0.5);
        graph.addEdge("U", "T", 2);
        graph.addEdge("T", "W", 0.5);
        graph.addEdge("T", "S", 3);
        graph.addEdge("S", "Y", 0.5);
        graph.addEdge("P", "S", 0.5);
        graph.addEdge("Y", "Z", 0.5);
        graph.addEdge("Z", "X", 0.4);
        graph.addEdge("X", "W", 0.5);
        graph.addEdge("W", "V", 2);
    }
}
