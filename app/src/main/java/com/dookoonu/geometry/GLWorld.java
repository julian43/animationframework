/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dookoonu.geometry;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import com.dookoonu.animationframework.GameView3D;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES10.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES10.GL_DITHER;
import static android.opengl.GLES10.GL_LINEAR;
import static android.opengl.GLES10.GL_NEAREST;
import static android.opengl.GLES10.GL_REPEAT;
import static android.opengl.GLES10.GL_REPLACE;
import static android.opengl.GLES10.GL_TEXTURE0;
import static android.opengl.GLES10.GL_TEXTURE_2D;
import static android.opengl.GLES10.GL_TEXTURE_COORD_ARRAY;
import static android.opengl.GLES10.GL_TEXTURE_ENV;
import static android.opengl.GLES10.GL_TEXTURE_ENV_MODE;
import static android.opengl.GLES10.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES10.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES10.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES10.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES10.glActiveTexture;
import static android.opengl.GLES10.glBindTexture;
import static android.opengl.GLES10.glDisable;
import static android.opengl.GLES10.glEnable;
import static android.opengl.GLES10.glEnableClientState;
import static android.opengl.GLES10.glTexCoordPointer;
import static android.opengl.GLES10.glTexEnvf;
import static android.opengl.GLES10.glTexParameterf;
import static android.opengl.GLES10.glTexParameterx;
import static android.opengl.GLES10.glVertexPointer;

public class GLWorld {
    public static final int SHADE_ALPHA = GL10.GL_ALPHA;
    public static final int SHADE_AMBIENT = GL10.GL_AMBIENT;
    public static final int SHADE_SMOOTH = GL10.GL_SMOOTH;
    public static final int SHADE_LUMINANCE_ALPHA = GL10.GL_LUMINANCE_ALPHA;
    public static final int SHADE_LUMINANCE = GL10.GL_LUMINANCE;
    public static final int SHADE_FOG = GL10.GL_FOG;

    private int shadeMode = SHADE_ALPHA;
    private int drawMode = GL10.GL_TRIANGLES;
    private boolean textureEnabled;
    private int mTextureID;

    public void addShape(GLShape shape) {
		mShapeList.add(shape);
		mIndexCount += shape.getIndexCount();
	}
	
	public void generate() {		
	    ByteBuffer bb = ByteBuffer.allocateDirect(mVertexList.size() * 4 * 4);
	    bb.order(ByteOrder.nativeOrder());
		mColorBuffer = bb.asIntBuffer();

	    bb = ByteBuffer.allocateDirect(mVertexList.size() * 4 * 3);
	    bb.order(ByteOrder.nativeOrder());
	    mVertexBuffer = bb.asFloatBuffer();

	    bb = ByteBuffer.allocateDirect(mIndexCount * 2);
	    bb.order(ByteOrder.nativeOrder());
	    mIndexBuffer = bb.asShortBuffer();

		Iterator<GLVertex> iter2 = mVertexList.iterator();
		while (iter2.hasNext()) {
			GLVertex vertex = iter2.next();
			vertex.put(mVertexBuffer, mColorBuffer);
		}

		Iterator<GLShape> iter3 = mShapeList.iterator();
		while (iter3.hasNext()) {
			GLShape shape = iter3.next();
			shape.putIndices(mIndexBuffer);
		}
	}
	
	public GLVertex addVertex(float x, float y, float z) {
		GLVertex vertex = new GLVertex(x, y, z, mVertexList.size());
		mVertexList.add(vertex);
		return vertex;
	}
	
	public void transformVertex(GLVertex vertex, M4 transform) {
		vertex.update(mVertexBuffer, transform);
	}

    private void initTexture(GL10 gl){
        glBindTexture(GL_TEXTURE_2D, mTextureID);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
                GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D,
                GL_TEXTURE_MAG_FILTER,
                GL_LINEAR);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
                GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T,
                GL_CLAMP_TO_EDGE);

        glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE,
                GL_REPLACE);
    }

    private void setTextureMap(Bitmap bitmap){
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
    }

    private final void enableTexture(GL10 surfaceGL){
        surfaceGL.glEnable(GL10.GL_TEXTURE_2D);
        surfaceGL.glActiveTexture(GL10.GL_TEXTURE0);
        surfaceGL.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);
        surfaceGL.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);
        surfaceGL.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);

        surfaceGL.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_CLAMP_TO_EDGE);

        surfaceGL.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
                GL10.GL_REPLACE);

        /*
        surfaceGL.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_REPEAT);
        surfaceGL.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_REPEAT);//param

        */

        //surfaceGL.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexBuffer);
    }

    public final void disableTexture(GL10 surfaceGL){
        surfaceGL.glDisable(GL10.GL_TEXTURE_2D);
        textureEnabled = false;
    }

    /**
     *
     * @param gameView3D the current gameView context
     * @param bitmap Bitmap to be used as a texture
     */
    public void setupTex(GameView3D gameView3D, Bitmap bitmap){
        int[] textures = new int[1];
        GL10 gl10 = gameView3D.getSurfaceGL();
        if(gl10==null){
            return;
        }
        gl10.glGenTextures(1, textures, 0);
        mTextureID = textures[0];
        initTexture(gl10);
        setTextureMap(bitmap);
       // enableTexture(gl10);

       // GLUtils.texImage2D(GL11ExtensionPack.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0,  //+face in 1st param
          //      bitmap, 0);


        textureEnabled = true;

    }

    /**
     * Draw all objects in this world
     * @param gl
     */
    public void draw(GL10 gl)
    {
		mColorBuffer.position(0);
		mVertexBuffer.position(0);
		mIndexBuffer.position(0);
        glDisable(GL_DITHER);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glFrontFace(GL10.GL_CW);

        if(textureEnabled){
            mTexBuffer.position(0);

            gl.glDisable(GL10.GL_CULL_FACE);

            glEnableClientState(GL_TEXTURE_COORD_ARRAY);

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, mTextureID);
            glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
                    GL_REPEAT);
            glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T,
                    GL_REPEAT);

            glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
            glEnable(GL_TEXTURE_2D);
            glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexBuffer);

        }else{
            gl.glDisable(GL10.GL_TEXTURE_2D);
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
            gl.glEnable(GL10.GL_CULL_FACE);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            //gl.glFrontFace(GL10.GL_CW);
            //gl.glFrontFace(GL10.GL_CCW);
            gl.glShadeModel(shadeMode);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);

            gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuffer);
        }

        gl.glDrawElements(drawMode, mIndexCount, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);


        //be friendly to other drawing routines
     /*   if(textureEnabled){
            gl.glDisable(GL10.GL_TEXTURE_2D);
        }else {
            gl.glDisable(GL10.GL_CULL_FACE);
            gl.glDisable(GL10.GL_SMOOTH);
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        }*/

    }

    public void setDrawMode(int drawMode) {
        this.drawMode = drawMode;
    }

    public void setShadeMode(int shadeMode) {
        this.shadeMode = shadeMode;
    }

    static public float toFloat(int x) {
    	return x/65536.0f;
    }

	private ArrayList<GLShape> mShapeList = new ArrayList<GLShape>();
	private ArrayList<GLVertex> mVertexList = new ArrayList<GLVertex>();
	
	private int mIndexCount = 0;

    private FloatBuffer mVertexBuffer;
    private IntBuffer mColorBuffer;
    private ShortBuffer mIndexBuffer;
    private FloatBuffer mTexBuffer;

    public void setTexBuffer(FloatBuffer textureBuffer) {
        mTexBuffer = textureBuffer;
    }
}
