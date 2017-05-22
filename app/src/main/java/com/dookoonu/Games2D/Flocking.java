package com.dookoonu.Games2D;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.dookoonu.animationframework.GameView;

/**
 * Created by cowell on 9/21/15.
 */
public class Flocking extends GameView {
    Flock flock;

    public Flocking(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void renderFrame() {
        background(color(50));
        flock.run();
    }

    @Override
    protected void initialize() {
        flock = new Flock();
        // Add an initial set of boids into the system
        for (int i = 0; i < 60; i++) {
            flock.addBoid(new Boid(surfaceWidth/2,surfaceHeight/2, this));
        }

        frameRate(30);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                flock.addBoid(new Boid(event.getX(),event.getY(), this));
                break;
            default:
                return false;
        }
        return true; //false if you want to swipe
    }
}
