package com.pampang.nav.MapNav;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecondMeatPathView extends View {

    private Paint pathPaint;
    private Paint nodePaint;
    private Paint textPaint;
    private Paint movingDotPaint;

    private Map<String, float[]> baseNodes;
    private Map<String, float[]> scaledNodes = new HashMap<>();
    private List<float[]> activePath = new ArrayList<>();

    private float animProgress = 0f;
    private PathMeasure pathMeasure;
    private float pathLength = 0f;
    private Path animatedPath = new Path();
    private ValueAnimator animator;

    // For responsiveness
    private float scaleX = 1f, scaleY = 1f;
    private static final float BASE_WIDTH = 1080f;
    private static final float BASE_HEIGHT = 2400f;

    public SecondMeatPathView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        pathPaint = new Paint();
        pathPaint.setColor(Color.BLUE);
        pathPaint.setStrokeWidth(15f);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setAntiAlias(true);
        pathPaint.setStrokeCap(Paint.Cap.ROUND);
        pathPaint.setStrokeJoin(Paint.Join.ROUND);

        nodePaint = new Paint();
        nodePaint.setColor(Color.GRAY);
        nodePaint.setStyle(Paint.Style.FILL);
        nodePaint.setAntiAlias(true);

        movingDotPaint = new Paint();
        movingDotPaint.setColor(Color.WHITE);
        movingDotPaint.setStyle(Paint.Style.FILL);
        movingDotPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(32f);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        setupBaseNodes();
    }

    private void setupBaseNodes() {
        baseNodes = new HashMap<>();

        baseNodes.put("A", new float[]{85f, 1125f});
        baseNodes.put("B", new float[]{85f, 1540f});
        baseNodes.put("C", new float[]{85f, 1820f});
        baseNodes.put("D", new float[]{280f, 1820f});
        baseNodes.put("E", new float[]{280f, 1540f});
        baseNodes.put("F", new float[]{280f, 1125f});
        baseNodes.put("G", new float[]{360f, 1125f});
        baseNodes.put("H", new float[]{360f, 1540f});
        baseNodes.put("I", new float[]{360f, 1820f});
        baseNodes.put("J", new float[]{455f, 1820f});
        baseNodes.put("K", new float[]{455f, 1540f});
        baseNodes.put("L", new float[]{455f, 1125f});
        baseNodes.put("M", new float[]{535f, 1125f});
        baseNodes.put("N", new float[]{535f, 1540f});
        baseNodes.put("O", new float[]{535f, 1820f});
        baseNodes.put("P", new float[]{623f, 1820f});
        baseNodes.put("Q", new float[]{623f, 1540f});
        baseNodes.put("R", new float[]{623f, 1125f});
        baseNodes.put("S", new float[]{710f, 1820f});
        baseNodes.put("T", new float[]{710f, 1540f});
        baseNodes.put("U", new float[]{710f, 1125f});
        baseNodes.put("V", new float[]{790f, 1125f});
        baseNodes.put("W", new float[]{790f, 1540f});
        baseNodes.put("X", new float[]{790f, 1740f});
        baseNodes.put("Y", new float[]{765f, 1820f});
        baseNodes.put("Z", new float[]{790f, 1820f});

    }
    // 🔁 Recalculate scaled positions whenever view size changes
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        scaleX = w / BASE_WIDTH;
        scaleY = h / BASE_HEIGHT;

        scaledNodes.clear();
        for (Map.Entry<String, float[]> entry : baseNodes.entrySet()) {
            float[] p = entry.getValue();
            scaledNodes.put(entry.getKey(), new float[]{p[0] * scaleX, p[1] * scaleY});
        }
    }

    public void showAnimatedPath(List<String> nodePath) {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }

        if (nodePath == null || nodePath.size() < 2) return;

        activePath.clear();
        for (String node : nodePath) {
            float[] point = scaledNodes.get(node);
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
        animator.setDuration(1200);
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
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
        activePath.clear();
        pathMeasure = null;
        animatedPath.reset();
        animProgress = 0f;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Map.Entry<String, float[]> entry : scaledNodes.entrySet()) {
            float[] point = entry.getValue();
            String label = entry.getKey();
            canvas.drawCircle(point[0], point[1], 7f, nodePaint);
            canvas.drawText(label, point[0], point[1] - 18f, textPaint);
        }

        if (pathMeasure == null) return;

        animatedPath.reset();
        float stop = pathLength * animProgress;
        pathMeasure.getSegment(0, stop, animatedPath, true);
        canvas.drawPath(animatedPath, pathPaint);

        float[] pos = new float[2];
        if (pathMeasure.getPosTan(stop, pos, null)) {
            canvas.drawCircle(pos[0], pos[1], 12f, movingDotPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            Log.d("MapTouch2", "Tapped at X=" + x + " Y=" + y);
        }
        return true;
    }
}
