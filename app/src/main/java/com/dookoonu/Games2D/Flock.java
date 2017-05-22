package com.dookoonu.Games2D;

import java.util.ArrayList;

/**
 * Created by cowell on 9/21/15.
 */
public class Flock {
    ArrayList<Boid> boids; // An ArrayList for all the boids

    Flock() {
        boids = new ArrayList<Boid>(); // Initialize the ArrayList
    }

    void run() {
        for (Boid b : boids) {
            b.run(boids);  // Passing the entire list of boids to each boid individually
        }
    }

    void addBoid(Boid b) {
        boids.add(b);
    }
}
