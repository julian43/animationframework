package com.dookoonu.Games2D;

import com.dookoonu.animationframework.GameView;

/**
 * Created by cowell on 9/15/15.
 */
public class FixedSpring extends Spring2D{
    float springLength;

    FixedSpring(float xpos, float ypos, float m, float g, float s, GameView gameView) {
        super(xpos, ypos, m, g, gameView);
        springLength = s;
    }

    void update(float newX, float newY) {
        // Calculate the target position
        float dx = x - newX;
        float dy = y - newY;
        float angle = (float)Math.atan2(dy, dx);
        float targetX = newX + (float)Math.cos(angle) * springLength;
        float targetY = newY + (float)Math.sin(angle) * springLength;

        // Activate update method from Spring2D
        super.update(targetX, targetY);

        // Constrain to display window
        x = gameView.constrain(x, radius, gameView.getSurfaceWidth() - radius);
        y = gameView.constrain(y, radius, gameView.getSurfaceHeight() - radius);
    }
}
