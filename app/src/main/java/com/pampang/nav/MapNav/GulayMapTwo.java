package com.pampang.nav.MapNav;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pampang.nav.R;

import java.util.List;

public class GulayMapTwo extends AppCompatActivity {

    private SecondGulayPath secondGulayPath;
    private Graph graph;
    private String startNode = null; 
    private final String DESTINATION_NODE = "R";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondgulay);

        secondGulayPath = findViewById(R.id.secondgulay);
        Button btnGo = findViewById(R.id.btnGo);
        Button btnClear = findViewById(R.id.btnClear);

        setupGraph();

        secondGulayPath.setOnNodeClickListener(nodeLabel -> {
            startNode = nodeLabel;
            Toast.makeText(this, "Current location set to: " + nodeLabel, Toast.LENGTH_SHORT).show();
            Log.d("GulayMapTwo", "Start node set to: " + startNode);
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
                secondGulayPath.showAnimatedPath(path);
            }
        });

        btnClear.setOnClickListener(v -> {
            secondGulayPath.clearPath();
            startNode = null; 
            Toast.makeText(this, "Path cleared and location reset", Toast.LENGTH_SHORT).show();
        });
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

        // Additional node connections
        graph.addEdge("H", "I", 3);
        graph.addEdge("I", "J", 5);
        graph.addEdge("J", "K", 2);
        graph.addEdge("K", "L", 2);
        graph.addEdge("L", "R", 8);
        graph.addEdge("M", "O", 4);
        graph.addEdge("N", "P", 6);
        graph.addEdge("O", "P", 1);
        graph.addEdge("P", "Q", 2);
        graph.addEdge("Q", "R", 1);
    }
}
