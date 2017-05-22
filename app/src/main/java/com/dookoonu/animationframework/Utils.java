package com.dookoonu.animationframework;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.text.format.Time;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import static java.lang.Math.sqrt;

/**
 * Created by cowell on 7/14/15.
 */
public class Utils {
    private static Time time;
    public static synchronized int second(){
        if(time == null){
            time = new Time();
        }
        time.set(SystemClock.uptimeMillis());
        return time.second;
    }

    public static synchronized int minute(){
        if(time == null){
            time = new Time();
        }
        time.set(SystemClock.uptimeMillis());
        return time.minute;
    }

    public static synchronized int hour(){
        if(time == null){
            time = new Time();
        }
        time.set(SystemClock.uptimeMillis());
        return time.hour;
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeSampledBitmapFromResource(InputStream is) {

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
        return bitmap;
    }

    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }else return false;
    }

    private ShapeHolder addBall(float x, float y, float w, float h) {
        OvalShape circle = new OvalShape();
        circle.resize(w, h);
        ShapeDrawable drawable = new ShapeDrawable(circle);
        ShapeHolder shapeHolder = new ShapeHolder(drawable);
        shapeHolder.setX(x);
        shapeHolder.setY(y);
        //these colors should be user definable
        int red = (int)(100 + Math.random() * 155);
        int green = (int)(100 + Math.random() * 155);
        int blue = (int)(100 + Math.random() * 155);
        int color = 0xff000000 | red << 16 | green << 8 | blue;
        Paint paint = drawable.getPaint();
        int darkColor = 0xff000000 | red/4 << 16 | green/4 << 8 | blue/4;
        RadialGradient gradient = new RadialGradient(37.5f, 12.5f,
                50f, color, darkColor, Shader.TileMode.CLAMP);
        paint.setShader(gradient);
        shapeHolder.setPaint(paint);
        //balls.add(shapeHolder);
        return shapeHolder;
    }

    /*************** Math Routines ***********************/

    /**
     * Find the smallest power of two >= the input value.
     * (Doesn't work for negative numbers.)
     */
    public static final float PI = (float)Math.PI;
    public static final float HALF_PI = (float)Math.PI/2.0f;
    public static final float TWO_PI = (float)Math.PI * 2.0f;
    private static Random random;
    public static synchronized float random() {
        if (random == null) {
            random = new Random(SystemClock.elapsedRealtimeNanos());
        }
        return random.nextFloat();
    }

    public static synchronized float random(float val) {
        if (random == null) {
            random = new Random(SystemClock.elapsedRealtimeNanos());
        }
        float rnd = random.nextFloat();
        while (rnd>=val) random.nextFloat();
        return rnd;
    }

    /**
     * For example, random(-5, 10.2) returns values starting at -5 and up to (but not including) 10.2.
     * @param low
     * @param high
     * @return a float with a value between the two values
     */
    public static synchronized float random(float low, float high){
        if (random == null) {
            random = new Random(SystemClock.elapsedRealtimeNanos());
        }
        float rnd = random.nextFloat();
        while (rnd>=high && rnd<low) random.nextFloat();
        return rnd;
    }
    private int roundUpPower2(int x) {
        x = x - 1;
        x = x | (x >> 1);
        x = x | (x >> 2);
        x = x | (x >> 4);
        x = x | (x >> 8);
        x = x | (x >>16);
        return x + 1;
    }

    public static float radians(float degrees){
        return (float)Math.toRadians(degrees);
    }

    public static float degrees(float radians){
        return (float)Math.toDegrees(radians);
    }

    /**
     * Math Routines inspired by processing
     */

    public static float constrain(float amt, float low, float high){
        if(amt<=low){
            return low;
        }else if(amt>=high){
            return high;
        }
        return amt;
    }

    public static int constrain(int amt, int low, int high){
        return constrain(amt, low, high);
    }

    public static float map(float value, float start1, float stop1, float start2, float stop2){
        return norm(value,start1,stop1) * (stop2 - start2) + start2;
    }

    public static float norm(float value, float start, float stop){
        return (stop-value)/(stop-start);
    }

    public static float dist(float x1, float y1, float x2, float y2) {
        float xdiff = x2 - x1;
        float ydiff = y2 - y1;

        return (float) sqrt(xdiff * xdiff + ydiff * ydiff);
    }

    public static float dist(float x1, float y1, float z1, float x2, float y2, float z2) {
        float xdiff = x2 - x1;
        float ydiff = y2 - y1;
        float zdiff = z2 - z1;

        return (float) sqrt(xdiff*xdiff + ydiff * ydiff + zdiff * zdiff);
    }

    public static float min(float ... values){
        float min = values[0];
        if(values.length > 1){
            for(float value : values){
                if(value < min) min = value;
            }
        }
        return min;
    }

    public static float max(float ... values){
        float max = values[0];
        if(values.length > 1){
            for(float value : values){
                if(value > max) max = value;
            }
        }
        return max;
    }

    public static float lerp(float start, float stop, float amt) {
        return norm(amt,start, stop) * (stop - start) + start;
    }

}
