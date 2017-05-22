package com.dookoonu.geometry;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;

import com.dookoonu.animationframework.GameView3D;
import com.dookoonu.animationframework.R;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by cowell on 7/6/15.
 */
public class Triangle {
    private final FloatBuffer mFVertexBuffer;
    private final FloatBuffer mTexBuffer;
    private final ShortBuffer mIndexBuffer;
    //private final int mProgram;
    private FloatBuffer vertexBuffer;
    private GameView3D gameView3D;

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
    private int mPositionHandle;
    private int vertexStride = 3;
    private int mColorHandle;
    private int vertexCount = 3;
    private final static int VERTS = 3;
    private int mTextureID;

    public Triangle(GameView3D gameView3D) {

        this.gameView3D = gameView3D;

        // Buffers to be passed to gl*Pointer() functions
        // must be direct, i.e., they must be placed on the
        // native heap where the garbage collector cannot
        // move them.
        //
        // Buffers with multi-byte datatypes (e.g., short, int, float)
        // must have their byte order set to native order

        ByteBuffer vbb = ByteBuffer.allocateDirect(VERTS * 3 * 4);
        vbb.order(ByteOrder.nativeOrder());
        mFVertexBuffer = vbb.asFloatBuffer();

        ByteBuffer tbb = ByteBuffer.allocateDirect(VERTS * 2 * 4);
        tbb.order(ByteOrder.nativeOrder());
        mTexBuffer = tbb.asFloatBuffer();

        ByteBuffer ibb = ByteBuffer.allocateDirect(VERTS * 2);
        ibb.order(ByteOrder.nativeOrder());
        mIndexBuffer = ibb.asShortBuffer();

        // A unit-sided equalateral triangle centered on the origin.
        float[] coords = {
                // X, Y, Z
                -0.5f, -0.25f, 0,
                0.5f, -0.25f, 0,
                0.0f,  0.559016994f, 0
        };

        for (int i = 0; i < VERTS; i++) {
            for(int j = 0; j < 3; j++) {
                mFVertexBuffer.put(coords[i*3+j] * 2.0f);
            }
        }

        for (int i = 0; i < VERTS; i++) {
            for(int j = 0; j < 2; j++) {
                mTexBuffer.put(coords[i*3+j] * 2.0f + 0.5f);
            }
        }

        for(int i = 0; i < VERTS; i++) {
            mIndexBuffer.put((short) i);
        }

        mFVertexBuffer.position(0);
        mTexBuffer.position(0);
        mIndexBuffer.position(0);
    }

    public void setupTex(Context mContext){
        int[] textures = new int[1];
        GL10 gl10 = gameView3D.getSurfaceGL();
        if(gl10==null){
            return;
        }
        gl10.glGenTextures(1, textures, 0);

        mTextureID = textures[0];
        gameView3D.initTexture(mTextureID);

        InputStream is = mContext.getResources()
                .openRawResource(R.raw.alberti5);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch(IOException e) {
                // Ignore.
            }
        }

        gameView3D.setTextureMap(bitmap);
        bitmap.recycle();

    }

    public void draw() {
        GL10 gl10 = gameView3D.getSurfaceGL();
        gl10.glDisable(GL10.GL_CULL_FACE);
        //texture
        gameView3D.enableTexture(mTextureID, mTexBuffer);
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        //gl.glTranslatef(0.0f,0.0f,0.6f+mAngle/10.0f);
        ///gl10.glRotatef(mAngle, 0.5f, 0, 1.0f);
        //gameView3D.camera();//called before any modification of space (e.g. rotation) and before actual draw
        gameView3D.saveState();
        gameView3D.rotateZ(angle);
        gameView3D.drawUsingTexture(mFVertexBuffer,VERTS,mIndexBuffer);
        gameView3D.restoreState();
        gameView3D.disableTexture();

    }
}
