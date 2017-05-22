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

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GLVertex {

    public float x;
    public float y;
    public float z;
    final short index; // index in vertex table
    GLColor color;

    GLVertex() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.index = -1;
    }

    GLVertex(float x, float y, float z, int index) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.index = (short)index;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof GLVertex) {
            GLVertex v = (GLVertex)other;
            return (x == v.x && y == v.y && z == v.z);
        }
        return false;
    }

    static public int toFixed(float x) {
        return (int)(x * 65536.0f);
    }

    public void put(FloatBuffer vertexBuffer, IntBuffer colorBuffer) {
        vertexBuffer.put(x);//(toFixed(x));
        vertexBuffer.put(y);//(toFixed(y));
        vertexBuffer.put(z);//(toFixed(z));
        if (color == null) {
            colorBuffer.put(0);
            colorBuffer.put(0);
            colorBuffer.put(0);
            colorBuffer.put(0);
        } else {
            colorBuffer.put(color.red);
            colorBuffer.put(color.green);
            colorBuffer.put(color.blue);
            colorBuffer.put(color.alpha);
        }
    }

    public void update(FloatBuffer vertexBuffer, M4 transform) {
        // skip to location of vertex in mVertex buffer
        vertexBuffer.position(index * 3);

        if (transform == null) {
            vertexBuffer.put(x);//(toFixed(x));
            vertexBuffer.put(y);//(toFixed(y));
            vertexBuffer.put(z);//(toFixed(z));
        } else {
            GLVertex temp = new GLVertex();
            transform.multiply(this, temp);
            vertexBuffer.put(temp.x);//(toFixed(temp.x));
            vertexBuffer.put(temp.y);//(toFixed(temp.y));
            vertexBuffer.put(temp.z);//(toFixed(temp.z));
        }
    }
}
