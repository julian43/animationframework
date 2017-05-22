/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dookoonu.animationframework;


import android.graphics.PointF;

/**
 *
 * @author Julian G. Cowell
 */
public class LatheCurve {

    private int step = 20; //number of introduced interpolated points.
    private float xsIn[], ysIn[];
    private float[] xs;
    private float[] ys;
    private PointF startTangent, endTangent;

    public LatheCurve(){
        
    }
    
    public LatheCurve(float[] xsIn, float[] ysIn) {
        this.xsIn = xsIn;
        this.ysIn = ysIn;
        int numVerts = xsIn.length;

        startTangent = new PointF((Math.abs(xsIn[1]) - Math.abs(xsIn[0])) * 2, 0);

        endTangent = new PointF((Math.abs(xsIn[numVerts - 1])
                - Math.abs(xsIn[numVerts - 2])) * 2, 0);

        makeCurve();
    }

    public void curve(float[] xsIn, float[] ysIn){
        this.xsIn = xsIn;
        this.ysIn = ysIn;
        int numVerts = xsIn.length;

        startTangent = new PointF((Math.abs(xsIn[1]) - Math.abs(xsIn[0])) * 2, 0);

        endTangent = new PointF((Math.abs(xsIn[numVerts - 1])
                - Math.abs(xsIn[numVerts - 2])) * 2, 0);

        makeCurve();
        
    }


    public void setStep(int step) {
        this.step = step;
    }

    private int countVerts() {
        int numOutVerts = 1;
        for (int i = 0; i < xsIn.length - 1; i++) {
            if (xsIn[i] < 0) //straight line starts here.
            {
                numOutVerts++;
            } else //curve segment starts here
            {
                numOutVerts += (step + 1);
            }
        }
        return numOutVerts;
    }

    private float fh1(float t) {
        return (2.0f) * (float)Math.pow(t, 3) - (3.0f * t * t) + 1;
    }

    private float fh2(float t) {
        return (-2.0f) * (float)Math.pow(t, 3) + (3.0f * t * t);
    }

    private float fh3(float t) {
        return (float)Math.pow(t, 3) - (2.0f * t * t) + t;
    }

    private float fh4(float t) {
        return (float)Math.pow(t, 3) - (t * t);
    }

    private void makeHermite(int startPosn, float x0, float y0,
            float x1, float y1, PointF t0, PointF t1) {
        float xCoord, yCoord;
        float tStep = 1.0f / (step + 1);
        float t;

        if (x1 < 0) {
            x1 = -x1;
        }   // +ve while making the curve

        for (int i = 0; i < step; i++) {
            t = tStep * (i + 1);
            xCoord = (fh1(t) * x0) + (fh2(t) * x1) + (fh3(t) * t0.x) + (fh4(t) * t1.x);
            xs[startPosn + i] = xCoord;

            yCoord = (fh1(t) * y0) + (fh2(t) * y1) + (fh3(t) * t0.y) + (fh4(t) * t1.y);
            ys[startPosn + i] = yCoord;
        }

        xs[startPosn + step] = x1;
        ys[startPosn + step] = y1;
    }

    private void setTangent(PointF tangent, int i) {
        float xLen = Math.abs(xsIn[i + 1]) - Math.abs(xsIn[i - 1]);
        float yLen = ysIn[i + 1] - ysIn[i - 1];
        tangent.set(xLen / 2, yLen / 2);
    }

    private void makeCurve() {
        int numInVerts = xsIn.length;
        int numOutVerts = countVerts();
        xs = new float[numOutVerts];
        ys = new float[numOutVerts];

        xs[0] = Math.abs(xsIn[0]);  // start of curve is initialised
        ys[0] = ysIn[0];
        int startPosn = 1;

        // tangents for the current curve seqment between two points
        PointF t0 = new PointF();
        PointF t1 = new PointF();

        for (int i = 0; i < numInVerts - 1; i++) {
            if (i == 0) {
                t0.set(startTangent.x, startTangent.y);
            } else {
                t0.set(t1.x, t1.y);
            }

            if (i == numInVerts - 2) {
                t1.set(endTangent.x, endTangent.y);
            } else {
                setTangent(t1, i + 1);
            } // tangent at pt i+1

            // if xsIn[i] < 0 then use a line to link (x,y) to next pt
            if (xsIn[i] < 0) {
                xs[startPosn] = Math.abs(xsIn[i + 1]);
                ys[startPosn] = ysIn[i + 1];
                startPosn++;
            } else { // make a Hermite curve
                makeHermite(startPosn, xsIn[i], ysIn[i], xsIn[i + 1], ysIn[i + 1],
                        t0, t1);

                startPosn += (step + 1);
            }
        }
    }

    public float[] getXs() {
        return xs;
    }

    public float[] getYs() {
        return ys;
    }

    public float getHeight() {
        float height = ys[0];

        for (int i = 1; i < ys.length; i++) {
            if (height < ys[i]) {
                height = ys[i];
            }
        }

        return height;
    }
}
