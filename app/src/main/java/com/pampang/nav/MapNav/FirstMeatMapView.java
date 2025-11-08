package com.pampang.nav.MapNav;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pampang.nav.R;

import java.util.List;

public class FirstMeatMapView extends AppCompatActivity {

    private FirstMeatPathView firstMeatPathView;
    private Graph graph;
    private String startNode = null;
    private final String DESTINATION_NODE = "S";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstmeat);

        firstMeatPathView = findViewById(R.id.firstMeatPath);
        Button btnGo = findViewById(R.id.btnGo);
        Button btnClear = findViewById(R.id.btnClear);

        setupGraph();

        firstMeatPathView.setOnNodeClickListener(nodeLabel -> {
            startNode = nodeLabel;
            Toast.makeText(this, "Current location set to: " + nodeLabel, Toast.LENGTH_SHORT).show();
            Log.d("FirstMeatMapView", "Start node set to: " + startNode);
        });

        btnGo.setOnClickListener(v -> {
            if (startNode == null) {
                Toast.makeText(this, "Please select a starting location by tapping on the map", Toast.LENGTH_LONG).show();
                return;
            }
            List<String> path = Dijkstra.findShortestPath(graph, startNode, DESTINATION_NODE);
            if (path == null || path.isEmpty()) {
                Toast.makeText(this, "No path found from " + startNode + " to " + DESTINATION_NODE, Toast.LENGTH_SHORT).show();
            } else {
                firstMeatPathView.showAnimatedPath(path);
            }
        });

        btnClear.setOnClickListener(v -> {
            firstMeatPathView.clearPath();
            startNode = null;
            Toast.makeText(this, "Path cleared and location reset", Toast.LENGTH_SHORT).show();
        });
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

        // Edges for new nodes
        graph.addEdge("I", "L", 4);
        graph.addEdge("J", "M", 2);
        graph.addEdge("L", "M", 3);
        graph.addEdge("A", "N", 6);
        graph.addEdge("B", "O", 2);
        graph.addEdge("C", "P", 1);
        graph.addEdge("D", "Q", 3);
        graph.addEdge("G", "R", 2);
        graph.addEdge("M", "S", 5);
        graph.addEdge("N", "O", 2);
        graph.addEdge("O", "P", 1);
        graph.addEdge("P", "Q", 2);
        graph.addEdge("Q", "R", 1);
        graph.addEdge("R", "S", 3);
    }
}
