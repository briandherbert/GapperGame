package com.meetme.tuneablegame;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.util.Log;

/*
 * Everything is expressed in feet. The squirrel's wheel is 1 foot in diameter, so we can find px per foot
 * by finding the desired height of the sq bmp in px, dividing that by 2.7304 (wheels per height),
 * and multiplying that by pi.
 */

public abstract class Visual {
    public static final String ID = "VISUAL";
    public static boolean logs = false;

    public static double mScreenWidth;
    public static double mScreenHeight;

    public static final int SCREEN_BOTTOM_PX = 15;

    //tracking movement
    public static double deltaTimeInSec;
    public static long deltaTime = 0;
    public static long lastTic;
    public static double dist = 0;
    public static double deltaDist = 0;
    public static double distInPx = 0;

    public static double screenCenterX;
    public static double screenCenterY;
    public static Context mContext;
    public static Rect screenRect;

    public static Paint paint = new Paint();

    public static Random rand;
    static Vibrator vib;

    public static BitmapFactory.Options options;

    boolean isVisible = true;

    public static int countdown;

    public static void init(int screenWidth, int screenHeight, Context context) {
        mContext = context;
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        screenCenterX = mScreenWidth / 2;
        screenCenterY = mScreenWidth / 2;
        screenRect = new Rect(0, 0, screenWidth, screenHeight);

        vib = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        paint.setColor(Color.BLACK);

        rand = new Random();

        options = new BitmapFactory.Options();
        options.inTempStorage = new byte[8 * 1024];
        options.inSampleSize = 2;

        countdown = 0;
    }

    public static void startTimers() {
        deltaTime = 0;
        deltaTimeInSec = 0;
        dist = 0;
        deltaDist = 0;
        distInPx = 0;
    }

    public Bitmap initBitmap(int id) {
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), id);
        //bmp = Bitmap.createScaledBitmap(bmp, (int)(bmp.getWidth()*.25), (int)(bmp.getHeight()*.25), false);
        return bmp;
    }

    public Bitmap initBitmap(int id, int width, int height) {
        if (width <= 0)
            width = 1;
        if (height <= 0)
            height = 1;

        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), id);
        //bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmp = Bitmap.createScaledBitmap(bmp, width, height, false);
        return bmp;
    }

    public Bitmap initBitmapOpaque(int id, int width, int height) {
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), id);
        bmp = bmp.copy(Bitmap.Config.RGB_565, false);
        bmp = Bitmap.createScaledBitmap(bmp, width, height, false);

        return bmp;
    }

    public abstract void draw(Canvas canvas);

    public abstract Region[] getHitRegions();

    public abstract void onTick();

    public void setVisibility(boolean uIsVisible) {
        isVisible = uIsVisible;
    }

    public static void vibrate(long ms) {
        if (vib == null) return;
        vib.vibrate(100);
    }
}