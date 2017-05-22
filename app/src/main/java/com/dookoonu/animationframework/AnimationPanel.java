package com.dookoonu.animationframework;

import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by cowell on 7/10/15.
 */
public class AnimationPanel {
    private AnimationThread animator;
    private SurfaceHolder surfaceHolder;
    private AnimationLoop animationLoop;
    private int FPS = 5;
    private final int convfac = 2000000000;
    private volatile long frameCount;

    public interface AnimationLoop {
        public void gameUpdate(long frameCount);
        public void gameSetup(Canvas canvas);
        public void gameRender(Canvas canvas);
    }

    public AnimationPanel() {
    }

    public AnimationPanel(SurfaceHolder surfaceHolder, AnimationLoop animationLoop) {
        this.surfaceHolder = surfaceHolder;
        this.animationLoop = animationLoop;
    }

    public AnimationPanel(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }

    public AnimationPanel(AnimationLoop animationLoop) {
        this.animationLoop = animationLoop;
    }

    public void init(){
        animator = new AnimationThread(surfaceHolder, convfac / FPS);
    }

    /**
     * initialize and start the animation thread
     */
    public void startGame() {
        if(animator==null){
            animator = new AnimationThread(surfaceHolder, convfac / FPS);
        }
        //if ( !animator.isRunning()) {
            animator.start();
        //}
    }

    public void stopGame() {animator.stopGame();}
    public void pauseGame(){
        animator.pauseGame();
    }
    public void resumeGame(){
        animator.resumeGame();
    }

    public SurfaceHolder getSurfaceHolder() {
        return surfaceHolder;
    }

    public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }

    public void setFPS(int FPS) {
        this.FPS = FPS;
        if(animator!=null) {
            animator.setFPS(FPS);
        }
    }

    public AnimationLoop getAnimationLoop() {
        return animationLoop;
    }

    public void setAnimationLoop(AnimationLoop animationLoop) {
        this.animationLoop = animationLoop;
    }

    private class AnimationThread extends Thread {
        private static final int MAX_FRAME_SKIPS = 5;
        private SurfaceHolder surfaceHolder; // for manipulating canvas
        private boolean running = true; // running by default
        private static final int NUM_DELAYS_PER_YIELD = 16;
        private long beforeTime, afterTime, timeDiff, sleepTime, excess;
        long overSleepTime = 0L;
        int noDelays, period;
        private boolean paused = false;

        /*
        Number of frames with a delay of 0 ms before the
        animation thread yields to other running threads
         */
        private AnimationThread(SurfaceHolder surfaceHolder, int period) {
            this.surfaceHolder = surfaceHolder;
            this.period = period;

        }
        public void stopGame() {running = false; paused=true;}
        public void pauseGame(){
            paused = true;
        }
        public void resumeGame(){
            paused = false;
        }
        public void setFPS(int framesPerSec){
            period = convfac/framesPerSec;
        }
        public boolean isRunning(){return running;}

        public boolean isPaused() {
            return paused;
        }

        /**
         * Repeatedly update, render, sleep so loop takes close
         * to period nsecs. Sleep inaccuracies are handled.
         */
        @Override
        public void run() {
            if (animationLoop != null) {
                Canvas canvas =  surfaceHolder.lockCanvas(null); // used for drawing
                animationLoop.gameSetup(canvas);
                surfaceHolder.unlockCanvasAndPost(canvas);
                Log.w("Init", Integer.toString(period));
                beforeTime = SystemClock.elapsedRealtimeNanos();
                while (running) {
                    while (!paused) {
                        try {
                            // get Canvas for exclusive drawing from this thread
                            canvas = surfaceHolder.lockCanvas(null);
                            synchronized (surfaceHolder) {
                                if (canvas != null) {
                                    frameCount++;
                                    animationLoop.gameUpdate(frameCount);
                                    animationLoop.gameRender(canvas);
                                } else {
                                    Log.w("ANi", "canvas is null");
                                }

                            }
                        } finally {
                            // do this in a finally so that if an exception is thrown
                            // during the above, we don't leave the Surface in an
                            // inconsistent state
                            // display canvas's contents on the AnmationView
                            // and enable other threads to use the Canvas
                            if (canvas != null) {
                                surfaceHolder.unlockCanvasAndPost(canvas);
                            }
                        }
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
                            if(canvas!=null){
                                frameCount++;
                                animationLoop.gameUpdate(frameCount); //update game, but don't render.
                            }
                            skips++;
                        }
                    }
                }
            }
        }
    }
}
