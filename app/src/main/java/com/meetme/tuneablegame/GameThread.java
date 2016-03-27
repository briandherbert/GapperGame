package com.meetme.tuneablegame;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    static final String TAG = GameThread.class.getSimpleName();

    private SurfaceHolder mSurfaceHolder;
    private Canvas canvas = null;
    boolean mRun = false;

    public int frames = 0;
    public int msCounter = 0;
    public double fpsGranularity = 1000;
    double fps;

    public static final int IDEAL_FPS = 70;
    public int msPerFrame = 1000 / IDEAL_FPS;
    long lastTick = -1;

    long deltaTime = 0;

    public Paint textPaint;

    public GameController mController;

    boolean showFPS = true;

    public GameThread(Context context, SurfaceHolder holder, GameController controller) {
        mSurfaceHolder = holder;
        mController = controller;
        mController.init(context);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(25);
        textPaint.setColor(Color.GREEN);
        textPaint.setStyle(Paint.Style.FILL);

        frames = 0;
        fps = 10;
    }

    public void setRunning(boolean b) {
        mRun = b;
    }

    public void run() {
        lastTick = System.currentTimeMillis();

        while (mRun) {
            try {
                canvas = mSurfaceHolder.lockCanvas();
                synchronized (mSurfaceHolder) {
                    doPhysics();
                    doDraw();
                }
            } catch (Exception e) {
                Log.e(TAG, "Thread exception ", e);

                mSurfaceHolder.unlockCanvasAndPost(canvas);
                return;

            } finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (canvas != null) {
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

            try {
                if (deltaTime < msPerFrame) {
                    sleep(msPerFrame - deltaTime);
                }

            } catch (Exception e) {
                String err = (e.getMessage() == null) ? "Couldn't sleep" : e.getMessage();

                Log.v(TAG, "Can't sleep" + err);
            }

            deltaTime = System.currentTimeMillis() - lastTick;
            lastTick = System.currentTimeMillis();
        }

        Log.v(TAG, "Thread finished");
    }

    public boolean onTouch(MotionEvent event) {
        synchronized (mSurfaceHolder) {
            mController.onTouch(event.getAction(), event.getRawX(), event.getRawY());
        }

        return true;
    }

    public void doPhysics() {
        if (showFPS) {
            msCounter += deltaTime;

            frames++;

            if (msCounter >= fpsGranularity) {
                msCounter = (int) (msCounter - fpsGranularity);
                fps = frames;
                frames = 0;
            }
        }

        // Update items
        mController.onTick(deltaTime);
        mController.detectCollisions();
    }

    public void doDraw() {
        if (canvas == null) return;

        mController.doDraw(canvas);

        if (showFPS) {
            if (deltaTime > 0) {
                canvas.drawText("FPS " + fps, 10, 50, textPaint);
            }
        }
    }
}
