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
    private String startNode = null;
    private final String DESTINATION_NODE = "N185"; // A node in the middle of the screen

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
        int cols = 22; // 1080 / 50
        int rows = 48; // 2400 / 50

        for (int i = 0; i < rows * cols; i++) {
            int row = i / cols;
            int col = i % cols;

            // Add edges to neighbors
            if (col < cols - 1) {
                graph.addEdge("N" + i, "N" + (i + 1), 1); // Right
            }
            if (row < rows - 1) {
                graph.addEdge("N" + i, "N" + (i + cols), 1); // Down
            }
             if (col < cols - 1 && row < rows - 1) {
                graph.addEdge("N" + i, "N" + (i + 1 + cols), 1.4); // Diagonal down-right
            }
            if (col > 0 && row < rows - 1) {
                graph.addEdge("N" + i, "N" + (i - 1 + cols), 1.4); // Diagonal down-left
            } 
        }
    }
}
