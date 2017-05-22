package com.dookoonu.Games2D;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.dookoonu.animationframework.GameView;

/**
 * Created by cowell on 9/15/15.
 */
public class Ex_20 extends GameView {
    private FixedSpring s1, s2;
    private float gravity = 1.2f;

    public Ex_20(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void renderFrame() {
        background(Color.GRAY);
        s1.update(s2.x, s2.y);
        s2.update(s1.x, s1.y);
        s1.display(s2.x, s2.y);
        s2.display(s1.x, s1.y);
    }

    @Override
    protected void initialize() {
        fill(color(0));
        ellipseMode(RADIUS);
        // Inputs: x, y, mass, gravity, length
        s1 = new FixedSpring(45, 33, 1.5f, gravity, 40.0f, this);
        s2 = new FixedSpring(55, 66, 1.5f, gravity, 40.0f, this);
        frameRate(30);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                s1.x = event.getX();
                s1.y = event.getY();
                break;
            default:
                return false;
        }
        return true; //false if you want to swipe
    }

}
