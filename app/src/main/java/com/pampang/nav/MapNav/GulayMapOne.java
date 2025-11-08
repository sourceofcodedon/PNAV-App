package com.pampang.nav.MapNav;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pampang.nav.R;

import java.util.List;

public class GulayMapOne extends AppCompatActivity {

    private FirstGulayPath firstGulayPath;
    private Graph graph;
    private String startNode = null; // No default start node
    private final String DESTINATION_NODE = "E"; // Fixed destination

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstgulay);

        firstGulayPath = findViewById(R.id.firstgulay);
        Button btnGo = findViewById(R.id.btnGo);
        Button btnClear = findViewById(R.id.btnClear);

        setupGraph();

        firstGulayPath.setOnNodeClickListener(nodeLabel -> {
            startNode = nodeLabel;
            Toast.makeText(this, "Current location set to: " + nodeLabel, Toast.LENGTH_SHORT).show();
            Log.d("GulayMapOne", "Start node set to: " + startNode);
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
                firstGulayPath.showAnimatedPath(path);
            }
        });

        btnClear.setOnClickListener(v -> {
            firstGulayPath.clearPath();
            startNode = null; // Reset start node
            Toast.makeText(this, "Path cleared and location reset", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupGraph() {
        graph = new Graph();
        // Existing edges
        graph.addEdge("A", "B", 3);
        graph.addEdge("A", "C", 8);
        graph.addEdge("B", "D", 6);
        graph.addEdge("C", "D", 4);
        graph.addEdge("D", "E", 3);
        graph.addEdge("C", "F", 5);
        graph.addEdge("F", "H", 3);
        graph.addEdge("H", "E", 1);

        // Connect new nodes
        graph.addEdge("B", "I", 4);
        graph.addEdge("D", "J", 4);
        graph.addEdge("E", "K", 4);
        graph.addEdge("H", "L", 4);
        graph.addEdge("I", "J", 6);
        graph.addEdge("J", "K", 3);
        graph.addEdge("K", "L", 2);

        graph.addEdge("A", "M", 5);
        graph.addEdge("B", "N", 5);
        graph.addEdge("I", "O", 5);
        graph.addEdge("M", "N", 3);
        graph.addEdge("N", "O", 3);

        graph.addEdge("M", "P", 7);
        graph.addEdge("N", "Q", 7);
        graph.addEdge("O", "R", 7);
        graph.addEdge("P", "Q", 3);
        graph.addEdge("Q", "R", 3);

        graph.addEdge("C", "S", 2);
        graph.addEdge("D", "T", 2);
        graph.addEdge("J", "U", 2);
        graph.addEdge("S", "T", 3);
        graph.addEdge("T", "U", 3);

        graph.addEdge("P", "V", 2);
        graph.addEdge("Q", "W", 2);
        graph.addEdge("R", "X", 2);
        graph.addEdge("V", "W", 3);
        graph.addEdge("W", "X", 3);

        graph.addEdge("F", "Y", 4);
        graph.addEdge("K", "Z", 4);
        graph.addEdge("Y", "Z", 10);
    }
}
