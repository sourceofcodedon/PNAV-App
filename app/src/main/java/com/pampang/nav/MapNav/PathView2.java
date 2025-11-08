package com.pampang.nav.MapNav;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import java.util.*;

public class PathView2 extends View {

    private Paint pathPaint;
    private Paint nodePaint;
    private Paint textPaint;
    private Paint movingDotPaint;

    private Map<String, float[]> baseNodes;
    private Map<String, float[]> nodes = new HashMap<>();

    private List<float[]> activePath = new ArrayList<>();
    private float animProgress = 0f;
    private PathMeasure pathMeasure;
    private float pathLength = 0f;
    private Path animatedPath = new Path();
    private ValueAnimator animator;

    private float scaleX = 1f, scaleY = 1f;

    // Listener for node clicks
    public interface OnNodeClickListener {
        void onNodeClick(String nodeLabel);
    }
    private OnNodeClickListener nodeClickListener;

    public void setOnNodeClickListener(OnNodeClickListener listener) {
        this.nodeClickListener = listener;
    }


    public PathView2(Context context, @Nullable AttributeSet attrs) {
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
        baseNodes.put("A", new float[]{125f, 1125f});
        baseNodes.put("B", new float[]{295f, 1125f});
        baseNodes.put("C", new float[]{125f, 1470f});
        baseNodes.put("D", new float[]{735f, 1125f});
        baseNodes.put("E", new float[]{735f, 1590f});
        baseNodes.put("F", new float[]{770f, 1590f});
        baseNodes.put("G", new float[]{735f, 1460f});
        baseNodes.put("H", new float[]{295f, 1465f});

        // Add more nodes to fill the screen
        baseNodes.put("I", new float[]{125f, 1700f});
        baseNodes.put("J", new float[]{295f, 1700f});
        baseNodes.put("K", new float[]{735f, 1700f});
        baseNodes.put("L", new float[]{770f, 1700f});
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

    public void clearPath() {
        if (animator != null) animator.cancel();
        activePath.clear();
        pathMeasure = null;
        animatedPath.reset();
        animProgress = 0f;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Map.Entry<String, float[]> entry : nodes.entrySet()) {
            float[] point = entry.getValue();
            String label = entry.getKey();
            canvas.drawCircle(point[0], point[1], 30f * scaleX, nodePaint);
            canvas.drawText(label, point[0], point[1] + (10 * scaleY), textPaint);
        }

        if (pathMeasure != null) {
            animatedPath.reset();
            float stop = pathLength * animProgress;
            pathMeasure.getSegment(0, stop, animatedPath, true);
            canvas.drawPath(animatedPath, pathPaint);
            float[] pos = new float[2];
            if (pathMeasure.getPosTan(stop, pos, null)) {
                canvas.drawCircle(pos[0], pos[1], 12f * scaleX, movingDotPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            Log.d("MapTouch", "Tapped at X=" + x + " Y=" + y);

            for (Map.Entry<String, float[]> entry : nodes.entrySet()) {
                float[] nodePos = entry.getValue();
                float dx = x - nodePos[0];
                float dy = y - nodePos[1];
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance < 50f * scaleX) {
                    String clickedNode = entry.getKey();
                    Log.d("MapTouch", "Clicked near node: " + clickedNode);
                    if (nodeClickListener != null) {
                        nodeClickListener.onNodeClick(clickedNode);
                    }
                    return true; // Event handled
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
