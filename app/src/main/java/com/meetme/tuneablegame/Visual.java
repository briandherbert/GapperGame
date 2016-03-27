package com.meetme.tuneablegame;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Vibrator;
import android.util.Log;

import com.meetme.tuneablegame.Gapper.Config;

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
    public static long lastTic;
    public static double dist = 0;
    public static double deltaDist = 0;
    public static double distInPx = 0;

    public static double screenCenterX;
    public static double screenCenterY;
    public static Context mContext;
    public static Rect screenRect;

    public static Random rand;

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

        rand = new Random();

        options = new BitmapFactory.Options();
        options.inTempStorage = new byte[8 * 1024];
        options.inSampleSize = 2;

        countdown = 0;
    }

    public static void startTimers() {
        dist = 0;
        deltaDist = 0;
        distInPx = 0;
    }


    public abstract void draw(Canvas canvas);

    public abstract Region[] getHitRegions();

    public abstract void onTick(long deltaTime);

    public void setConfig(Config config) {

    }

    public void setVisibility(boolean uIsVisible) {
        isVisible = uIsVisible;
    }
}