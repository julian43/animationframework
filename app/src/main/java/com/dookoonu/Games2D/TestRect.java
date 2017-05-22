package com.dookoonu.Games2D;

import android.content.Context;
import android.util.AttributeSet;

import com.dookoonu.animationframework.GameView;

import static com.dookoonu.animationframework.Utils.random;
import static com.dookoonu.animationframework.Utils.second;

/**
 * * Rotate.
 *
 * Rotating a square around the Z axis. To get the results
 * you expect, send the rotate function angle parameters that are
 * values between 0 and PI*2 (TWO_PI which is roughly 6.28). If you prefer to
 * think about angles as degrees (0-360), you can use the radians()
 * method to convert your values. For example: scale(radians(90))
 * is identical to the statement scale(PI/2).
 * Created by cowell on 9/22/15.
 *
 */


public class TestRect extends GameView{
    float angle;
    float jitter;
    public TestRect(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void renderFrame() {
        background(color(51));

        // during even-numbered seconds (0, 2, 4, 6...)
        if (second() % 2 == 0) {
            jitter = random(-0.3f, 0.3f);
        }
        angle = angle + jitter;
        float c = (float)Math.cos(angle);
        translate(surfaceWidth/2, surfaceHeight/2);
        rotate(c);
        rect(0, 0, 180, 180);
    }

    @Override
    protected void initialize() {
        fill(color(255));
        rectMode(CENTER);
        frameRate(30);
    }
}
