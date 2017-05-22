package com.dookoonu.math;

import com.dookoonu.exceptions.FileFormatException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;

/**
 * Created by cowell on 7/24/15.
 */
public class Model3D {
    float vert[];
    int tvert[];
    int nvert, maxvert;
    int con[];
    int ncon, maxcon;
    boolean transformed;
    Matrix3D mat;
    MakeShape makeShape;
    float xmin, xmax, ymin, ymax, zmin, zmax;

    public interface MakeShape{
        void generateFacesList(int n);
        void addFaceToShape();
    }


    Model3D () {
        mat = new Matrix3D ();
        mat.xrot(20);
        mat.yrot(30);
    }
    /** Create a 3D model by parsing an input stream */
    public Model3D(MakeShape makeShape)  {
        this.makeShape = makeShape;

    }

    public void loadObject(InputStream is)throws IOException, FileFormatException{
        StreamTokenizer st = new StreamTokenizer(
                new BufferedReader(new InputStreamReader(is, "UTF-8")));
        st.eolIsSignificant(true);
        st.commentChar('#');
        scan:
        while (true) {
            switch (st.nextToken()) {
                default:
                    break scan;
                case StreamTokenizer.TT_EOL:
                    break;
                case StreamTokenizer.TT_WORD:
                    if ("v".equals(st.sval)) {
                        double x = 0, y = 0, z = 0;
                        if (st.nextToken() == StreamTokenizer.TT_NUMBER) {
                            x = st.nval;
                            if (st.nextToken() == StreamTokenizer.TT_NUMBER) {
                                y = st.nval;
                                if (st.nextToken() == StreamTokenizer.TT_NUMBER)
                                    z = st.nval;
                            }
                        }
                        addVert((float) x, (float) y, (float) z);
                        while (st.ttype != StreamTokenizer.TT_EOL &&
                                st.ttype != StreamTokenizer.TT_EOF)
                            st.nextToken();
                    } else if ("f".equals(st.sval) || "fo".equals(st.sval) || "l".equals(st.sval)) {
                        int start = -1;
                        int prev = -1;
                        int n = -1;
                        while (true) {
                            if (st.nextToken() == StreamTokenizer.TT_NUMBER) {
                                n = (int) st.nval;
                                makeShape.generateFacesList((int)st.nval);
                                if (prev >= 0)
                                    add(prev - 1, n - 1);
                                if (start < 0)
                                    start = n;
                                prev = n;
                            } else if (st.ttype == '/')
                                st.nextToken();
                            else
                                break;
                        }
                        makeShape.addFaceToShape();
                        if (start >= 0)
                            add(start - 1, prev - 1);
                        if (st.ttype != StreamTokenizer.TT_EOL)
                            break scan;
                    } else {
                        while (st.nextToken() != StreamTokenizer.TT_EOL
                                && st.ttype != StreamTokenizer.TT_EOF);
                    }
            }
        }
        is.close();
        if (st.ttype != StreamTokenizer.TT_EOF)
            throw new FileFormatException(st.toString());
    }

    /** Add a vertex to this model */
    int addVert(float x, float y, float z) {
        int i = nvert;
        if (i >= maxvert)
            if (vert == null) {
                maxvert = 100;
                vert = new float[maxvert * 3];
            } else {
                maxvert *= 2;
                float nv[] = new float[maxvert * 3];
                System.arraycopy(vert, 0, nv, 0, vert.length);
                vert = nv;
            }
        i *= 3;
        vert[i] = x;
        vert[i + 1] = y;
        vert[i + 2] = z;
        //makeShape.addVertexToShape(x, y, z);
        return nvert++;
    }
    /** Add a line from vertex p1 to vertex p2 */
    void add(int p1, int p2) {
        int i = ncon;
        if (p1 >= nvert || p2 >= nvert)
            return;
        if (i >= maxcon)
            if (con == null) {
                maxcon = 100;
                con = new int[maxcon];
            } else {
                maxcon *= 2;
                int nv[] = new int[maxcon];
                System.arraycopy(con, 0, nv, 0, con.length);
                con = nv;
            }
        if (p1 > p2) {
            int t = p1;
            p1 = p2;
            p2 = t;
        }
        con[i] = (p1 << 16) | p2;
        ncon = i + 1;
    }
    /** Transform all the points in this model */
    void transform() {
        if (transformed || nvert <= 0)
            return;
        if (tvert == null || tvert.length < nvert * 3)
            tvert = new int[nvert*3];
        mat.transform(vert, tvert, nvert);
        transformed = true;
    }

    /* Quick Sort implementation
     */
    private void quickSort(int a[], int left, int right)
    {
        int leftIndex = left;
        int rightIndex = right;
        int partionElement;
        if ( right > left)
        {

         /* Arbitrarily establishing partition element as the midpoint of
          * the array.
          */
            partionElement = a[ ( left + right ) / 2 ];

            // loop through the array until indices cross
            while( leftIndex <= rightIndex )
            {
            /* find the first element that is greater than or equal to
             * the partionElement starting from the leftIndex.
             */
                while( ( leftIndex < right ) && ( a[leftIndex] < partionElement ) )
                    ++leftIndex;

            /* find an element that is smaller than or equal to
             * the partionElement starting from the rightIndex.
             */
                while( ( rightIndex > left ) &&
                        ( a[rightIndex] > partionElement ) )
                    --rightIndex;

                // if the indexes have not crossed, swap
                if( leftIndex <= rightIndex )
                {
                    swap(a, leftIndex, rightIndex);
                    ++leftIndex;
                    --rightIndex;
                }
            }

         /* If the right index has not reached the left side of array
          * must now sort the left partition.
          */
            if( left < rightIndex )
                quickSort( a, left, rightIndex );

         /* If the left index has not reached the right side of array
          * must now sort the right partition.
          */
            if( leftIndex < right )
                quickSort( a, leftIndex, right );

        }
    }

    private void swap(int a[], int i, int j)
    {
        int T;
        T = a[i];
        a[i] = a[j];
        a[j] = T;
    }

    public float[] getVert() {
        return vert;
    }

    /** eliminate duplicate lines */
    void compress() {
        int limit = ncon;
        int c[] = con;
        quickSort(con, 0, ncon - 1);
        int d = 0;
        int pp1 = -1;
        for (int i = 0; i < limit; i++) {
            int p1 = c[i];
            if (pp1 != p1) {
                c[d] = p1;
                d++;
            }
            pp1 = p1;
        }
        ncon = d;
    }

    /** Find the bounding box of this model */
    void findBB() {
        if (nvert <= 0)
            return;
        float v[] = vert;
        float xmin = v[0], xmax = xmin;
        float ymin = v[1], ymax = ymin;
        float zmin = v[2], zmax = zmin;
        for (int i = nvert * 3; (i -= 3) > 0;) {
            float x = v[i];
            if (x < xmin)
                xmin = x;
            if (x > xmax)
                xmax = x;
            float y = v[i + 1];
            if (y < ymin)
                ymin = y;
            if (y > ymax)
                ymax = y;
            float z = v[i + 2];
            if (z < zmin)
                zmin = z;
            if (z > zmax)
                zmax = z;
        }
        this.xmax = xmax;
        this.xmin = xmin;
        this.ymax = ymax;
        this.ymin = ymin;
        this.zmax = zmax;
        this.zmin = zmin;
    }

}
