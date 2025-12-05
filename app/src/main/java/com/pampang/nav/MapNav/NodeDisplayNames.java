package com.pampang.nav.MapNav;

import java.util.Arrays;

public class NodeDisplayNames {

    public static String getDisplayName(String nodeLabel) {
        if (Arrays.asList("n1", "n8").contains(nodeLabel)) {
            return "Entrance";
        } else if (Arrays.asList("n6", "n24", "n3", "n18").contains(nodeLabel)) {
            return "Entrance Leftwing";
        } else if (Arrays.asList("n45", "n12", "n21", "n15", "n64").contains(nodeLabel)) {
            return "Rightwing Entrance";
        } else if (Arrays.asList("n27", "n28", "n97", "n7", "n29", "n74", "n75", "n11").contains(nodeLabel)) {
            return "LeftWing";
        } else if (Arrays.asList("n42", "n43", "n93", "n2", "n29", "n44", "n84", "n85").contains(nodeLabel)) {
            return "Hallway";
        } else if (Arrays.asList("n65", "n66", "n96", "n67", "n68", "n69", "n91", "n73").contains(nodeLabel)) {
            return "RightWing";
        } else if ("n48".equals(nodeLabel)) {
            return "Pampang Office";
        }
        // Add more mappings here for other stores
        return nodeLabel;
    }
}
