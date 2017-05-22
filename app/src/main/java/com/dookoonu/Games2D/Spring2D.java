package com.dookoonu.Games2D;

import com.dookoonu.animationframework.GameView;

/**
 * Created by cowell on 9/15/15.
 */
public class Spring2D {
    float vx, vy; // The x- and y-axis velocities
    float x, y; // The x- and y-coordinates
    float gravity;
    float mass;
    float radius = 10.0f;
    float stiffness = 0.2f;
    float damping = 0.7f;
    GameView gameView;
    int fillColor;

    Spring2D(float xpos, float ypos, float m, float g, GameView gameView) {
        x = xpos;
        y = ypos;
        mass = m;
        gravity = g;
        this.gameView = gameView;
        fillColor = gameView.color(255);
    }

    void update(float targetX, float targetY) {
        float forceX = (targetX - x) * stiffness;
        float ax = forceX / mass;
        vx = damping * (vx + ax);
        x += vx;
        float forceY = (targetY - y) * stiffness;
        forceY += gravity;
        float ay = forceY / mass;
        vy = damping * (vy + ay);
        y += vy;
    }

    void display(float nx, float ny) {
        gameView.noStroke();
        //gameView.circle(x, y, radius*2);
        gameView.ellipse(x,y,radius,radius);
        gameView.stroke(fillColor);
        gameView.line(x, y, nx, ny);
    }
}
