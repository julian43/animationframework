package com.dookoonu.animationframework;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.AttributeSet;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import static com.dookoonu.animationframework.AnimationPanel3D.AnimationLoop3D;

/**
 * Created by cowell on 7/15/15.
 */
public abstract class GameView3D extends GLSurfaceView implements AnimationLoop3D {
    private final Game3DRenderer gameRenderer;
    protected long frameCount;
    private AnimationPanel3D animationPanel3D;
    private GL10 surfaceGL;
    private boolean initialized = false;
    public int width, height;

    public GameView3D(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Set the Renderer for drawing on the GLSurfaceView
        gameRenderer = new Game3DRenderer(this);
        setRenderer(gameRenderer);
        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        animationPanel3D = new AnimationPanel3D(this);
    }

    public final void startGame() {//iniitalize and start animation loop
        animationPanel3D.init();
        animationPanel3D.startGame();
    }

    public final GL10 getSurfaceGL() {
        return surfaceGL;
    }


    /**
     * drawing routines
     */

    public final void background(float red, float green, float blue, float alpha) {
        surfaceGL.glClearColor(red, green, blue, alpha);
        surfaceGL.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    }

    public final void initTexture(int textureID){
        surfaceGL.glBindTexture(GL10.GL_TEXTURE_2D, textureID);

        surfaceGL.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);
        surfaceGL.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);

        surfaceGL.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_CLAMP_TO_EDGE);
        surfaceGL.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_CLAMP_TO_EDGE);

        surfaceGL.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
                GL10.GL_REPLACE);
    }

    public void setTextureMap(Bitmap bitmap){
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
    }

    public final void enableTexture(int textureID, FloatBuffer texBuffer){
        surfaceGL.glActiveTexture(GL10.GL_TEXTURE0);
        surfaceGL.glBindTexture(GL10.GL_TEXTURE_2D, textureID);
        surfaceGL.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_REPEAT);
        surfaceGL.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_REPEAT);//param

        surfaceGL.glEnable(GL10.GL_TEXTURE_2D);
        surfaceGL.glTexCoordPointer(2, GL10.GL_FLOAT, 0,texBuffer);
    }

    public final void disableTexture(){
        surfaceGL.glDisable(GL10.GL_TEXTURE_2D);
    }

    public final void draw(FloatBuffer vertexBuffer, int noVertices, ShortBuffer indexBuffer){
        surfaceGL.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        surfaceGL.glEnableClientState(GL10.GL_COLOR_ARRAY);
        surfaceGL.glEnable(GL10.GL_CULL_FACE);
        surfaceGL.glShadeModel(GL10.GL_SMOOTH);
        surfaceGL.glEnable(GL10.GL_DEPTH_TEST);

        //ccw is the default
        surfaceGL.glFrontFace(GL10.GL_CCW);
        surfaceGL.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

        surfaceGL.glDrawElements(GL10.GL_TRIANGLE_STRIP, noVertices,
                GL10.GL_UNSIGNED_SHORT, indexBuffer);
    }

    public final void rotateX(float angle){
        surfaceGL.glRotatef(angle, 1.0f, 0.0f, 0.0f);
    }

    public final void rotateY(float angle){
        surfaceGL.glRotatef(angle, 0.0f, 1.0f, 0.0f);
    }

    public final void rotateZ(float angle){
        surfaceGL.glRotatef(angle, 0.0f, 0.0f, 1.0f);
    }

    public final void rotate(float angle, float x, float y, float z){
        surfaceGL.glRotatef(angle, x, y, z);
    }

    public final void scale(float x, float y, float z){
        surfaceGL.glScalef(x, y, z);
    }

    public final void translateX(float x){
        surfaceGL.glTranslatef(x, 0.0f, 0.0f);
    }

    public final void translateY(float y){
        surfaceGL.glTranslatef(0.0f, y, 0.0f);
    }

    public final void translateZ(float z){
        surfaceGL.glTranslatef(0.0f, 0.0f, z);
    }

    public final void translate(float x, float y, float z){
        surfaceGL.glTranslatef(x, y, z);
    }

    public final void saveState() {
        surfaceGL.glPushMatrix();
    }

    public final void restoreState() {
        surfaceGL.glPopMatrix();
    }

    /**
     * game routines for running and setup
     */

    public final void gameUpdate(long frameCount) {
        this.frameCount = frameCount;
        requestRender();
    }

    @Override
    public void gameRender() {//this might not be necessary

    }

    @Override
    public final void gameSetup() {
        initialized = false;
        requestRender();
    }

    public void setGL(GL10 gl) {
        //Log.i("3D", "setting surfaceGL");
        surfaceGL = gl;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public void onPause(){
        super.onPause();
        animationPanel3D.pauseGame();
    }

    public void onResume(){
        super.onResume();
        animationPanel3D.resumeGame();
    }

    public final void frameRate(int fps) {
        animationPanel3D.setFPS(fps);
    }


    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    protected abstract void renderFrame();

    protected abstract void initialize();

    /**
     * sets a default camera and matrix mode
     */
    public final void camera(){
        surfaceGL.glMatrixMode(GL10.GL_MODELVIEW);
        surfaceGL.glLoadIdentity();

        GLU.gluLookAt(surfaceGL, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }


    /**
     *
     * @param eyeX
     * @param eyeY
     * @param eyeZ
     * @param centerX
     * @param centerY
     * @param centerZ
     * @param upX
     * @param upY
     * @param upZ
     */
    public final void camera( float eyeX, float eyeY, float eyeZ,
                              float centerX, float centerY, float centerZ, float upX, float upY,
                              float upZ){

        surfaceGL.glMatrixMode(GL10.GL_MODELVIEW);
        surfaceGL.glLoadIdentity();

        GLU.gluLookAt(surfaceGL, eyeX,eyeY,eyeZ,centerX,centerY,centerZ,upX,upY,upZ);

    }

    /**
     *
     * @param eyeX
     * @param eyeY
     * @param eyeZ
     * @param centerX
     * @param centerY
     * @param centerZ
     * @param upX
     * @param upY
     * @param upZ
     * @param matrixMode
     */
    public final void camera(float eyeX, float eyeY, float eyeZ,
                             float centerX, float centerY, float centerZ, float upX, float upY,
                             float upZ, int matrixMode){

        surfaceGL.glMatrixMode(matrixMode);
        surfaceGL.glLoadIdentity();

        GLU.gluLookAt(surfaceGL, eyeX,eyeY,eyeZ,centerX,centerY,centerZ,upX,upY,upZ);

    }

    /**
     *
     * @param matrixMode
     */
    public final void camera(int matrixMode){
        surfaceGL.glMatrixMode(matrixMode);
        surfaceGL.glLoadIdentity();

        GLU.gluLookAt(surfaceGL, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }

    public final void drawUsingTexture(FloatBuffer vertexBuffer, int noVertices, ShortBuffer indexBuffer) {
        //surfaceGL.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        surfaceGL.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
                GL10.GL_MODULATE);

        surfaceGL.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        surfaceGL.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        surfaceGL.glFrontFace(GL10.GL_CCW);
        surfaceGL.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

        surfaceGL.glDrawElements(GL10.GL_TRIANGLE_STRIP, noVertices,
                GL10.GL_UNSIGNED_SHORT, indexBuffer);
    }
}
