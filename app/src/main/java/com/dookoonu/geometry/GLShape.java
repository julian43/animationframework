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

import com.dookoonu.animationframework.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PVector;

public class GLShape {

    private final M4 scaleTransform;
    public boolean autoAnimate = false;
	public GLShape(GLWorld world) {
		mWorld = world;
        mTransform = new M4();
        mAnimateTransform = new M4();
        mAnimateTransform.setIdentity();
        scaleTransform = new M4();
        scaleTransform.setIdentity();
        mTransform.setIdentity();
	}


    public static FloatBuffer prepareTexBuffer(float[]...coords){

        ByteBuffer tbb = ByteBuffer.allocateDirect(coords.length * 2 * 4);
        tbb.order(ByteOrder.nativeOrder());
        FloatBuffer mTexBuffer = tbb.asFloatBuffer();
        for(float[] coord : coords){
            for(int i =0; i<2; i++){
                mTexBuffer.put(coord[i] * 2.0f + 0.5f);
            }
        }
        mTexBuffer.position(0);
        return mTexBuffer;
    }

    public static FloatBuffer prepareTexBuffer(float[] coords){
        int no_verts = coords.length/3; //assume x,y,z
        ByteBuffer tbb = ByteBuffer.allocateDirect(no_verts * 3 * 4);
        tbb.order(ByteOrder.nativeOrder());
        FloatBuffer mTexBuffer = tbb.asFloatBuffer();
        for (int i = 0; i < no_verts; i++) {
            for(int j = 0; j < no_verts-1; j++) {
                mTexBuffer.put(coords[i*no_verts+j] * 2.0f + 0.5f);
            }
        }
        mTexBuffer.position(0);
        return mTexBuffer;
    }


	public void addFace(GLFace face) {
		mFaceList.add(face);
	}
	
	public void setFaceColor(int face, GLColor color) {
		mFaceList.get(face).setColor(color);
	}
			
	public void putIndices(ShortBuffer buffer) {
		Iterator<GLFace> iter = mFaceList.iterator();
		while (iter.hasNext()) {
			GLFace face = iter.next();
			face.putIndices(buffer);
		}		
	}
	
	public int getIndexCount() {
		int count = 0;
		Iterator<GLFace> iter = mFaceList.iterator();
		while (iter.hasNext()) {
			GLFace face = iter.next();
			count += face.getIndexCount();
		}		
		return count;
	}

	public GLVertex addVertex(float x, float y, float z) {
		
		// look for an existing GLVertex first
		Iterator<GLVertex> iter = mVertexList.iterator();
		while (iter.hasNext()) {
			GLVertex vertex = iter.next();
			if (vertex.x == x && vertex.y == y && vertex.z == z) {
				return vertex;
			}
		}
		
		// doesn't exist, so create new vertex
		GLVertex vertex = mWorld.addVertex(x, y, z);
		mVertexList.add(vertex);
		return vertex;
	}

    public GLVertex addVertex(float x, float y, float z, GLColor glColor) {

        GLVertex vertex =  addVertex(x, y, z);
        vertex.color = glColor;
        return vertex;
    }

	public void animateTransform(M4 transform) {
		mAnimateTransform = transform.copy();
		
		if (mTransform != null)
			transform = mTransform.multiply(transform);

		Iterator<GLVertex> iter = mVertexList.iterator();
		while (iter.hasNext()) {
			GLVertex vertex = iter.next();
			mWorld.transformVertex(vertex, transform);
		}
	}

    public void animateTransform(){
        mAnimateTransform = mTransform.copy();

        if (mTransform != null)
            mTransform = mTransform.multiply(mTransform);

        Iterator<GLVertex> iter = mVertexList.iterator();
        while (iter.hasNext()) {
            GLVertex vertex = iter.next();
            mWorld.transformVertex(vertex, mTransform);
        }
    }
	
	public void startAnimation() {
        Iterator<GLVertex> iter = mVertexList.iterator();
        while (iter.hasNext()) {
            GLVertex vertex = iter.next();
            mWorld.transformVertex(vertex, mTransform);
        }
	}

    public boolean isAutoAnimate() {
        return autoAnimate;
    }

    public void setAutoAnimate(boolean autoAnimate) {
        this.autoAnimate = autoAnimate;
    }

    public void setAxis(int axis){
        mAxis = axis;
    }

    public void setAngle(float angle, int axis){
        mAxis = axis;
        setAngle(angle);
    }

    public void setScale(float scale){
        float[][] m = scaleTransform.m;
        m[0][0] = m[1][1] = m[3][3] = scale;
        animateTransform(scaleTransform);
    }

    /**
     *
     * @param angle
     */
    public void setAngle(float angle) {
        // normalize the angle
        while (angle >= Utils.TWO_PI) angle -= Utils.TWO_PI;
        while (angle < 0f) angle += Utils.TWO_PI;
//		mAngle = angle;

        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);

        float[][] m = mTransform.m;
        switch (mAxis) {
            case kAxisX:
                m[1][1] = cos;
                m[1][2] = sin;
                m[2][1] = -sin;
                m[2][2] = cos;
                m[0][0] = 1f;
                m[0][1] = m[0][2] = m[1][0] = m[2][0] = 0f;
                break;
            case kAxisY:
                m[0][0] = cos;
                m[0][2] = sin;
                m[2][0] = -sin;
                m[2][2] = cos;
                m[1][1] = 1f;
                m[0][1] = m[1][0] = m[1][2] = m[2][1] = 0f;
                break;
            case kAxisZ:
                m[0][0] = cos;
                m[0][1] = sin;
                m[1][0] = -sin;
                m[1][1] = cos;
                m[2][2] = 1f;
                m[2][0] = m[2][1] = m[0][2] = m[1][2] = 0f;
                break;
        }

        animateTransform();

    }

    public void setRotateX(float angle){
        // normalize the angle
        while (angle >= Utils.TWO_PI) angle -= Utils.TWO_PI;
        while (angle < 0f) angle += Utils.TWO_PI;

        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);
        M4 transform = new M4();
        transform.setIdentity();
        float[][] m = transform.m;

        m[1][1] = cos;
        m[1][2] = sin;
        m[2][1] = -sin;
        m[2][2] = cos;
        m[0][0] = 1f;
        m[0][1] = m[0][2] = m[1][0] = m[2][0] = 0f;

        mTransform = mTransform.multiply(transform);
        animateTransform();
    }

    public void setRotateY(float angle){
        // normalize the angle
        while (angle >= Utils.TWO_PI) angle -= Utils.TWO_PI;
        while (angle < 0f) angle += Utils.TWO_PI;

        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);

        M4 transform = new M4();
        transform.setIdentity();
        float[][] m = transform.m;

        m[0][0] = cos;
        m[0][2] = sin;
        m[2][0] = -sin;
        m[2][2] = cos;
        m[1][1] = 1f;
        m[0][1] = m[1][0] = m[1][2] = m[2][1] = 0f;

        mTransform = mTransform.multiply(transform);
        animateTransform();
    }

    /**
     * rotates continuously
     * @param angle
     */
    public void setRotateZ(float angle){
        // normalize the angle
        while (angle >= Utils.TWO_PI) angle -= Utils.TWO_PI;
        while (angle < 0f) angle += Utils.TWO_PI;

        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);

        M4 transform = new M4();
        transform.setIdentity();
        float[][] m = transform.m;

        m[0][0] = cos;
        m[0][1] = sin;
        m[1][0] = -sin;
        m[1][1] = cos;
        m[2][2] = 1f;
        m[2][0] = m[2][1] = m[0][2] = m[1][2] = 0f;

        mTransform = mTransform.multiply(transform);
        animateTransform();
    }

    /**
     * rotate once
     * @param angle
     */
    public void rotateX(float angle){
        // normalize the angle
        while (angle >= Utils.TWO_PI) angle -= Utils.TWO_PI;
        while (angle < 0f) angle += Utils.TWO_PI;

        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);
        M4 transform = new M4();
        transform.setIdentity();
        float[][] m = transform.m;

        m[1][1] = cos;
        m[1][2] = sin;
        m[2][1] = -sin;
        m[2][2] = cos;
        m[0][0] = 1f;
        m[0][1] = m[0][2] = m[1][0] = m[2][0] = 0f;

        mTransform = mTransform.multiply(transform);
    }

    /**
     * rotate once
     * @param angle
     */
    public void rotateY(float angle){
        // normalize the angle
        while (angle >= Utils.TWO_PI) angle -= Utils.TWO_PI;
        while (angle < 0f) angle += Utils.TWO_PI;

        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);

        M4 transform = new M4();
        transform.setIdentity();
        float[][] m = transform.m;

        m[0][0] = cos;
        m[0][2] = sin;
        m[2][0] = -sin;
        m[2][2] = cos;
        m[1][1] = 1f;
        m[0][1] = m[1][0] = m[1][2] = m[2][1] = 0f;

        mTransform = mTransform.multiply(transform);
    }

    /**
     * rotates once
     * @param angle
     */
    public void rotateZ(float angle){
        // normalize the angle
        while (angle >= Utils.TWO_PI) angle -= Utils.TWO_PI;
        while (angle < 0f) angle += Utils.TWO_PI;

        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);

        M4 transform = new M4();
        transform.setIdentity();
        float[][] m = transform.m;

        m[0][0] = cos;
        m[0][1] = sin;
        m[1][0] = -sin;
        m[1][1] = cos;
        m[2][2] = 1f;
        m[2][0] = m[2][1] = m[0][2] = m[1][2] = 0f;

        mTransform = mTransform.multiply(transform);
    }


    /**
     * translate once by a vector
     * @param x
     * @param y
     * @param z
     */
    public void translate(float x, float y, float z){
        M4 transform = new M4();
        transform.setIdentity();
        float[][] m = transform.m;

        m[3][0]  = x;
        m[3][1]  = y;
        m[3][2]  = z;

        mTransform = mTransform.multiply(transform);
    }


    public void translate(PVector v){
        M4 transform = new M4();
        transform.setIdentity();
        float[][] m = transform.m;

        m[3][0]  = v.x;
        m[3][1]  = v.y;
        m[3][2]  = v.z;

        mTransform = mTransform.multiply(transform);
    }


    /**
     * translates once
     * @param x
     */
    public void translateX(float x){
        M4 transform = new M4();
        transform.setIdentity();
        float[][] m = transform.m;

        m[3][0]  = x;

        mTransform = mTransform.multiply(transform);
    }

    /**
     * continuous translation
     * @param x
     */
    public void setTranslateX(float x){
        M4 transform = new M4();
        transform.setIdentity();
        float[][] m = transform.m;

        m[3][0]  = x;
        mTransform = mTransform.multiply(transform);
        animateTransform();
    }

    /**
     * continuous translation around a vector
     * @param x
     * @param y
     * @param z
     */
    public void setTranslate(float x, float y, float z){
        M4 transform = new M4();
        transform.setIdentity();
        float[][] m = transform.m;

        m[3][0]  = x;
        m[3][1]  = y;
        m[3][2]  = z;
        mTransform = mTransform.multiply(transform);
        animateTransform();
    }

	public void endAnimation() {
		if (mTransform == null) {
			mTransform = new M4(mAnimateTransform);
            //mTransform = mTransform.multiply(scaleTransform);
		} else {
			mTransform = mTransform.multiply(mAnimateTransform);
		}
	}

	public M4						mTransform;
	public M4						mAnimateTransform;
	protected ArrayList<GLFace> mFaceList = new ArrayList<GLFace>();
	protected ArrayList<GLVertex> mVertexList = new ArrayList<GLVertex>();
	protected ArrayList<Integer> mIndexList = new ArrayList<Integer>();	// make more efficient?
	protected GLWorld mWorld;
    int mAxis = kAxisX;
    static public final int kAxisX = 0;
    static public final int kAxisY = 1;
    static public final int kAxisZ = 2;
}
