package com.dookoonu.geometry;

import java.nio.FloatBuffer;

/**
 * Created by cowell on 7/21/15.
 */
public class Triangle2 extends GLShape{
    private final int xcor = 0;
    private final int ycor = 1;
    private final int zcor = 2;
    private FloatBuffer textureBuffer;

    public Triangle2(GLWorld world, float[] vertex1, float[] vertex2, float[] vertex3) {
        super(world);

        GLVertex v1 = addVertex(vertex1[xcor], vertex1[ycor], vertex1[zcor]);
        GLVertex v2 = addVertex(vertex2[xcor], vertex2[ycor], vertex2[zcor]);
        GLVertex v3 = addVertex(vertex3[xcor], vertex3[ycor], vertex3[zcor]);

        // vertices are added in a clockwise orientation (when viewed from the outside)
        addFace(new GLFace(v1,v2,v3));
        textureBuffer = GLShape.prepareTexBuffer(vertex1, vertex2, vertex3);
        world.setTexBuffer(textureBuffer);
    }

    public Triangle2(GLWorld world, float[] coords) {
        super(world);

        GLVertex v1 = addVertex(coords[xcor], coords[ycor], coords[zcor]);
        GLVertex v2 = addVertex(coords[xcor+3], coords[ycor+3], coords[zcor+3]);
        GLVertex v3 = addVertex(coords[xcor+6], coords[ycor+6], coords[zcor+6]);

        // vertices are added in a clockwise orientation (when viewed from the outside)
        addFace(new GLFace(v1,v2,v3));
        textureBuffer = GLShape.prepareTexBuffer(coords);
        world.setTexBuffer(textureBuffer);
    }
}
