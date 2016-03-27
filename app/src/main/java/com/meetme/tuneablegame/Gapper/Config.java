package com.meetme.tuneablegame.Gapper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.meetme.tuneablegame.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by bherbert on 3/26/16.
 */
public class Config {
    public static final String TAG = Config.class.getSimpleName();

    static final Config sConfig = new Config();

    public static final int COL_SPEED = 0;
    public static final int COL_VERT_SPACE = 1;
    public static final int COL_GAP = 2;
    public static final int COL_WALL_COLOR = 3;
    public static final int COL_BGCOLOR = 4;
    public static final int COL_MODE = 5;
    public static final int COL_PLAYER_URL = 6;

    // Walls
    public static long msToTravelScreen = 2000;
    public static int vertSpacePlayerPct = 300;
    public static int gapPlayerPct = 300;
    public static int wallColor = Color.BLACK;
    public static Bitmap playerBitmap = null;

    // Background
    public static int bgColor = Color.WHITE;

    enum Mode {
        ascend,
        descend;
    }

    public static Mode mode = Mode.ascend;

    public static Config get() {
        return sConfig;
    }

    public static Config parse(String[][] rowsCols, Context context) {
        Config config = new Config();

        try {
            sConfig.msToTravelScreen = Integer.valueOf(rowsCols[1][COL_SPEED]);
            sConfig.vertSpacePlayerPct = Integer.valueOf(rowsCols[1][COL_VERT_SPACE]);
            sConfig.gapPlayerPct = Integer.valueOf(rowsCols[1][COL_GAP]);

            try {
                sConfig.wallColor = Color.parseColor(rowsCols[1][sConfig.COL_WALL_COLOR]);
            } catch (Exception e) {

            }

            try {
                sConfig.bgColor = Color.parseColor(rowsCols[1][sConfig.COL_BGCOLOR]);
            } catch (Exception e) {

            }

            sConfig.mode = Mode.valueOf(rowsCols[1][sConfig.COL_MODE]);
        } catch (Exception e) {

        }


        try {
            int playerSize = context.getResources().getDimensionPixelSize(R.dimen.player_visual_radius) * 2;
            playerBitmap = Picasso.with(context).load(rowsCols[1][sConfig.COL_PLAYER_URL]).resize(playerSize, playerSize).get();
        } catch (Exception e) {
            playerBitmap = null;
            e.printStackTrace();
        }

        Log.v(TAG, "Parsed " + sConfig.toString());
        return config;
    }

    public String toString() {
        return "Config\n"
                + " msToTravelScreen: " + msToTravelScreen + "\n"
                + " vertSpacePlayerPct: " + vertSpacePlayerPct + "\n"
                + " gapPlayerPct: " + gapPlayerPct + "\n"
                + " color: " + wallColor + "\n";
    }
}
