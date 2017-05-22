package com.dookoonu.animationframework;

import android.os.SystemClock;

/**
 * Created by cowell on 7/17/15.
 */
public class AnimationPanel3D {
    private int FPS = 5;
    private final int convfac = 2000000000;
    private volatile long frameCount;
    private AnimationThread animator;
    private AnimationLoop3D animationLoop3D;

    public interface AnimationLoop3D {
        public void gameUpdate(long frameCount);
        public void gameSetup();
        public void gameRender();
    }

    public AnimationPanel3D(AnimationLoop3D animationLoop3D) {
        this.animationLoop3D = animationLoop3D;
    }

    public void init() {
        animator = new AnimationThread(convfac / FPS);
    }

    /**
     * initialize and start the animation thread
     */
    public void startGame() {
        if (animator == null) {
            animator = new AnimationThread(convfac / FPS);
        }
        //if ( !animator.isRunning()) {
        animator.start();
        //}
    }

    public void stopGame() {
        animator.stopGame();
    }

    public void pauseGame() {
        animator.pauseGame();
    }

    public void resumeGame() {
        animator.resumeGame();
    }
    public void setFPS(int FPS) {
        this.FPS = FPS;
        if(animator!=null) {
            animator.setFPS(FPS);
        }
    }

    private class AnimationThread extends Thread {
        private static final int MAX_FRAME_SKIPS = 5;
        private boolean running = true; // running by default
        private static final int NUM_DELAYS_PER_YIELD = 16;
        private long beforeTime, afterTime, timeDiff, sleepTime, excess;
        long overSleepTime = 0L;
        int noDelays, period;
        private boolean paused = false;

        public AnimationThread(int i) {
            period = i;
        }

        public void stopGame() {
            running = false;
            paused = true;
        }

        public void pauseGame() {
            paused = true;
        }

        public void resumeGame() {
            paused = false;
        }

        public void setFPS(int framesPerSec) {
            period = convfac / framesPerSec;
        }

        public boolean isRunning() {
            return running;
        }

        public boolean isPaused() {
            return paused;
        }


        /**
         * Repeatedly update, render, sleep so loop takes close
         * to period nsecs. Sleep inaccuracies are handled.
         */
        public void run() {
            if (animationLoop3D != null) {
                animationLoop3D.gameSetup();
                beforeTime = SystemClock.elapsedRealtimeNanos();
                while (running) {
                    while (!paused) {
                        frameCount++;
                        animationLoop3D.gameUpdate(frameCount);
                        animationLoop3D.gameRender();

                        afterTime = SystemClock.elapsedRealtimeNanos();
                        timeDiff = afterTime - beforeTime;
                        sleepTime = (period - timeDiff) - overSleepTime;
                        //Log.w("SLeep",Long.toString(overSleepTime) + " " +Long.toString(period) + " " + Long.toString(sleepTime) + " " + Long.toString(timeDiff));
                        if (sleepTime > 0) {//some time left in this cycle
                            try {
                                Thread.sleep(sleepTime / 1000000L); //nano to ms
                            } catch (InterruptedException ex) {
                                overSleepTime = (SystemClock.elapsedRealtimeNanos() - afterTime) - sleepTime;
                            }
                        } else {//sleepTime<=0; frame took longer than the period
                            excess -= sleepTime; //store excess time value
                            overSleepTime = 0L;
                            if (++noDelays >= NUM_DELAYS_PER_YIELD) {
                                Thread.yield(); //give another thread a chance to run
                                noDelays = 0;
                            }
                        }
                        beforeTime = SystemClock.elapsedRealtimeNanos();
                        /*
                    if frame animation is taking too long, update the game state
                    without rendering it, to get the updates/sec closer to the
                    required FPS.
                     */
                        int skips = 0;
                        while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
                            excess -= period;
                            frameCount++;
                            animationLoop3D.gameUpdate(frameCount); //update game, but don't render.
                            skips++;
                        }
                    }
                }
            }
        }
    }
}
