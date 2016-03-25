package com.meetme.tuneablegame;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    public static final String TAG = GameView.class.getSimpleName();

    public GameThread thread;
    Context context;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.context = context;
        //resume();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(TAG, "surface created, size ");

        if (thread == null) {
            resume();
        }

        thread.initGraphics(getWidth(), getHeight());
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //thread.setRunning(false);

        // TODO Auto-generated method stub
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return thread.onTouch(event);
    }

    public void pause() {
        if (thread != null) {
            try {
                thread.setRunning(false);
                thread.interrupt();
                thread = null;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void resume() {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        thread = new GameThread(context, holder);
    }
}