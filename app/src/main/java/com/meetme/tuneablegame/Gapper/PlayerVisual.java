package com.meetme.tuneablegame.Gapper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Region;
import android.util.Log;

import com.meetme.tuneablegame.R;
import com.meetme.tuneablegame.Visual;

/**
 * Created by bherbert on 3/24/16.
 */
public class PlayerVisual extends Visual {
    public static final String TAG = PlayerVisual.class.getSimpleName();

    static final int HIT_DURATION = 500;

    private Bitmap mBmp = null;

    enum State {
        normal(Color.GREEN),
        hit(Color.RED);

        State(int color) {
            this.color = color;
        }

        int color;
    }

    //PointF loc = new PointF();
    Region mHitRegion = new Region();
    Region[] mRegions = {mHitRegion};

    private int mRadius;
    int mBottomPadding;

    Paint mPaint = new Paint();

    State mState = State.normal;
    long mTimeSinceStateChange = 0;

    int y = 50;
    int x = 50;

    public PlayerVisual(Context context) {
        mPaint.setColor(State.normal.color);
        mPaint.setAntiAlias(true);

        Resources res = context.getResources();

        mBottomPadding = res.getDimensionPixelSize(R.dimen.player_visual_from_bottom);
        mRadius = res.getDimensionPixelSize(R.dimen.player_visual_radius);

        setConfig(Config.get());
    }

    @Override
    public void draw(Canvas canvas) {
        if (mBmp != null) {
            canvas.drawBitmap(mBmp, x - mRadius, y - mRadius, mPaint);
        } else {
            canvas.drawCircle(x, y, mRadius, mPaint);
        }
    }

    public void onMove(int action, float x, float y) {
        this.x = (int) x;
    }

    @Override
    public Region[] getHitRegions() {
        mHitRegion.set(x - mRadius, y - mRadius, x + mRadius, y + mRadius);
        return mRegions;
    }

    @Override
    public void onTick() {
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

    void setState(State state) {
        if (mState == state || state == null) return;

        mPaint.setColor(state.color);

        switch (state) {
            case hit:
                vibrate(100);

                break;

            case normal:
                break;
        }

        mState = state;
        mTimeSinceStateChange = 0;
    }

    public void hitWall() {
        setState(State.hit);
    }

    @Override
    public void setConfig(Config config) {
        switch (config.mode) {
            case ascend:
                y = (int) mScreenHeight - (mBottomPadding + mRadius);

                break;

            case descend:
                y = (int) mBottomPadding + mRadius;

                break;
        }

        mBmp = config.playerBitmap;
        Log.v(TAG, "y is " + y);
    }
}
