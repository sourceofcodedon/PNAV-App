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
        } else if ("n4".equals(nodeLabel)) {
            return "MOD - RIZAL VEGETABLES AND FLOWER SHOP";
        } else if ("n53".equals(nodeLabel)) {
            return "PALENGKE HERO";
        } else if ("n9".equals(nodeLabel)) {
            return "BGM \"BABY\" FARM PRODUCTS";
        } else if ("n10".equals(nodeLabel)) {
            return "BOY & SITA";
        } else if ("n13".equals(nodeLabel)) {
            return "JOEY & QUEY BAGUIO VEGETABLE WHILESALER & RETAILER";
        } else if ("n14".equals(nodeLabel)) {
            return "JAENA & VEGETABLES STORE";
        } else if ("n16".equals(nodeLabel)) {
            return "JAY & REAH STORE";
        } else if ("n92".equals(nodeLabel)) {
            return "SDR BAGUIO VEGETABLES DEALER";
        } else if ("n19".equals(nodeLabel)) {
            return "VANGIE STORE";
        } else if ("n20".equals(nodeLabel)) {
            return "SARAH DRIED FISH STORE";
        } else if ("n22".equals(nodeLabel)) {
            return "RYAN AND APPLE KOREAN ITEMS";
        } else if ("n23".equals(nodeLabel)) {
            return "ZHEN & GWEN";
        } else if ("n25".equals(nodeLabel)) {
            return "RAMON & BETH FRUIT STORE";
        } else if ("n26".equals(nodeLabel)) {
            return "EDMON BARRIO VEGETABLES DEALER";
        } else if ("n30".equals(nodeLabel)) {
            return "ALEX & CINDY COCONUT STORE";
        } else if ("n31".equals(nodeLabel)) {
            return "STOCK ROOM";
        } else if ("n32".equals(nodeLabel)) {
            return "MARIBEL'S VEGETABLE STORE";
        } else if ("n33".equals(nodeLabel)) {
            return "FERDIE & BING BANANA & COCONUT STORE";
        } else if ("n34".equals(nodeLabel)) {
            return "ALING APING VEGETABLES STORE";
        } else if ("n35".equals(nodeLabel)) {
            return "RAMON & BETH STORE";
        } else if ("n36".equals(nodeLabel)) {
            return "GINA'S VEGETABLE STORE";
        } else if ("n37".equals(nodeLabel)) {
            return "PENG & LENG FRUITS & VEGETABLES STORE";
        } else if ("n38".equals(nodeLabel)) {
            return "BEN & LOLET (JERR) FROZEN FOODS";
        } else if ("n39".equals(nodeLabel)) {
            return "FELIX & EVELYN VEGETABLE DEALER & RETAILER";
        } else if ("n40".equals(nodeLabel)) {
            return "BEN & LOLET";
        } else if ("n41".equals(nodeLabel)) {
            return "MALOU FROZEN FOOD";
        } else if ("n46".equals(nodeLabel)) {
            return "TORING FROZEN FOODS";
        } else if ("n47".equals(nodeLabel)) {
            return "MARIO & REBING FOOD PRODUCTS";
        } else if ("n49".equals(nodeLabel)) {
            return "TESS GARCIA VEGETABLE DEALER";
        } else if ("n50".equals(nodeLabel)) {
            return "GINA'S STORE";
        } else if ("n51".equals(nodeLabel)) {
            return "RJ VEGETABLES STORE";
        } else if ("n52".equals(nodeLabel)) {
            return "JULIUS D GREAT STORE";
        } else if ("n5".equals(nodeLabel)) {
            return "NYONG'S SARI SARI STORE";
        } else if ("n54".equals(nodeLabel)) {
            return "CESAR & SHAI FOOD PRODUCTS";
        } else if ("n55".equals(nodeLabel)) {
            return "DIANA'S MEAT STORE";
        } else if ("n56".equals(nodeLabel)) {
            return "MILA SICAT";
        } else if ("n57".equals(nodeLabel)) {
            return "JEFF & JERZELL";
        } else if ("n58".equals(nodeLabel)) {
            return "WENDY";
        } else if ("n59".equals(nodeLabel)) {
            return "BENG PANTIG";
        } else if ("n60".equals(nodeLabel)) {
            return "CORA ROQUE";
        } else if ("n61".equals(nodeLabel)) {
            return "Alex and Cindy COCONUT STORE";
        } else if ("n88".equals(nodeLabel)) {
            return "JASON & FRIGA";
        } else if ("n63".equals(nodeLabel)) {
            return "Brod. Long Hair (Live tilapia and Fresh Bangus)";
        } else if ("n70".equals(nodeLabel)) {
            return "Bert and Ester Meat Store";
        } else if ("n72".equals(nodeLabel)) {
            return "Chona's Chicken Store";
        } else if ("n76".equals(nodeLabel)) {
            return "Brian and & Shara";
        } else if ("n90".equals(nodeLabel)) {
            return "Edgar and Ason";
        }
        // Add more mappings here for other stores
        return nodeLabel;
    }
}
