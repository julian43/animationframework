package com.dookoonu.animationframework;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;


import static com.dookoonu.animationframework.AnimationPanel.AnimationLoop;
import static java.lang.Math.sqrt;

/**
 * Created by cowell on 7/10/15.
 */
public abstract class GameView extends SurfaceView implements SurfaceHolder.Callback,
        AnimationLoop, View.OnTouchListener{
    public final int CENTER = 0, RADIUS =1, CORNER = 2, CORNERS = 3;
    private Activity activity;
    private boolean dialogIsDisplayed = false;
    protected Canvas surfaceCanvas;
    protected Paint generalPaint, textPaint, strokePaint;
    protected Picture picture;
    protected int surfaceWidth, surfaceHeight;
    protected AnimationPanel animationPanel;
    protected Random random;
    private boolean strokeSet;
    private boolean fillSet;
    private Stack<Object> stack;
    private float[] vertices;
    private int numVertices = 100;
    private int pcount;
    protected long frameCount;
    protected Boolean downloadComplete;

    protected ArrayList<Bitmap> images;
    protected static final float PI = (float)Math.PI;
    protected static final float HALF_PI = (float)Math.PI/2.0f;
    protected static final float TWO_PI = (float)Math.PI * 2.0f;
    private Canvas.VertexMode vertexMode;
    private Path vPath;
    private Matrix shapeMat;
    private boolean drawingShape = false;
    private RectF abounds, ebounds, bounds, rbounds;
    private int emode = CENTER, rmode = CORNERS;
    private int ewidth, eheight;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        animationPanel = new AnimationPanel(getHolder(), (AnimationPanel.AnimationLoop)this);
        activity = (Activity) context;
        generalPaint = new Paint();
        textPaint = new Paint();
        strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(20.0f);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setColor(Color.WHITE);

        generalPaint.setColor(Color.BLUE);
        generalPaint.setStyle(Paint.Style.FILL);
        //generalPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        picture = new Picture();
        vertices = new float[numVertices * 2];
        random = new Random();
        random.setSeed(SystemClock.uptimeMillis());
        vertexMode = Canvas.VertexMode.TRIANGLE_STRIP;
        vPath = new Path();
        shapeMat = new Matrix();
        //pApplet = new PApplet();

        ebounds = new RectF();
        abounds = new RectF();
        bounds = new RectF();
        rbounds = new RectF();
        setOnTouchListener(this);
    }

    protected void showGameDialog(final int messageId, final String message, final String title,
                                  final String positiveBtnText) {
        // DialogFragment to display quiz stats and start new quiz
        final DialogFragment gameDialog = new DialogFragment(){
            //create an AlertDialog and return it
            public Dialog onCreateDialog(Bundle bundle){
                //create dialog displaying String resource for messageId
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getResources().getString(messageId));

                //display number of shots fired and total time elapsed
                builder.setMessage(message);
                builder.setPositiveButton(positiveBtnText, new DialogInterface.OnClickListener() {
                    //called when "Reset Game" button is pressed
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogIsDisplayed = false;
                        newGame();
                    }
                });

                return builder.create(); //return the alert dialog
            }
        };

        //In GUI thread, use FragmentManager to display the DialogFragment
        activity.runOnUiThread(new Runnable(){

            @Override
            public void run() {
                dialogIsDisplayed = true;
                gameDialog.setCancelable(false);//modal dialog
                gameDialog.show(activity.getFragmentManager(), title);
            }});
    }

    protected void newGame() {

    }

    /**
     *
     * @param background color value
     */
    public final void background(int background) {
        surfaceCanvas.drawColor(background, PorterDuff.Mode.SRC);
       //surfaceCanvas.drawRGB(55,44,123);
    }

    /**
     *
     * @param background
     * @param mode
     */
    public final void background(int background, PorterDuff.Mode mode) {
        surfaceCanvas.drawColor(background, mode);
    }

    public final void circle(float x, float y, float radius){
        surfaceCanvas.drawCircle(x,y, radius, generalPaint);
        if(strokeSet){
            surfaceCanvas.drawCircle(x, y, radius, strokePaint);
        }
        if(fillSet)
            surfaceCanvas.drawCircle(x, y, radius, generalPaint);
    }

    /**
     *
     * @param rmode
     */
    public final void rectMode(int rmode){
        this.rmode = rmode;
    }

    /**
     *
     * @param a
     * @param b
     * @param c
     * @param d
     */
    public final void rect(float a, float b, float c, float d){
        switch (rmode) {
            case CENTER:
                ehh = (int) (d/2);
                ehw = (int) (c/2);
                rbounds.set(a - ehw, b - ehh, a + ehw, b + ehh);
                break;
            case RADIUS:
                rbounds.set(a - c, b - d, a + c, b + d);
                break;
            case CORNER:
                rbounds.set(a,b,a + c, b + d);
                break;
            case CORNERS:
            default:
                rbounds.set(a,b,c,d);
        }
        if(strokeSet){
            surfaceCanvas.drawRect(rbounds, strokePaint);
        }
        if(fillSet)
            surfaceCanvas.drawRect(rbounds, generalPaint);
    }

    /**
     *
     * @param a
     * @param b
     * @param c
     * @param d
     * @param rx
     * @param ry
     */
    public final void roundRect(float a, float b, float c, float d, float rx, float ry){
        switch (rmode) {
            case CENTER:
                ehh = (int) (d/2);
                ehw = (int) (c/2);
                rbounds.set(a - ehw, b - ehh, a + ehw, b + ehh);
                break;
            case RADIUS:
                rbounds.set(a - c, b - d, a + c, b + d);
                break;
            case CORNER:
                rbounds.set(a,b,a + c, b + d);
                break;
            case CORNERS:
            default:
                rbounds.set(a,b,c,d);
        }
        if(strokeSet){
            surfaceCanvas.drawRoundRect(rbounds, rx, ry, strokePaint);
        }
        if(fillSet)
            surfaceCanvas.drawRoundRect(rbounds, rx, ry, generalPaint);
    }

    public final void arc(float a, float b, float c, float d, float start, float sweep, boolean useCenter){
        //RectF bounds = new RectF(a, b, c, d);
        abounds.set(a,b,c,d);
        if(strokeSet){
            surfaceCanvas.drawArc(abounds, start, sweep,useCenter,strokePaint);
        }
        if(fillSet)
        surfaceCanvas.drawArc(abounds, start, sweep,useCenter,generalPaint);
    }


    public final void ellipseMode(int mode){
        emode = mode;
    }

    int ehw, ehh;
    /**
     * draw an ellipse. Parameters are interpreted according to mode
     * CENTER a,b are the center; c,d are the width and height
     * RADIUS a,b are the center; c,d are the half width and half height
     * CORNER a,b are the upper left corner; c,d are the width and height
     * CORNERS parameters represent the bounding box (topx,topy, btmx, btmy)
     * @param a
     * @param b
     * @param c
     * @param d
     */
    public final void ellipse(float a, float b, float c, float d){
        //RectF bounds = new RectF(a, b, c, d);
        //left top right bottom
        switch (emode) {
            case CENTER:
                ehh = (int) (d/2);
                ehw = (int) (c/2);
                ebounds.set(a - ehw, b - ehh, a + ehw, b + ehh);
                break;
            case RADIUS:
                ebounds.set(a - c, b - d, a + c, b + d);
                break;
            case CORNER:
                ebounds.set(a,b,a + c, b + d);
                break;
            case CORNERS:
            default:
                ebounds.set(a,b,c,d);
        }

        if(strokeSet){
            surfaceCanvas.drawOval(ebounds, strokePaint);
        }
        if(fillSet)
            surfaceCanvas.drawOval(ebounds, generalPaint);
    }

    public final void line(float startX, float startY, float stopX, float stopY){
        surfaceCanvas.drawLine(startX, startY, stopX, stopY, strokePaint);
    }

    /**
     * Draw a series of lines. Each line is taken from 4 consecutive values in the pts array.
     * Thus to renderFrame 1 line, the array must contain at least 4 values.
     * This is logically the same as drawing the array as follows:
     * drawLine(pts[0], pts[1], pts[2], pts[3]) followed by drawLine(pts[4], pts[5], pts[6], pts[7])
     * and so on.
     * @param pts Array of points to renderFrame [x0 y0 x1 y1 x2 y2 ...]
     * @param offset Number of values in the array to skip before drawing.
     * @param count The number of values in the array to process, after skipping "offset" of them.
     *              Since each line uses 4 values, the number of "lines" that are drawn is really
     *              (count >> 2).
     */
    public final void lines(float[] pts, int offset, int count){
        surfaceCanvas.drawLines(pts, offset, count, strokePaint);
    }

    public final void lines(float pts[]){
        surfaceCanvas.drawLines(pts, strokePaint);
    }

    public final void point(float x, float y){
        surfaceCanvas.drawPoint(x, y, generalPaint);
    }

    public final void points(float[] h){
        surfaceCanvas.drawPoints(h, generalPaint);
    }


 /*   public final void beginShape(int vertexCount){
        this.numVertices = vertexCount;
        vertices = new float[numVertices * 2];
        pcount = 0;
    }
*/

    public final void beginShape(Path.FillType fillType){
        beginShape();
        vPath.setFillType(fillType);
    }

    public final void beginShape(){
        //vertices = new float[numVertices * 2];
        //Arrays.fill(vertices,0.0f);
        vPath.rewind();
        pcount = 0;

    }

    public final void endShape(){
        //surfaceCanvas.drawVertices(vertexMode, pcount,vertices,0,null,0,
          //      null,0,null,0,0,generalPaint);
        vPath.close();
        if(strokeSet) surfaceCanvas.drawPath(vPath, strokePaint);
        if(fillSet) surfaceCanvas.drawPath(vPath, generalPaint);

    }

    public final void vertex(float x, float y){
        if(pcount++==0){
            vPath.moveTo(x,y);
        }else {
            vPath.lineTo(x,y);
        }
        //vertices[pcount++] = x;
        //vertices[pcount++] = y;

    }

    public void setNumVertices(int numVertices) {
        this.numVertices = numVertices;
    }

    public final void translate(float x, float y){
        surfaceCanvas.translate(x,y);
    }

    public final void rotate(float degrees){
        surfaceCanvas.rotate(degrees);
    }

    public final void rotate(float degrees, float pivotx, float pivoty){
        surfaceCanvas.rotate(degrees, pivotx, pivoty);
    }

    public final void shear(float x, float y){
        surfaceCanvas.skew(x,y);
    }

    public final void text(String string, float x, float y){
        surfaceCanvas.drawText(string,x,y,textPaint);
    }

    public final void textSize(float size){
        textPaint.setTextSize(size);
    }

    /**
     *
     * @param color color int value
     */
    public final void textColor(int color){
        textPaint.setColor(color);
    }

    /**
     * Sets the width of the pen
     * @param width pen width
     */
    public final void strokeWidth(float width){
        strokePaint.setStrokeWidth(width);
    }

    /**
     *
     * @param color color int value
     */
    public final void fill(int color){
        generalPaint.setColor(color);
        fillSet = true;
    }

    /**
     *
     * @param color color int value
     * @param style
     */
    public final void fill(int color, Paint.Style style){
        fill(color);
        generalPaint.setStyle(style);
    }

    /**
     *
     * @param r
     * @param g
     * @param b
     * @param a
     */
    public final void fill(int r, int g, int b, int a){
        fillSet = true;
        generalPaint.setARGB(a, r, g, b);
    }

    /**
     * Deactivates fill.
     */
    public final void noFill(){
        fillSet = false;
    }

    /**
     * Activates the pen and sets its color.
     * @param c the color returned by the color method
     */
    public final void stroke(int c){
        strokePaint.setColor(c);
        strokeSet = true;
    }

    /**
     * Deactivates the pen.
     */
    public final void noStroke(){
        strokeSet = false;
    }

    /**
     *
     * @param r red value
     * @param g green value
     * @param b blue value
     * @return
     */
    public final int color(int r, int g, int b){
        return Color.rgb(r, g, b);
    }

    /**
     * Return a color-int from alpha, red, green, blue amd alpha components.
     * @param r red value
     * @param g green value
     * @param b blue value
     * @param a alpha value
     * @return color value
     */
    public final int color(int r, int g, int b, int a){
        return Color.argb(a, r, g, b);
    }

    /**
     * Return a color-int from alpha, red, green, blue components.
     * @param c The greyscale color value (0..255)
     * @param alpha The alpha value
     * @return color value
     */
    public final int color(int c, int alpha){
        return Color.argb(alpha, c, c, c);
    }

    /**
     *
     * @param c
     * @return color value
     */
    public final int color(int c){
        return Color.rgb(c,c,c);
    }

    /**
     *  Return the alpha component of a color int. This is the same as saying
     * color >>> 24
     * @param color color int value
     * @return
     */
    public final int alpha(int color){
        return Color.alpha(color);
    }

    /**
     *  Return the red component of a color int. This is the same as saying
     * (color >> 16) & 0xFF
     * @param color
     * @return
     */
    public final int red(int color){
        return Color.red(color);
    }

    /**
     * Return the green component of a color int. This is the same as saying
     * (color >> 8) & 0xFF
     * @param color
     * @return
     */
    public final int green(int color){
        return Color.green(color);
    }

    /**
     * Return the blue component of a color int. This is the same as saying
     * color & 0xFF
     * @param color
     * @return
     */
    public final  int blue(int color){
        return Color.blue(color);
    }

    public final void saveState(){
        surfaceCanvas.save();
    }

    public final void restoreState(){
        surfaceCanvas.restore();
    }
/*
    public float noise(float x){
        return pApplet.noise(x);
    }

    public float noise(float x, float y) {
        return  pApplet.noise(x, y);
    }

    public float noise(float x, float y, float z) {
        return pApplet.noise(x, y, z);
    }

    public void noiseDetail(int lod) {
       pApplet.noiseDetail(lod);
    }

    public void noiseDetail(int lod, float falloff) {
       pApplet.noise(lod, falloff);
    }

    public void noiseSeed(long seed) {
       pApplet.noiseSeed(seed);
    }
*/

    /**
     * Image Routines
     */

    public final Bitmap loadImage(int resid, int width, int height){
        return Utils.decodeSampledBitmapFromResource(getResources(), resid, 100, 100);
    }

    /**
     *
     * @param urls location of images on network
     */
    public final void loadImage(String...urls){
        images = new ArrayList<>();
        downloadComplete = false;
        new DownloadImagesTask(images, downloadComplete).execute(urls);
    }

    /**
     *
     * @param bitMap
     * @param left location
     * @param top  location
     */
    public final void image(Bitmap bitMap,float left, float top){
        surfaceCanvas.drawBitmap(bitMap, left, top, null);
    }

    /**
     * Place image on screen
     * @param which image (integer position)  in the array images
     * @param left location
     * @param top location
     */
    public final void image(int which, float left, float top){
        //Log.w("Image", Boolean.toString(downloadComplete)+" " + images.size());
        if(images.size()-1>=which){
            surfaceCanvas.drawBitmap(images.get(which),left,top,null);
        }
    }

    /**
     * place image on screen
     * @param which image (integer position)  in the array images
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public final void image(int which, float left, float top, float right, float bottom){
        //Log.w("Image", Boolean.toString(downloadComplete)+" " + images.size());
        if(images.size()-1>=which){
            RectF rectF = new RectF(left,top, right, bottom);
            surfaceCanvas.drawBitmap(images.get(which),null, rectF, null);
        }
    }

    /**
     * Math Routines inspired by processing
     */

    /**
     * keep a floating point value between two extrema
     * @param amt value to adjust
     * @param low lowest value
     * @param high highest value
     * @return a constrained floating point value
     */
    public final float constrain(float amt, float low, float high){
        if(amt<=low){
            return low;
        }else if(amt>=high){
            return high;
        }
        return amt;
    }

    /**
     * keep an integer value between two extrema
     * @param amt value to adjust
     * @param low lowest value
     * @param high highest value
     * @return a constrained integer value
     */
    public final int constrain(int amt, int low, int high){
        if(amt<=low){
            return low;
        }else if(amt>=high){
            return high;
        }
        return amt;
    }

    /**
     *
     * @param value
     * @param start1
     * @param stop1
     * @param start2
     * @param stop2
     * @return
     */
    public final float map(float value, float start1, float stop1, float start2, float stop2){
        return norm(value,start1,stop1) * (stop2 - start2) + start2;
    }

    /**
     *
     * @param value
     * @param start
     * @param stop
     * @return
     */
    public final float norm(float value, float start, float stop){
        return (stop-value)/(stop-start);
    }

    /**
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public final float dist(float x1, float y1, float x2, float y2) {
        float xdiff = x2 - x1;
        float ydiff = y2 - y1;

        return (float) sqrt(xdiff * xdiff + ydiff * ydiff);
    }

    /**
     *
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @return
     */
    public final float dist(float x1, float y1, float z1, float x2, float y2, float z2) {
        float xdiff = x2 - x1;
        float ydiff = y2 - y1;
        float zdiff = z2 - z1;

        return (float) sqrt(xdiff*xdiff + ydiff * ydiff + zdiff * zdiff);
    }

    /**
     *
     * @param values
     * @return
     */
    public final float min(float ... values){
        float min = values[0];
        if(values.length > 1){
            for(float value : values){
                if(value < min) min = value;
            }
        }
        return min;
    }

    /**
     *
     * @param values
     * @return
     */
    public final float max(float ... values){
        float max = values[0];
        if(values.length > 1){
            for(float value : values){
                if(value > max) max = value;
            }
        }
        return max;
    }

    /**
     *
     * @param start
     * @param stop
     * @param amt
     * @return
     */
    public final float lerp(float start, float stop, float amt) {
       return norm(amt,start, stop) * (stop - start) + start;
    }

  //  public int lerpColor(int c1, int c2, int amt, int mode){
    //    return PApplet.lerpColor(c1, c2,amt, mode);
    //}

    /**
     * game specific routines
     */

    /**
     *
     * @param canvas
     */
    public final void gameSetup(Canvas canvas){
        surfaceCanvas = picture.beginRecording(surfaceWidth, surfaceHeight);
        initialize();
        picture.endRecording();
        canvas.drawPicture(picture);
    }

    /**
     *
     * @param frameCount
     */
    public final void gameUpdate(long frameCount) {
        surfaceCanvas = picture.beginRecording(surfaceWidth, surfaceHeight);
        this.frameCount = frameCount;
        renderFrame();
        picture.endRecording();
    }

    public int getSurfaceWidth() {
        return surfaceWidth;
    }

    public int getSurfaceHeight() {
        return surfaceHeight;
    }

    /**
     *
     * @param canvas
     */
    public final void gameRender(Canvas canvas) {
        canvas.drawPicture(picture);
        //now reset
        noStroke();
    }

    /**
     * endless animation loop
     */
    public final void loop(){
        animationPanel.resumeGame();
    }

    /**
     * stop frame animation
     */
    public final void noLoop(){
        animationPanel.pauseGame();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.w("Ani", "surface created");
        animationPanel.init();
        animationPanel.startGame();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        surfaceWidth = width;
        surfaceHeight = height;
        //Log.w("Ani","surface changed " +  Integer.toString(width) + " " + Integer.toString(height));
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.w("Ani","surface destroyed");
        animationPanel.stopGame();
    }

    /**
     * set the frame rate; default is a slooow 5 fps
     * @param fps frames per second
     */
    public final void frameRate(int fps){
        animationPanel.setFPS(fps);
    }

    public boolean onTouch(View v, MotionEvent event) {
        return super.onTouchEvent(event);
    }

    /**
     * called every tick of the animation clock
     */
    protected abstract void renderFrame();
    protected abstract void initialize();
}
