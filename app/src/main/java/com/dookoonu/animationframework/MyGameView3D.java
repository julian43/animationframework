package com.dookoonu.animationframework;

import android.content.Context;
import android.util.AttributeSet;

import com.dookoonu.geometry.Cube;
import com.dookoonu.geometry.GLColor;
import com.dookoonu.geometry.GLShape;
import com.dookoonu.geometry.GLWorld;
import com.dookoonu.geometry.M4;
import com.dookoonu.geometry.Triangle;
import com.dookoonu.geometry.Triangle2;
import com.dookoonu.geometry.Universe;

import static com.dookoonu.animationframework.Utils.radians;

/**
 * Created by cowell on 7/18/15.
 */
public class MyGameView3D extends GameView3D{
    Triangle triangle;
    Cube cube;
    Triangle2 triangle2;
    GLWorld world = new GLWorld();
    Universe universe;

    int one = 0x10000;
    int half = 0x08000;
    GLColor red = new GLColor(one, 0, 0);
    GLColor green = new GLColor(0, one, 0);
    GLColor blue = new GLColor(0, 0, one);
    GLColor yellow = new GLColor(one, one, 0);
    GLColor orange = new GLColor(one, half, 0);
    GLColor white = new GLColor(one, one, one);
    GLColor black = new GLColor(0, 0, 0);

    // coordinates for our cubes
    float c0 = -1.0f;
    float c1 = -0.38f;
    float c2 = -0.32f;
    float c3 = 0.32f;
    float c4 = 0.38f;
    float c5 = 1.0f;
    private float angle, ainc;
    private M4 cubeTransform;

    // A unit-sided equalateral triangle centered on the origin.
    float[] coords = {
            // X, Y, Z
            -0.5f, -0.25f, 0,
            0.5f, -0.25f, 0,
            0.0f,  0.559016994f, 0
    };


    public MyGameView3D(Context context, AttributeSet attrs) {
        super(context, attrs);
        //universe = new Universe(this);
       // universe.addWorld("textured");
        //universe.addWorld("regular");
        triangle = new Triangle(this);
        cube = new Cube(world, c0, c0, c4, c1, c1, c5);
        triangle2 = new Triangle2(world,coords);
        triangle2.setFaceColor(0,blue);
        triangle2.setAxis(GLShape.kAxisZ);
        cube.setAxis(GLShape.kAxisY);
        /*cubeTransform = new M4();
        cubeTransform.setIdentity();
        cubeTransform.m[0][0]=(float)cos(Math.PI/100);
        cubeTransform.m[0][1]=(float)-sin(Math.PI / 100);
        cubeTransform.m[1][0]=(float)sin(Math.PI/100);
        cubeTransform.m[1][1]=(float)cos(Math.PI / 100);*/
        //cube.mTransform = cubeTransform;
        for(int i=0;i<6;i++){
            cube.setFaceColor(i, orange);
        }//colors have to be set before generate.
        world.addShape(cube);
        world.addShape(triangle2);
        world.generate();

        /*universe.addShape("regular", cube);
        universe.addShape("textured", triangle2);
        universe.close();*/
        //has to be done after generate
        //cube.setAngle(radians(3.0f));
        //triangle2.setAngle(Utils.radians(40.0f), GLShape.kAxisZ);
    }

    @Override
    protected void renderFrame() {
        background(0.1f, 0.7f, 0.7f, 1.0f);
        camera();
        //triangle.draw();
        //translate(0, 0, -1.8f);
       /* saveState();
        scale(0.5f, 0.5f, 0.5f);
        rotate(angle, 0, 1, 0);
        rotate(angle * 0.25f, 1, 0, 0);
        world.draw(getSurfaceGL());
        restoreState();*/
        //triangle2.setAngle(angle);


        triangle2.startAnimation();
        cube.startAnimation();
        world.draw(getSurfaceGL());
        triangle2.endAnimation();
        cube.endAnimation();


        triangle.draw();

        //cube.animateTransform(cubeTransform);
        //world.draw(getSurfaceGL());
        //cube.endAnimation();

        //universe.draw();

        angle+= ainc;
    }


    @Override
    protected void initialize() {
        //background(0.5f, 0.5f, 0.5f, 0.0f);
        /*cube.setFaceColor(Cube.kTop, orange);
        cube.setFaceColor(Cube.kBack, blue);
        cube.setFaceColor(Cube.kBottom, blue);
        cube.setFaceColor(Cube.kFront, blue);
        cube.setFaceColor(Cube.kLeft, black);
        cube.setFaceColor(Cube.kRight, yellow);*/
        angle = radians(15);
        ainc = radians(5.0f);
        triangle2.setAngle(angle);
        cube.setAngle(radians(3.0f));

        triangle.setupTex(getContext());
        frameRate(30);
    }
}
