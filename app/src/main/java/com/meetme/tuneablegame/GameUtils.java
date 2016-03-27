package com.meetme.tuneablegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Vibrator;

/**
 * Created by bherbert on 3/26/16.
 */
public class GameUtils {
    static Vibrator vib;

    public static void init(Context context) {
        vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public static void vibrate(long ms) {
        if (vib == null) return;
        vib.vibrate(100);
    }

    public Bitmap initBitmap(Context context, int id) {
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), id);
        //bmp = Bitmap.createScaledBitmap(bmp, (int)(bmp.getWidth()*.25), (int)(bmp.getHeight()*.25), false);
        return bmp;
    }

    public Bitmap initBitmap(Context context, int id, int width, int height) {
        if (width <= 0)
            width = 1;
        if (height <= 0)
            height = 1;

        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), id);
        //bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmp = Bitmap.createScaledBitmap(bmp, width, height, false);
        return bmp;
    }

    public Bitmap initBitmapOpaque(Context context, int id, int width, int height) {
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), id);
        bmp = bmp.copy(Bitmap.Config.RGB_565, false);
        bmp = Bitmap.createScaledBitmap(bmp, width, height, false);

        return bmp;
    }

}
