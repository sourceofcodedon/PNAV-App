package com.pampang.nav.MapNav;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.*;

public class SecondGulayPath extends View {

    private Paint pathPaint;
    private Paint nodePaint;
    private Paint textPaint;
    private Paint movingDotPaint;
    private Paint markerPaint;

    private Map<String, float[]> baseNodes;
    private Map<String, float[]> nodes = new HashMap<>();

    private List<float[]> activePath = new ArrayList<>();
    private float animProgress = 0f;
    private PathMeasure pathMeasure;
    private float pathLength = 0f;
    private Path animatedPath = new Path();
    private ValueAnimator animator;
    private ValueAnimator markerAnimator;

    private String selectedNode = null;
    private List<String> clickableNodes = new ArrayList<>();

    private float scaleX = 1f, scaleY = 1f;

    // Listener for node clicks
    public interface OnNodeClickListener {
        void onNodeClick(String nodeLabel);
    }
    private OnNodeClickListener nodeClickListener;

    public void setOnNodeClickListener(OnNodeClickListener listener) {
        this.nodeClickListener = listener;
    }

    public void setClickableNodes(List<String> clickableNodes) {
        this.clickableNodes = clickableNodes;
        invalidate();
    }

    public SecondGulayPath(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        pathPaint = new Paint();
        pathPaint.setColor(Color.parseColor("#016B61"));
        pathPaint.setStrokeWidth(15f);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setAntiAlias(true);
        pathPaint.setStrokeCap(Paint.Cap.ROUND);
        pathPaint.setStrokeJoin(Paint.Join.ROUND);

        nodePaint = new Paint();
        nodePaint.setColor(Color.TRANSPARENT);
        nodePaint.setStyle(Paint.Style.FILL);
        nodePaint.setAntiAlias(true);

        movingDotPaint = new Paint();
        movingDotPaint.setColor(Color.WHITE);
        movingDotPaint.setStyle(Paint.Style.FILL);
        movingDotPaint.setAntiAlias(true);

        markerPaint = new Paint();
        markerPaint.setColor(Color.RED);
        markerPaint.setStyle(Paint.Style.FILL);
        markerPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.TRANSPARENT);
        textPaint.setTextSize(32f);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        setupBaseNodes();
    }

    private void setupBaseNodes() {
        baseNodes = new HashMap<>();
        baseNodes.put("n1", new float[]{100f, 1125f});
        baseNodes.put("n2", new float[]{440f, 1125f});
        baseNodes.put("n3", new float[]{100f, 660f});
        baseNodes.put("n4", new float[]{440f, 660f});
        baseNodes.put("n5", new float[]{440f, 380f});
        baseNodes.put("n6", new float[]{100f, 250f});
        baseNodes.put("n7", new float[]{440f, 250f});

        // Add more nodes to fill the screen
        baseNodes.put("n8", new float[]{850f, 1125f});
        baseNodes.put("n9", new float[]{850f, 660f});
        baseNodes.put("n10", new float[]{850f, 380f});
        baseNodes.put("n11", new float[]{850f, 250f});

        baseNodes.put("n12", new float[]{100f, 1480f});
        baseNodes.put("n13", new float[]{440f, 1480f});
        baseNodes.put("n14", new float[]{850f, 1480f});

        baseNodes.put("n15", new float[]{100f, 1900f});
        baseNodes.put("n16", new float[]{440f, 1900f});
        baseNodes.put("n17", new float[]{850f, 1900f});

        baseNodes.put("n18", new float[]{100f, 890f});
        baseNodes.put("n19", new float[]{440f, 890f});
        baseNodes.put("n20", new float[]{850f, 890f});

        baseNodes.put("n21", new float[]{100f, 1700f});
        baseNodes.put("n22", new float[]{440f, 1700f});
        baseNodes.put("n23", new float[]{850f, 1700f});

        baseNodes.put("n24", new float[]{100f, 500f});
        baseNodes.put("n25", new float[]{850f, 500f});

        //new nodes
        baseNodes.put("n26", new float[]{440f, 500f});

        //new nodes from red dots
        baseNodes.put("n27", new float[]{215f, 250f});
        baseNodes.put("n28", new float[]{295f, 250f});
        baseNodes.put("n29", new float[]{590f, 250f});

        baseNodes.put("n30", new float[]{215f, 380f});
        baseNodes.put("n31", new float[]{295f, 380f});
        baseNodes.put("n32", new float[]{590f, 380f});

        baseNodes.put("n33", new float[]{215f, 500f});
        baseNodes.put("n34", new float[]{295f, 500f});
        baseNodes.put("n35", new float[]{590f, 500f});

        baseNodes.put("n36", new float[]{215f, 660f});
        baseNodes.put("n37", new float[]{295f, 660f});
        baseNodes.put("n38", new float[]{590f, 660f});

        baseNodes.put("n39", new float[]{215f, 890f});
        baseNodes.put("n40", new float[]{295f, 890f});
        baseNodes.put("n41", new float[]{590f, 890f});

        baseNodes.put("n42", new float[]{215f, 1125f});
        baseNodes.put("n43", new float[]{295f, 1125f});
        baseNodes.put("n44", new float[]{590f, 1125f});

        baseNodes.put("n45", new float[]{100f, 1312f});
        baseNodes.put("n46", new float[]{215f, 1312f});
        baseNodes.put("n47", new float[]{295f, 1312f});
        baseNodes.put("n48", new float[]{440f, 1312f});
        baseNodes.put("n49", new float[]{590f, 1312f});
        baseNodes.put("n50", new float[]{665f, 1312f});
        baseNodes.put("n51", new float[]{850f, 1312f});

        baseNodes.put("n52", new float[]{215f, 1480f});
        baseNodes.put("n53", new float[]{295f, 1480f});
        baseNodes.put("n54", new float[]{590f, 1480f});
        baseNodes.put("n55", new float[]{665f, 1480f});

        baseNodes.put("n56", new float[]{215f, 1700f});
        baseNodes.put("n57", new float[]{295f, 1700f});
        baseNodes.put("n58", new float[]{590f, 1700f});
        baseNodes.put("n59", new float[]{665f, 1700f});

        baseNodes.put("n60", new float[]{215f, 1900f});
        baseNodes.put("n61", new float[]{295f, 1900f});
        baseNodes.put("n62", new float[]{590f, 1900f});
        baseNodes.put("n63", new float[]{665f, 1900f});

        baseNodes.put("n64", new float[]{100f, 2150f});
        baseNodes.put("n65", new float[]{215f, 2150f});
        baseNodes.put("n66", new float[]{295f, 2150f});
        baseNodes.put("n67", new float[]{440f, 2150f});
        baseNodes.put("n68", new float[]{590f, 2150f});
        baseNodes.put("n69", new float[]{665f, 2150f});
        baseNodes.put("n70", new float[]{850f, 1800f});

        baseNodes.put("n71", new float[]{900f, 1900f});
        baseNodes.put("n72", new float[]{900f, 2000f});
        baseNodes.put("n73", new float[]{900f, 2150f});

        //new nodes from red dots (second batch)
        baseNodes.put("n74", new float[]{670f, 250f});
        baseNodes.put("n75", new float[]{745f, 250f});
        baseNodes.put("n76", new float[]{670f, 380f});
        baseNodes.put("n77", new float[]{745f, 380f});
        baseNodes.put("n78", new float[]{670f, 500f});
        baseNodes.put("n79", new float[]{745f, 500f});
        baseNodes.put("n80", new float[]{670f, 660f});
        baseNodes.put("n81", new float[]{745f, 660f});
        baseNodes.put("n82", new float[]{670f, 890f});
        baseNodes.put("n83", new float[]{745f, 890f});
        baseNodes.put("n84", new float[]{670f, 1125f});
        baseNodes.put("n85", new float[]{745f, 1125f});

        baseNodes.put("n86", new float[]{745f, 1312f});
        baseNodes.put("n87", new float[]{745f, 1480f});
        baseNodes.put("n88", new float[]{745f, 1700f});
        baseNodes.put("n89", new float[]{745f, 1900f});
        baseNodes.put("n90", new float[]{745f, 1800f});
        baseNodes.put("n91", new float[]{740f, 2150f});
        baseNodes.put("n92", new float[]{375f, 560f});
        baseNodes.put("n93", new float[]{375f, 1110f});
        baseNodes.put("n94", new float[]{375f, 1900f});
        baseNodes.put("n95", new float[]{375f, 1780f});
        baseNodes.put("n96", new float[]{375f, 2150f});
        baseNodes.put("n97", new float[]{375f, 250f});
        baseNodes.put("n98", new float[]{375f, 380f});
        baseNodes.put("n99", new float[]{375f, 890f});
    }

    public void setSelectedNode(String nodeLabel) {
        selectedNode = nodeLabel;
        invalidate();
    }
    
    public float[] getNodeCoordinates(String nodeLabel) {
        return nodes.get(nodeLabel);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float baseWidth = 1080f;
        float baseHeight = 2400f;
        scaleX = w / baseWidth;
        scaleY = h / baseHeight;
        nodes.clear();
        for (Map.Entry<String, float[]> entry : baseNodes.entrySet()) {
            float[] p = entry.getValue();
            nodes.put(entry.getKey(), new float[]{p[0] * scaleX, p[1] * scaleY});
        }
        pathPaint.setStrokeWidth(20f * scaleX);
        textPaint.setTextSize(32f * scaleX);
    }

    public void showAnimatedPath(List<String> nodePath) {
        if (animator != null) animator.cancel();
        if (nodePath == null || nodePath.size() < 2) return;
        activePath.clear();
        for (String node : nodePath) {
            float[] point = nodes.get(node);
            if (point != null) activePath.add(point);
        }
        if (activePath.size() < 2) return;
        Path fullPath = new Path();
        float[] start = activePath.get(0);
        fullPath.moveTo(start[0], start[1]);
        for (int i = 1; i < activePath.size(); i++) {
            float[] next = activePath.get(i);
            fullPath.lineTo(next[0], next[1]);
        }
        pathMeasure = new PathMeasure(fullPath, false);
        pathLength = pathMeasure.getLength();
        animProgress = 0f;
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(a -> {
            animProgress = (float) a.getAnimatedValue();
            invalidate();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animator = null;
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                animator = null;
            }
        });
        animator.start();
    }

    public void startPathAnimation(List<String> nodePath, ImageView userMarker, TextView instructionText, List<String> instructions, Graph graph) {
        if (markerAnimator != null) {
            markerAnimator.cancel();
        }

        Path fullPath = new Path();
        List<Float> segmentLengths = new ArrayList<>();
        if (nodePath.size() >= 2) {
            float[] startPoint = nodes.get(nodePath.get(0));
            fullPath.moveTo(startPoint[0], startPoint[1]);
            float totalLength = 0;
            for (int i = 1; i < nodePath.size(); i++) {
                float[] prevPoint = nodes.get(nodePath.get(i-1));
                float[] nextPoint = nodes.get(nodePath.get(i));
                fullPath.lineTo(nextPoint[0], nextPoint[1]);
                float segmentLength = (float) Math.sqrt(Math.pow(nextPoint[0] - prevPoint[0], 2) + Math.pow(nextPoint[1] - prevPoint[1], 2));
                totalLength += segmentLength;
                segmentLengths.add(totalLength);
            }
        }

        PathMeasure pathMeasure = new PathMeasure(fullPath, false);
        float pathLength = pathMeasure.getLength();

        markerAnimator = ValueAnimator.ofFloat(0f, 1f);
        markerAnimator.setDuration(15000); // 15-second animation for the marker
        markerAnimator.setInterpolator(new LinearInterpolator());

        float[] pos = new float[2];
        final int[] currentInstructionIndex = {0};

        markerAnimator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            float distance = pathLength * progress;
            pathMeasure.getPosTan(distance, pos, null);

            // Update marker position
            userMarker.setTranslationX(pos[0] - (userMarker.getWidth() / 2f));
            userMarker.setTranslationY(pos[1] - (userMarker.getHeight() / 2f));

            // Update instruction text
            for (int i = 0; i < segmentLengths.size(); i++) {
                if (distance < segmentLengths.get(i)) {
                    if (currentInstructionIndex[0] != i) {
                        currentInstructionIndex[0] = i;
                        instructionText.setText(instructions.get(i));
                    }
                    break;
                }
            }
        });

        markerAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                instructionText.setText("You have arrived!");
            }
        });

        markerAnimator.start();
    }


    public void clearPath() {
        if (animator != null) animator.cancel();
        if (markerAnimator != null) markerAnimator.cancel();
        activePath.clear();
        pathMeasure = null;
        animatedPath.reset();
        animProgress = 0f;
        selectedNode = null; // Also clear the selected node
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Map.Entry<String, float[]> entry : nodes.entrySet()) {
            String label = entry.getKey();
            float[] point = entry.getValue();
            if (clickableNodes.contains(label)) {
                nodePaint.setColor(Color.TRANSPARENT);
            } else {
                nodePaint.setColor(Color.TRANSPARENT);
            }
            canvas.drawCircle(point[0], point[1], 20f * scaleX, nodePaint);
            canvas.drawText(label, point[0], point[1] + (10 * scaleY), textPaint);
        }

        if (selectedNode != null && nodes.containsKey(selectedNode)) {
            float[] point = nodes.get(selectedNode);
            canvas.drawCircle(point[0], point[1], 15f * scaleX, markerPaint); // Draw marker
        }

        if (pathMeasure != null) {
            animatedPath.reset();
            float stop = pathLength * animProgress;
            pathMeasure.getSegment(0, stop, animatedPath, true);
            canvas.drawPath(animatedPath, pathPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            float clickRadius = 30f * scaleX;

            for (Map.Entry<String, float[]> entry : nodes.entrySet()) {
                String nodeLabel = entry.getKey();
                float[] nodePos = entry.getValue();

                if (clickableNodes.contains(nodeLabel)) {
                    double distance = Math.sqrt(Math.pow(x - nodePos[0], 2) + Math.pow(y - nodePos[1], 2));
                    if (distance <= clickRadius) {
                        if (nodeClickListener != null) {
                            nodeClickListener.onNodeClick(nodeLabel);
                        }
                        return true;
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
