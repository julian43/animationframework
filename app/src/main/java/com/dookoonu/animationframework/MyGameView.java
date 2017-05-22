package com.dookoonu.animationframework;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;

/**
 * Created by cowell on 7/10/15.
 */
public class MyGameView extends GameView {

    public MyGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    int rad = 60;        // Width of the shape
    float xpos, ypos;    // Starting position of shape

    float xspeed = 2.8f;  // Speed of the shape
    float yspeed = 2.2f;  // Speed of the shape

    int xdirection = 1;  // Left or Right
    int ydirection = 1;  // Top to Bottom
    float dx,dy;
    float angle = PI/3.0f;
    Bitmap pic;

    @Override
    protected void renderFrame() {
        //surfaceCanvas.drawColor(Color.BLACK);
        //background(Color.BLACK);
        //fill(Color.GREEN);
        //circle(50 + dx, 60 + dy, 20);
        //fill(Color.YELLOW);
        //rect(60,60,100,100);

        text(Long.toString(frameCount),150,70);


        // Update the position of the shape
        xpos = xpos + ( xspeed * xdirection );
        ypos = ypos + ( yspeed * ydirection );

        // Test to see if the shape exceeds the boundaries of the screen
        // If it does, reverse its direction by multiplying by -1
        if (xpos > surfaceWidth-rad || xpos < rad) {
            xdirection *= -1;
        }
        if (ypos > surfaceHeight-rad || ypos < rad) {
            ydirection *= -1;
        }

        // Draw the shape
        fill(0,0,0,24);
        rect(0,0,surfaceWidth,surfaceHeight);
        stroke(Color.RED);
        fill(color(23,123, 11));
        circle(xpos, ypos, rad);
        fill(color(123, 33, 111));
        //circle(xpos*0.8f, ypos*0.8f, rad/2);
        arc(xpos*0.6f, ypos*0.6f, xpos*0.8f + 70, ypos*0.7f + 70, 0, PI, true);
        fill(Color.WHITE);
        stroke(Color.YELLOW);


        saveState();
        translate(50,90);
        rotate(angle - 2*dx);//this must happen before call to beginShape
        beginShape();
        vertex(30, 20);
        vertex(85, 20);
        vertex(85, 75);
        vertex(30, 75);
        endShape();
        restoreState();

        noStroke();

        fill(Color.WHITE);
        rect(0, 0, 50, 50);  // White rectangle

        saveState();
        translate(surfaceWidth/2, surfaceHeight/2);
        fill(Color.YELLOW);

        rotate(angle + dx);
        rect(-26, -26, 52, 52);
        restoreState();

        fill(color(100));
        rect(15, 10, 50, 50);  // Gray rectangle
        dx+=0.5f;

        image(0, 50,50,100,100);
    }

    @Override
    public void initialize() {
        frameRate(30);
        xpos = surfaceWidth/2;
        ypos = surfaceHeight/2;
        background(Color.RED);
        strokeWidth(2.0f);
        String url = "http://10.0.2.2:8080/marilyn/marilyn-monroe001.jpg";
        //pic = loadImage(R.raw.img1, 100,100);
        loadImage(url);
        //noLoop();
    }
}
