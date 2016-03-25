package com.meetme.tuneablegame.Gapper;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.meetme.tuneablegame.DuctTapeBackend;
import com.meetme.tuneablegame.GameController;
import com.meetme.tuneablegame.Visual;

/**
 * Created by bherbert on 3/25/16.
 */
public class GapperController extends GameController implements DuctTapeBackend.DownloadGoogleSpreadsheetDataListener {
    public static final String TAG = GapperController.class.getSimpleName();
    static final int MAX_VISUALS = 5;

    static final String GOOGLE_DOC_KEY = "1Vsyd5zvn12WdVO518-m1oin4zpqBgycTk7AFsLalcd0";

    Context mContext;

    StatusVisual mStatus;
    PlayerVisual mPlayer;
    WallsVisual mWalls;

    static final int STATUS_IDX = 0;
    static final int PLAYER_IDX = 1;
    static final int WALLS_IDX = 2;

    Visual[] mVisuals = new Visual[MAX_VISUALS];

    GapperHandler handler = new GapperHandler();

    WallsVisual.Config wallsConfig = new WallsVisual.Config();

    @Override
    public void onSpreadsheetDataLoaded(String csv) {
        try {
            Log.v(TAG, "Got spreadsheet data");
            mStatus.setMessage("got spreadsheet data");

            final String[][] rowsCols = DuctTapeBackend.parseCsvToRowColData(csv);

            wallsConfig = WallsVisual.Config.parse(rowsCols);

            Log.v(TAG, "speed is " + wallsConfig.msToTravelScreen);

            handler.sendEmptyMessage(GapperHandler.MSG_DOWNLOADED_CONFIG);


            return;
        } catch (Exception e) {
            Log.e(TAG, "Failed to download or parse", e);
            handler.sendEmptyMessage(GapperHandler.MSG_DOWNLOAD_FAILED);
        }
    }

    @Override
    public void onSpreadsheetDataFailed(String message) {
        handler.sendEmptyMessage(GapperHandler.MSG_DOWNLOAD_FAILED);

    }

    enum State {
        begin,
        downloading,
        loaded,
        failed
    }

    private State mState = State.begin;
    long lastStateChange = -1;

    @Override
    public void init(Context context) {
        mStatus = new StatusVisual();

        mPlayer = new PlayerVisual(context);

        mVisuals[STATUS_IDX] = mStatus;

        mContext = context;

        setState(State.downloading);
    }

    void setState(State state) {
        mStatus.setMessage("Setting state from " + mState + " to " + state);
        mState = state;

        switch (state) {
            case downloading:
                DuctTapeBackend.downloadGoogleSpreadsheetData(GOOGLE_DOC_KEY, this);
                break;

            case loaded:
                mWalls = new WallsVisual(mContext, wallsConfig);

                mVisuals[PLAYER_IDX] = mPlayer;
                mVisuals[WALLS_IDX] = mWalls;
                break;
        }

        lastStateChange = System.currentTimeMillis();
    }

    public void downloadConfig() {
        DuctTapeBackend.downloadGoogleSpreadsheetData(GOOGLE_DOC_KEY, this);
    };

    class GapperHandler extends Handler {
        public static final int MSG_DOWNLOADED_CONFIG = 1;
        public static final int MSG_DOWNLOAD_FAILED = 2;

        @Override
        public void handleMessage(Message msg) {
            Log.v(TAG, "Got message " + msg.what);
            switch (msg.what) {
                case MSG_DOWNLOADED_CONFIG:
                    if (mWalls != null) {
                        mWalls.setConfig(wallsConfig);
                        return;
                    }
                case MSG_DOWNLOAD_FAILED:
                    setState(State.loaded);
                    break;
            }
        }
    }

    @Override
    public Visual[] getVisuals() {
        return mVisuals;
    }

    @Override
    public void onMove(int action, float x, float y) {
        mPlayer.onMove(action, x, y);
    }

    @Override
    public void onCollision(Visual visual1, int idx1, Visual visual2, int idx2) {
        Class class1 = visual1.getClass();
        Class class2 = visual2.getClass();

        Class nonplayer = null;
        if (class1 == PlayerVisual.class) {
            nonplayer = class2;
        } else if (class2 == PlayerVisual.class) {
            nonplayer = class1;
        }

        if (nonplayer == null) return;

        if (WallsVisual.class == nonplayer) {
            mPlayer.hitWall();
        } else {

        }
    }
}
