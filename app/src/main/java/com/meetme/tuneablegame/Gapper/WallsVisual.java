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

    static int NUM_WALLS = 60;

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

    Config.Mode mMode = Config.Mode.ascend;

    Config mConfig = new Config();

    public WallsVisual(Context context, Config config) {
        for (int i = 0; i < mWalls.length; i++) {
            mNonNullHitRegions[i] = new Region();
        }

        playerSize = context.getResources().getDimensionPixelSize(R.dimen.player_visual_radius) * 2;

        setConfig(config);
    }

    @Override
    public void setConfig(Config config) {
        if (config == null) return;

        mConfig = config;

        // Calc all the size
        wallHeight = playerSize / 2;
        vertSpace = Math.min((int)mScreenHeight / 2, (int) (playerSize * (mConfig.vertSpacePlayerPct) / 100.0));
        gapSize = Math.min((int) (mScreenWidth * .8), (int) (playerSize * (mConfig.gapPlayerPct / 100.0)));
        mPaint.setColor(Color.BLUE);
        pxPerMs = mScreenHeight / mConfig.msToTravelScreen;

        mPaint.setColor(config.wallColor);

        mMode = config.mode;
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
    public void onTick(long deltaTime) {
        double dist = deltaTime * pxPerMs;

        if (Config.Mode.ascend == mMode) {
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

                int j = i + 1;
                while (mReportedHitRegions[i] != null) {
                    j++;
                }

                int gapStart = rand.nextInt((int) mScreenWidth - gapSize);

                mWalls[i] = new Rect(0, -wallHeight, gapStart, 0);
                mWalls[j] = new Rect(gapStart + gapSize, -wallHeight, (int) mScreenWidth, 0);
            }
        } else if (Config.Mode.descend == mMode) {
            int lowestY = (int) -1;

            for (int i = 0; i < mWalls.length; i++) {
                if (mWalls[i] != null) {
                    mWalls[i].top -= dist;
                    mWalls[i].bottom -= dist;

                    if (mWalls[i].bottom > lowestY) {
                        lowestY = mWalls[i].bottom;
                    }

                    if (mWalls[i].bottom < 0) {
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

            if (lowestY < (mScreenHeight - vertSpace)) {
                // Make a new gap

                int i = 0;
                while (mReportedHitRegions[i] != null) {
                    i++;
                }

                int j = i + 1;
                while (mReportedHitRegions[i] != null) {
                    j++;
                }

                int gapStart = rand.nextInt((int) mScreenWidth - gapSize);

                mWalls[i] = new Rect(0, (int) mScreenHeight, gapStart, (int) mScreenHeight + wallHeight);
                mWalls[j] = new Rect(gapStart + gapSize, (int) mScreenHeight, (int) mScreenWidth, (int) mScreenHeight + wallHeight);
            }
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
