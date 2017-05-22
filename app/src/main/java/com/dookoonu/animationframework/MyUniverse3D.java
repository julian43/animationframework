package com.dookoonu.animationframework;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;

import com.dookoonu.geometry.Cube;
import com.dookoonu.geometry.GLColor;
import com.dookoonu.geometry.GLShape;
import com.dookoonu.geometry.M4;
import com.dookoonu.geometry.Object3D;
import com.dookoonu.geometry.Triangle2;
import com.dookoonu.geometry.Universe;

import java.io.InputStream;

import static com.dookoonu.animationframework.Utils.PI;
import static com.dookoonu.animationframework.Utils.radians;

/**
 * Created by cowell on 7/22/15.
 */
public class MyUniverse3D extends GameView3D{
    Cube cube;
    Triangle2 triangle2;
    Object3D hughes;
    Bitmap bitmap;
    Universe universe;

    int one = 0x10000;
    int half = 0x08000;
    GLColor red = new GLColor(one, 0, 0);
    GLColor green2 = new GLColor(0, one, 0,0x22);
    GLColor green = new GLColor(0, one, 0);
    GLColor blue = new GLColor(0, 0, one);
    GLColor yellow2 = new GLColor(one, one, 0, 0x00010);
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

    public MyUniverse3D(Context context, AttributeSet attrs) {
        super(context, attrs);
        InputStream is = getContext().getResources()
                .openRawResource(R.raw.botticelli33);
        //bitmap = Utils.decodeSampledBitmapFromResource(getResources(),R.raw.botticelli33,100,100);
        bitmap = Utils.decodeSampledBitmapFromResource(is);

        //re-using input stream
        is = getContext().getResources()
                .openRawResource(R.raw.hughes_500);

        universe = new Universe(this);
        universe.addWorld("textured");
        universe.addWorld("regular");

        cube = new Cube(universe.getWorld("regular"), c0, c0, c4, c1, c1, c5);
        for(int i=0;i<6;i++){
            if(i%2==0){
                cube.setFaceColor(i, red);
            }else{
                cube.setFaceColor(i, blue);
            }

        }//colors have to be set before generate.
        float scale = 0.05f;
        hughes = new Object3D(universe.getWorld("regular"),is,scale);

        for(int i=0;i<hughes.getFaces();i+=15){
            if(i>=hughes.getFaces())break;
            if(i%2==0){
                hughes.setFaceColor(i, green2);
            }else{
                hughes.setFaceColor(i, yellow2);
            }
        }
        Log.i("Construct", "Finished");
        triangle2 = new Triangle2(universe.getWorld("textured"), coords);
        //triangle2.setFaceColor(0,blue);

        triangle2.setAxis(GLShape.kAxisZ);
        cube.setAxis(GLShape.kAxisY);

        //universe.addShape("textured", triangle2, true);//true -> autodraw
        universe.addShape("regular", cube);
        universe.addShape("regular", hughes);
        universe.close();
    }

    int flip = 1;
    @Override
    protected void renderFrame() {
        background(0.1f, 0.7f, 0.7f, 1.0f);
        camera();
        //scale(0.05f,0.05f,0.05f);
        //cube.rotateZ(Utils.PI/60.0f);
        cube.center.mult(-1.0f);
        cube.translate(cube.center);
        cube.rotateZ(PI/60.0f);
        cube.center.mult(-1.0f);
        cube.translate(cube.center);
        cube.startAnimation();
        cube.endAnimation();
        hughes.rotateZ(PI/60.0f);
        hughes.startAnimation();
        hughes.endAnimation();
        universe.draw();
        //universe.autoDraw();
        if(frameCount%2==0){
            flip = -1;
        }else flip = 1;
        //hughes.rotateZ(Utils.PI/12.0f);
        //hughes.translateX((100.0f * flip) / width);
        angle+= ainc;
    }

    @Override
    protected void initialize() {
        universe.getWorld("textured").setupTex(this, bitmap);
        angle = radians(15);
        ainc = radians(5.0f);
        triangle2.setAngle(angle);

        //cube.setAngle(radians(3.0f));
        //cube.setTranslateX(-0.2f);
        //hughes.setAxis(GLShape.kAxisY);
        //hughes.setAngle(PI/2.0f);
        //hughes.setScale(0.05f);

        //hughes.translateX(-100.0f / width);
        //hughes.rotateX(PI/2.0f);
        //hughes.rotateZ(Utils.PI/60.0f);
    }
}
