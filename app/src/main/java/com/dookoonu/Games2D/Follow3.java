package com.dookoonu.Games2D;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.dookoonu.animationframework.GameView;

/**
 * Created by cowell on 9/14/15.
 * A segmented line follows the mouse. The relative angle from
 * each segment to the next is calculated with atan2() and the
 * position of the next is calculated with sin() and cos().
 */
public class Follow3 extends GameView {
    float[] x = new float[20];
    float[] y = new float[20];
    float segLength = 18;
    int rad = 6;
    float xpos = 80, ypos = 80;    // Starting position of shape

    float xspeed = 8.8f;  // Speed of the shape
    float yspeed = 8.2f;  // Speed of the shape

    int xdirection = 1;  // Left or Right
    int ydirection = 1;  // Top to Bottom

    public Follow3(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void renderFrame() {
        background(Color.GREEN);
        // Update the position of the shape
        xpos = xpos + (xspeed * xdirection);
        ypos = ypos + (yspeed * ydirection);

        // Test to see if the shape exceeds the boundaries of the screen
        // If it does, reverse its direction by multiplying by -1
        if (xpos > surfaceWidth - rad || xpos < rad) {
            xdirection *= -1;
        }
        if (ypos > surfaceHeight - rad || ypos < rad) {
            ydirection *= -1;
        }

        dragSegment(0, xpos, ypos);
        for (int i = 0; i < x.length - 1; i++) {
            dragSegment(i + 1, x[i], y[i]);
        }
    }

    @Override
    protected void initialize() {
        strokeWidth(9);
        stroke(color(66, 77, 22, 100));
        frameRate(30);
    }


    void dragSegment(int i, float xin, float yin) {
        float dx = xin - x[i];
        float dy = yin - y[i];
        float angle = (float) Math.atan2(dy, dx);
        x[i] = xin - (float) Math.cos(angle) * segLength;
        y[i] = yin - (float) Math.sin(angle) * segLength;
        segment(x[i], y[i], angle);
    }

    void segment(float x, float y, float a) {
        saveState();
        translate(x, y);
        rotate(a);
        line(0, 0, segLength, 0);
        restoreState();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i("touch", "ok");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xpos = event.getX();
                ypos = event.getY();
                break;
            default:
                return false;
        }
        return true; //false if you want to swipe
    }

}
