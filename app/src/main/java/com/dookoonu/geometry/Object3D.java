package com.dookoonu.geometry;

import com.dookoonu.math.Model3D;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by cowell on 7/24/15.
 */
public class Object3D extends GLShape implements Model3D.MakeShape{
    private float scale;
    Model3D model3D;
    private int count = 0;
    private GLVertex[] vertices;
    private int[] facesList;
    private GLColor globalColor = new GLColor(0,210,10);
    private int faces;
    public Object3D(GLWorld world, InputStream inputStream) {
        super(world);
        facesList = new int[4];//max size of a face -- 4 vertices
        model3D = new Model3D( this);
        try {
            model3D.loadObject(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (com.dookoonu.exceptions.FileFormatException e) {
            e.printStackTrace();
        }
    }

    public Object3D(GLWorld world, InputStream inputStream, float scale) {
        super(world);
        this.scale = scale;
        facesList = new int[4];//max size of a face -- 4 vertices
        model3D = new Model3D( this);
        try {
            model3D.loadObject(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (com.dookoonu.exceptions.FileFormatException e) {
            e.printStackTrace();
        }
    }


    public int getFaces() {
        return faces;
    }

    @Override
    public void generateFacesList(int n) {
        if(count == facesList.length){
          resetFacesList();
        }
        facesList[count++] = n;
    }

    private void resetFacesList() {
        count = 0;
        Arrays.fill(facesList, 0);
    }

    @Override
    public void addFaceToShape() {
        float[] vert = model3D.getVert();

        vertices = new GLVertex[count];
        float x,y,z;
        int j;
        if(scale > 0.0f){
            for(int i=0;i<count;i++){//collect all the vertices for a face
                j =(facesList[i] - 1) * 3;//get the vertex index
                x = vert[j] * scale;
                y = vert[j + 1] * scale;
                z = vert[j + 2] * scale;
                vertices[i] = addVertex(x,y,z);//, new GLColor(200,0,0));
            }
        }else {
            for(int i=0;i<count;i++){//collect all the vertices for a face
                j =(facesList[i] - 1) * 3;//get the vertex index
                x = vert[j] ;//* 0.05f;
                y = vert[j + 1] ;//* 0.05f;
                z = vert[j + 2];// * 0.05f;
                vertices[i] = addVertex(x,y,z);
            }
        }

        addFace(new GLFace(vertices));
        faces++;
        //Log.i("O3D", Integer.toString(faces));
        resetFacesList();
    }
}
