package com.dookoonu.animationframework;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

/**
 * Created by cowell on 7/17/15.
 */
public class Game3DRenderer implements GLSurfaceView.Renderer  {
    GameView3D gameView3D;

    public Game3DRenderer(GameView3D gameView3D) {
        this.gameView3D = gameView3D;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        /*
         * By default, OpenGL enables features that improve quality
         * but reduce performance. One might want to tweak that
         * especially on software renderer.
         */
       /* if(checkIfContextSupportsFrameBufferObject(gl)){
            Log.i("Surface","yes");
        }else {
            Log.i("Surface","NO FrameBuffer");
        }*/
        gl.glDisable(GL10.GL_DITHER);

        /*
         * Some one-time OpenGL initialization can be made here
         * probably based on features of this particular context
         */
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                GL10.GL_FASTEST);

        gl.glClearColor(.5f, .5f, .5f, 1);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glActiveTexture(GL10.GL_TEXTURE0);
      //  int mTargetTexture = createTargetTexture(gl, 256, 256);
       // int mFramebuffer = createFrameBuffer(gl, 256, 256, mTargetTexture);

        gameView3D.startGame();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        gameView3D.setWidth(width);
        gameView3D.setHeight(height);
        gl.glViewport(0, 0, width, height);

        /*
         * Set our projection matrix. This doesn't have to be done
         * each time we renderFrame, but usually a new projection needs to be set
         * when the viewport is resized.
         */

        float ratio = (float)width / height;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);

        /*
         * By default, OpenGL enables features that improve quality
         * but reduce performance. One might want to tweak that
         * especially on software renderer.
         */
        gl.glDisable(GL10.GL_DITHER);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glActiveTexture(GL10.GL_TEXTURE0);

    }


    @Override
    public void onDrawFrame(GL10 gl) {
        gameView3D.setGL(gl);
        if(gameView3D.isInitialized()){

             /*
         * By default, OpenGL enables features that improve quality
         * but reduce performance. One might want to tweak that
         * especially on software renderer.
         */
            gl.glDisable(GL10.GL_DITHER);
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                    GL10.GL_FASTEST);
            //gl.glShadeModel(GL10.GL_SMOOTH);
            gl.glEnable(GL10.GL_DEPTH_TEST);

          //  gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
            //        GL10.GL_MODULATE);

        /*
         * Usually, the first thing one might want to do is to clear
         * the screen. The most efficient way of doing this is to use
         * glClear().
         */

            //gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        /*
         * Now we're ready to draw some 3D objects
         */

        /*    gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();

            GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);*/
            //gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);


 /*
         * Usually, the first thing one might want to do is to clear
         * the screen. The most efficient way of doing this is to use
         * glClear(). However we must make sure to set the scissor
         * correctly first. The scissor is always specified in window
         * coordinates:
         */

          //  gl.glClearColor(0.5f,0.5f,0.5f,1);
         //   gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        /*
         * Now we're ready to draw some 3D object
         */

/*
            gl.glColor4f(0.7f, 0.7f, 0.7f, 1.0f);

            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
            gl.glEnable(GL10.GL_CULL_FACE);
            gl.glShadeModel(GL10.GL_SMOOTH);
            gl.glEnable(GL10.GL_DEPTH_TEST);

*/


            gameView3D.renderFrame();
        }else {
            gameView3D.initialize();
            gameView3D.setInitialized(true);
        }

    }

    private boolean checkIfContextSupportsFrameBufferObject(GL10 gl) {
        return checkIfContextSupportsExtension(gl, "GL_OES_framebuffer_object");
    }

    private boolean checkIfContextSupportsExtension(GL10 gl, String extension) {
        String extensions = " " + gl.glGetString(GL10.GL_EXTENSIONS) + " ";
        // The extensions string is padded with spaces between extensions, but not
        // necessarily at the beginning or end. For simplicity, add spaces at the
        // beginning and end of the extensions string and the extension string.
        // This means we can avoid special-case checks for the first or last
        // extension, as well as avoid special-case checks when an extension name
        // is the same as the first part of another extension name.
        return extensions.indexOf(" " + extension + " ") >= 0;
    }

    private int createFrameBuffer(GL10 gl, int width, int height, int targetTextureId) {
        GL11ExtensionPack gl11ep = (GL11ExtensionPack) gl;
        int framebuffer;
        int[] framebuffers = new int[1];
        gl11ep.glGenFramebuffersOES(1, framebuffers, 0);
        framebuffer = framebuffers[0];
        gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, framebuffer);

        int depthbuffer;
        int[] renderbuffers = new int[1];
        gl11ep.glGenRenderbuffersOES(1, renderbuffers, 0);
        depthbuffer = renderbuffers[0];

        gl11ep.glBindRenderbufferOES(GL11ExtensionPack.GL_RENDERBUFFER_OES, depthbuffer);
        gl11ep.glRenderbufferStorageOES(GL11ExtensionPack.GL_RENDERBUFFER_OES,
                GL11ExtensionPack.GL_DEPTH_COMPONENT16, width, height);
        gl11ep.glFramebufferRenderbufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
                GL11ExtensionPack.GL_DEPTH_ATTACHMENT_OES,
                GL11ExtensionPack.GL_RENDERBUFFER_OES, depthbuffer);

        gl11ep.glFramebufferTexture2DOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
                GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES, GL10.GL_TEXTURE_2D,
                targetTextureId, 0);
        int status = gl11ep.glCheckFramebufferStatusOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES);
        if (status != GL11ExtensionPack.GL_FRAMEBUFFER_COMPLETE_OES) {
            throw new RuntimeException("Framebuffer is not complete: " +
                    Integer.toHexString(status));
        }
        gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);
        return framebuffer;
    }

    private int createTargetTexture(GL10 gl, int width, int height) {
        int texture;
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        texture = textures[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
        gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, width, height, 0,
                GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, null);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_REPEAT);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_REPEAT);
        ;            return texture;
    }
}
