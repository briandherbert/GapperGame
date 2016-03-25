package com.meetme.tuneablegame.Gapper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;

import com.meetme.tuneablegame.R;
import com.meetme.tuneablegame.Visual;

/**
 * Created by bherbert on 3/25/16.
 */
public class WallsVisual extends Visual {
    static final String TAG = WallsVisual.class.getSimpleName();

    Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    static int NUM_WALLS = 80;

    Region[] mNonNullHitRegions = new Region[NUM_WALLS];
    Region[] mReportedHitRegions = new Region[NUM_WALLS];
    Rect[] mWalls = new Rect[NUM_WALLS];

    int playerSize;

    // Config vars, expressed in terms of screen height
    int wallHeight;
    double vertSpacePct = .2;

    double pxPerMs;
    int vertSpace;
    int gapSize;

    public static class Config {
        public static final int COL_SPEED = 0;
        public static final int COL_VERT_SPACE = 1;
        public static final int COL_GAP = 2;
        public static final int COL_COLOR = 3;

        public long msToTravelScreen = 2000;
        public double vertSpacePlayerPct = 100;
        public double gapPlayerPct = 100;
        String color = "#0000FF";

        public static Config parse(String[][] rowsCols) {
            Config config = new Config();

            try {
                config.msToTravelScreen = Integer.valueOf(rowsCols[1][WallsVisual.Config.COL_SPEED]);
                config.vertSpacePlayerPct = Integer.valueOf(rowsCols[1][WallsVisual.Config.COL_VERT_SPACE]);
                config.gapPlayerPct = Integer.valueOf(rowsCols[1][WallsVisual.Config.COL_GAP]);
                config.color = rowsCols[1][Config.COL_COLOR];
            } catch (Exception e) {

            }

            Log.v(TAG, "Parsed " + config.toString());
            return config;
        }

        public String toString() {
            return "Config\n"
                    + " msToTravelScreen: " + msToTravelScreen + "\n"
                    + " vertSpacePlayerPct: " + vertSpacePlayerPct + "\n"
                    + " gapPlayerPct: " + gapPlayerPct + "\n"
                    + " color: " + color + "\n";
        }
    }

    Config mConfig = new Config();


    public WallsVisual(Context context, Config config) {
        mWalls[0] = new Rect((int) screenCenterX, 0, (int) screenCenterX + 20, (int) mScreenHeight);

        for (int i = 0; i < mWalls.length; i++) {
            mNonNullHitRegions[i] = new Region();
        }

        playerSize = context.getResources().getDimensionPixelSize(R.dimen.player_visual_radius) * 2;

        if (config != null) mConfig = config;

        // Calc all the size
        wallHeight = playerSize / 2;
        vertSpace = (int) (playerSize * (mConfig.vertSpacePlayerPct) / 100.0);
        gapSize = (int) (playerSize * (mConfig.gapPlayerPct / 100.0));
        mPaint.setColor(Color.BLUE);
        pxPerMs = mScreenHeight / mConfig.msToTravelScreen;

        try {
            mPaint.setColor(Color.parseColor(config.color));
        } catch (Exception e) {
            mPaint.setColor(Color.BLUE);
        }
//
//        mPaint.setColor(Color.BLUE);
//        vertSpace = playerSize * 3;
//        gapSize = (int) (playerSize * 1.5);

    }

    @Override
    public void draw(Canvas canvas) {
        for (Rect rect : mWalls) {
            if (rect != null) canvas.drawRect(rect, mPaint);
        }
    }

    @Override
    public Region[] getHitRegions() {
        return mReportedHitRegions;
    }

    @Override
    public void onTick() {
        double dist = deltaTime * pxPerMs;

        int highestY = (int) mScreenHeight;

        for (int i = 0; i < mWalls.length; i++) {
            if (mWalls[i] != null) {
                mWalls[i].top += dist;
                mWalls[i].bottom += dist;

                if (mWalls[i].top < highestY) {
                    highestY = mWalls[i].top;
                }

                if (mWalls[i].top > mScreenHeight) {
                    // Off screen, clear out

                    mWalls[i] = null;
                    mReportedHitRegions[i] = null;
                } else {
                    mNonNullHitRegions[i].set(mWalls[i]);
                    mReportedHitRegions[i] = mNonNullHitRegions[i];
                }
            } else {
                mReportedHitRegions[i] = null;
            }
        }

        if (highestY > vertSpace) {
            // Make a new gap

            int i = 0;
            while (mReportedHitRegions[i] != null) {
                i++;
            }

            int j = i+1;
            while (mReportedHitRegions[i] != null) {
                j++;
            }

            int gapStart = rand.nextInt((int) mScreenWidth - gapSize);

            mWalls[i] = new Rect(0, -wallHeight, gapStart, 0);
            mWalls[j] = new Rect(gapStart + gapSize, -wallHeight, (int) mScreenWidth, 0);
        }

        updateHitboxes();
    }

    void updateHitboxes() {
        for (int i = 0; i < mWalls.length; i++) {
            if (mWalls[i] != null) {
                mNonNullHitRegions[i].set(mWalls[i]);
                mReportedHitRegions[i] = mNonNullHitRegions[i];
            } else {
                mReportedHitRegions[i] = null;
            }
        }
    }
}
