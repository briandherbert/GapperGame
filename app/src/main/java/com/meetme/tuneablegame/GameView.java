package com.meetme.tuneablegame;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import com.meetme.tuneablegame.Gapper.GapperController;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    public static final String TAG = GameView.class.getSimpleName();

    public GameThread mGameThread;

    GameController mController;

    public GameView(Context context) {
        super(context, null);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        Log.v(TAG, "init");
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(TAG, "surface created, thread null " + (mGameThread == null));

        Visual.init(getWidth(), getHeight(), getContext());

        if (mGameThread == null) {
            mGameThread = new GameThread(getContext(), holder, getController());
        }

        GameUtils.init(getContext());

        mGameThread.setRunning(true);
        mGameThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(TAG, "surfaceDestroyed");

        if (mGameThread != null) {
            try {
                mGameThread.setRunning(false);
                mGameThread.interrupt();
                mGameThread = null;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGameThread.onTouch(event);
    }

    public GameController getController() {
        if (mController == null) mController = new GapperController();
        return mController;
    }
}