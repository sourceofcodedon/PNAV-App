package com.pampang.nav.MapNav;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.pampang.nav.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecondMeatMapView extends AppCompatActivity {

    private SecondMeatPathView secondMeatPathView;
    private Graph graph;
    private String startNode = null;
    private final String DESTINATION_NODE = "n88"; // Fixed destination
    private List<String> clickableNodes;
    private ImageView userMarker;
    private TextView instructionText;
    private MaterialCardView instructionBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondmeat);

        secondMeatPathView = findViewById(R.id.secondmeatpath);
        Button btnGo = findViewById(R.id.btnGo);
        Button btnClear = findViewById(R.id.btnClear);
        userMarker = findViewById(R.id.user_marker);
        instructionText = findViewById(R.id.instruction_text);
        instructionBanner = findViewById(R.id.instruction_banner);

        setupGraph();
        clickableNodes = new ArrayList<>(graph.getAdjList().keySet());
        secondMeatPathView.setClickableNodes(clickableNodes);

        secondMeatPathView.setOnNodeClickListener(nodeLabel -> {
            startNode = nodeLabel;
            secondMeatPathView.setSelectedNode(nodeLabel);
            String displayName = NodeDisplayNames.getDisplayName(nodeLabel);
            Toast.makeText(this, "Current location set to: " + displayName, Toast.LENGTH_SHORT).show();
            Log.d("SecondMeatMapView", "Start node set to: " + startNode);
        });

        btnGo.setOnClickListener(v -> {
            if (startNode == null) {
                Toast.makeText(this, "Please select a starting location by tapping on the map", Toast.LENGTH_LONG).show();
                return;
            }
            List<String> path = Dijkstra.findShortestPath(graph, startNode, DESTINATION_NODE);
            if (path == null || path.isEmpty()) {
                String startDisplayName = NodeDisplayNames.getDisplayName(startNode);
                String destDisplayName = NodeDisplayNames.getDisplayName(DESTINATION_NODE);
                Toast.makeText(this, "No path found from " + startDisplayName + " to " + destDisplayName, Toast.LENGTH_SHORT).show();
            } else {
                userMarker.setVisibility(View.VISIBLE);
                instructionBanner.setVisibility(View.VISIBLE);
                List<String> instructions = generateInstructions(path);
                secondMeatPathView.startPathAnimation(path, userMarker, instructionText, instructions, graph);
            }
        });

        btnClear.setOnClickListener(v -> {
            secondMeatPathView.clearPath();
            startNode = null;
            userMarker.setVisibility(View.GONE);
            instructionBanner.setVisibility(View.GONE);
            Toast.makeText(this, "Path cleared and location reset", Toast.LENGTH_SHORT).show();
        });
    }

    private List<String> generateInstructions(List<String> path) {
        List<String> instructions = new ArrayList<>();
        if (path.size() < 2) {
            instructions.add("You have arrived.");
            return instructions;
        }

        instructions.add("Start moving towards " + NodeDisplayNames.getDisplayName(path.get(1)));

        for (int i = 1; i < path.size() - 1; i++) {
            String prevNode = path.get(i - 1);
            String currNode = path.get(i);
            String nextNode = path.get(i + 1);

            float[] pPrev = secondMeatPathView.getNodeCoordinates(prevNode);
            float[] pCurr = secondMeatPathView.getNodeCoordinates(currNode);
            float[] pNext = secondMeatPathView.getNodeCoordinates(nextNode);

            if (pPrev == null || pCurr == null || pNext == null) continue;

            double angle = calculateAngle(pPrev, pCurr, pNext);

            if (angle > 45 && angle < 135) {
                instructions.add("Turn right towards " + NodeDisplayNames.getDisplayName(nextNode));
            } else if (angle < -45 && angle > -135) {
                instructions.add("Turn left towards " + NodeDisplayNames.getDisplayName(nextNode));
            } else {
                instructions.add("Continue straight towards " + NodeDisplayNames.getDisplayName(nextNode));
            }
        }

        instructions.add("You will arrive at " + NodeDisplayNames.getDisplayName(path.get(path.size() - 1)));
        return instructions;
    }

    private double calculateAngle(float[] p1, float[] p2, float[] p3) {
        double angle1 = Math.toDegrees(Math.atan2(p2[1] - p1[1], p2[0] - p1[0]));
        double angle2 = Math.toDegrees(Math.atan2(p3[1] - p2[1], p3[0] - p2[0]));
        double angle = angle2 - angle1;

        if (angle > 180) {
            angle -= 360;
        } else if (angle < -180) {
            angle += 360;
        }
        return angle;
    }

    private void setupGraph() {
        graph = new Graph();
        //First column
        graph.addEdge("n6", "n24", 0);
        graph.addEdge("n24", "n3", 0);
        graph.addEdge("n3", "n18", 0);
        graph.addEdge("n18", "n1", 0);
        graph.addEdge("n1", "n45", 0);
        graph.addEdge("n45", "n12", 0);
        graph.addEdge("n12", "n52", 0);
        graph.addEdge("n21", "n12", 0);
        graph.addEdge("n21", "n15", 0);
        graph.addEdge("n15", "n64", 0);

        //second column
        graph.addEdge("n27", "n30", 1);
        graph.addEdge("n30", "n33", 1);
        graph.addEdge("n33", "n36", 1);
        graph.addEdge("n39", "n42", 1);
        graph.addEdge("n39", "n36", 1);
        graph.addEdge("n42", "n1", 2);

        //third column
        graph.addEdge("n28", "n31", 1);
        graph.addEdge("n31", "n34", 1);
        graph.addEdge("n34", "n37", 1);
        graph.addEdge("n37", "n40", 1);
        graph.addEdge("n40", "n43", 1);
        graph.addEdge("n43", "n47", 1);
        graph.addEdge("n47", "n53", 1);
        graph.addEdge("n53", "n57", 1);
        graph.addEdge("n57", "n61", 1);
        graph.addEdge("n61", "n66", 1);


        //fourth column
        graph.addEdge("n7", "n5", 1);
        graph.addEdge("n5", "n26", 1);
        graph.addEdge("n26", "n4", 1);
        graph.addEdge("n2", "n19", 1);
        graph.addEdge("n2", "n48", 1);
        graph.addEdge("n48", "n13", 1);
        graph.addEdge("n13", "n22", 1);
        graph.addEdge("n22", "n16", 1);
        graph.addEdge("n16", "n67", 1);

        //fifth column
        graph.addEdge("n29", "n32", 1);
        graph.addEdge("n32", "n35", 1);
        graph.addEdge("n35", "n38", 1);
        graph.addEdge("n38", "n41", 1);
        graph.addEdge("n41", "n44", 1);
        graph.addEdge("n44", "n49", 1);
        graph.addEdge("n49", "n54", 1);
        graph.addEdge("n54", "n58", 1);
        graph.addEdge("n58", "n62", 1);
        graph.addEdge("n62", "n68", 1);

        //sixth column
        graph.addEdge("n74", "n76", 1);
        graph.addEdge("n76", "n78", 1);
        graph.addEdge("n78", "n80", 1);
        graph.addEdge("n80", "n82", 1);
        graph.addEdge("n82", "n84", 1);
        graph.addEdge("n84", "n50", 1);
        graph.addEdge("n50", "n55", 1);
        graph.addEdge("n55", "n59", 1);
        graph.addEdge("n59", "n63", 1);
        graph.addEdge("n63", "n69", 1);

        //seventh column
        graph.addEdge("n75", "n77", 1);
        graph.addEdge("n77", "n79", 1);
        graph.addEdge("n79", "n81", 1);
        graph.addEdge("n81", "n83", 1);
        graph.addEdge("n83", "n85", 1);
        graph.addEdge("n85", "n86", 1);
        graph.addEdge("n86", "n87", 1);
        graph.addEdge("n87", "n88", 0);
        graph.addEdge("n88", "n90", 1);
        graph.addEdge("n90", "n89", 1);
        graph.addEdge("n89", "n91", 1);

        //eight column
        graph.addEdge("n11", "n10", 1);
        graph.addEdge("n10", "n25", 1);
        graph.addEdge("n25", "n9", 1);
        graph.addEdge("n9", "n20", 1);
        graph.addEdge("n20", "n8", 1);
        graph.addEdge("n8", "n51", 1);
        graph.addEdge("n51", "n14", 1);
        graph.addEdge("n14", "n23", 1);
        graph.addEdge("n14", "n88", 1);
        graph.addEdge("n23", "n70", 1);
        graph.addEdge("n70", "n17", 1);


        //First Row
        graph.addEdge("n6", "n27", 1);
        graph.addEdge("n27", "n28", 1);
        graph.addEdge("n28", "n7", 1);
        graph.addEdge("n7", "n29", 1);
        graph.addEdge("n29", "n74", 1);
        graph.addEdge("n74", "n75", 1);
        graph.addEdge("n75", "n11", 1);

        //Second Row
        graph.addEdge("n3", "n36", 1);
        graph.addEdge("n36", "n37", 1);
        graph.addEdge("n37", "n4", 2);
        graph.addEdge("n4", "n38", 2);
        graph.addEdge("n38", "n80", 1);
        graph.addEdge("n80", "n81", 1);
        graph.addEdge("n81", "n9", 1);

        //Third Row
        graph.addEdge("n1", "n42", 1);
        graph.addEdge("n42", "n43", 1);
        graph.addEdge("n43", "n2", 2);
        graph.addEdge("n2", "n44", 2);
        graph.addEdge("n44", "n84", 1);
        graph.addEdge("n84", "n85", 1);
        graph.addEdge("n85", "n8", 1);

        //Fourth Row
        graph.addEdge("n12", "52", 1);
        graph.addEdge("n52", "n53", 1);
        graph.addEdge("n53", "n13", 1);
        graph.addEdge("n13", "n54", 1);
        graph.addEdge("n54", "n55", 1);
        graph.addEdge("n55", "n87", 1);
        graph.addEdge("n87", "n14", 1);

        //Fifth Row
        graph.addEdge("n21", "n56", 0);
        graph.addEdge("n56", "n57", 1);
        graph.addEdge("n57", "n22", 1);
        graph.addEdge("n22", "n58", 1);
        graph.addEdge("n58", "n59", 1);
        graph.addEdge("n59", "n88", 0);
        graph.addEdge("n88", "n23", 1);

        //Sixth Row
        graph.addEdge("n64", "n65", 1);
        graph.addEdge("n65", "n66", 1);
        graph.addEdge("n66", "n67", 1);
        graph.addEdge("n67", "n68", 1);
        graph.addEdge("n68", "n69", 1);
        graph.addEdge("n69", "n91", 1);
        graph.addEdge("n91", "n73", 1);

        graph.addEdge("n17", "n72", 0);
        graph.addEdge("n72", "n73", 0);

        graph.addEdge("n97", "n7", 1);
        graph.addEdge("n97", "n98", 1);
        graph.addEdge("n98", "n92", 1);
        graph.addEdge("n92", "n4", 1);
        graph.addEdge("n92", "n37", 1);
        graph.addEdge("n99", "n92", 1);
        graph.addEdge("n99", "n93", 1);
        graph.addEdge("n93", "n48", 1);
        graph.addEdge("n93", "n47", 1);
        graph.addEdge("n93", "n43", 1);
        graph.addEdge("n93", "n2", 1);
        graph.addEdge("n97", "n28", 1);
    }
}
