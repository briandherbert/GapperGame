package com.meetme.tuneablegame.Gapper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Region;

import com.meetme.tuneablegame.Visual;

/**
 * Created by bherbert on 3/25/16.
 */
public class StatusVisual extends Visual {
    Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    String msg = "";

    public StatusVisual() {
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(25);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawText("msg", 10, 300, mPaint);
    }

    @Override
    public Region[] getHitRegions() {
        return new Region[0];
    }

    @Override
    public void onTick() {

    }
}
