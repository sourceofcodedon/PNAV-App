package com.pampang.nav.MapNav;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pampang.nav.R;

import java.util.List;

public class MapActivityAF extends AppCompatActivity {

    private PathView pathView;
    private Graph graph;
    private String startNode = null;
    private final String DESTINATION_NODE = "F";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainaf);

        pathView = findViewById(R.id.pathView);
        Button btnGo = findViewById(R.id.btnGo);
        Button btnClear = findViewById(R.id.btnClear);

        setupGraph();

        pathView.setOnNodeClickListener(nodeLabel -> {
            startNode = nodeLabel;
            Toast.makeText(this, "Current location set to: " + nodeLabel, Toast.LENGTH_SHORT).show();
            Log.d("MapActivityAF", "Start node set to: " + startNode);
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
                pathView.showAnimatedPath(path);
            }
        });

        btnClear.setOnClickListener(v -> {
            pathView.clearPath();
            startNode = null;
            Toast.makeText(this, "Path cleared and location reset", Toast.LENGTH_SHORT).show();
        });
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
        
        // Edges for new nodes
        graph.addEdge("A", "I", 5);
        graph.addEdge("B", "J", 5);
        graph.addEdge("E", "K", 5);
        graph.addEdge("I", "L", 7);
        graph.addEdge("J", "M", 7);
        graph.addEdge("K", "N", 7);
        graph.addEdge("L", "M", 7);
        graph.addEdge("M", "N", 7);
    }
}
