package com.meetme.tuneablegame.Gapper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Region;

import com.meetme.tuneablegame.Visual;

/**
 * Created by bherbert on 3/26/16.
 */
public class BackgroundVisual extends Visual {
    Paint mPaint = new Paint();

    static final int HIT_DURATION = 100;

    enum State {
        normal,
        hit;
    }

    long mTimeSinceStateChange = 0;

    State mState = State.normal;

    int mColor;

    public BackgroundVisual(Config config) {
        setConfig(config);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPaint(mPaint);
    }

    @Override
    public Region[] getHitRegions() {
        return new Region[0];
    }

    @Override
    public void onTick(long deltaTime) {
        mTimeSinceStateChange += deltaTime;

        switch (mState) {
            case hit:
                if (mTimeSinceStateChange >= HIT_DURATION) {
                    setState(State.normal);
                }

                break;

            case normal:
                break;
        }
    }

    public void hitWall() {
        setState(State.hit);
    }

    public void setConfig(Config config) {
        mColor = config.bgColor;

        if (State.normal == mState) mPaint.setColor(mColor);
    }

    void setState(State state) {
        if (mState == state || state == null) return;

        switch (state) {
            case hit:
                mPaint.setColor(Color.RED);
                break;

            case normal:
                mPaint.setColor(mColor);
                break;
        }

        mState = state;
        mTimeSinceStateChange = 0;
    }
}
