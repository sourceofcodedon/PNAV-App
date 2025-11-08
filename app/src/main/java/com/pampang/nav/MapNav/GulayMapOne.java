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
    private final String DESTINATION_NODE = "n5"; // Fixed destination

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

        //First column
        graph.addEdge("n6", "n24", 2);
        graph.addEdge("n6", "n27", 1);
        graph.addEdge("n24", "n3", 1);
        graph.addEdge("n3", "n36", 1);
        graph.addEdge("n3", "n18", 2);
        graph.addEdge("n18", "n1", 2);
        graph.addEdge("n1", "n42", 1);
        graph.addEdge("n1", "n45", 1.5);
        graph.addEdge("n45", "n12", 1);
        graph.addEdge("n12", "n52", 1);
        graph.addEdge("n21", "n12", 1);
        graph.addEdge("n21", "n15", 1);
        graph.addEdge("n21", "n56", 1);
        graph.addEdge("n15", "n64", 2);

        //second column
        graph.addEdge("n27", "n28", 2);
        graph.addEdge("n21", "n12", 2);




    }
}
