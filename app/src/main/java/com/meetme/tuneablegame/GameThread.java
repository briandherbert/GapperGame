package com.meetme.tuneablegame;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Region;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.meetme.tuneablegame.Gapper.GapperController;


public class GameThread extends Thread {
    public final String ID = "RSGAME";
    boolean log = true;
    private Context mContext;
    private SurfaceHolder mSurfaceHolder;
    private Canvas canvas = null;
    boolean mRun = false;

    public boolean isTouching = false;

    public long startTime;
    public long lastTime;
    public long elapsedTime;

    public static final int GAME_DURATION = 60000;
    public int frames;
    public int msCounter;
    public double fpsGranularity = 1000;
    double fps;

    public static final int IDEAL_FPS = 70;
    public int msPerFrame = 1000 / IDEAL_FPS;
    long lastTick;
    long tickTime = 0;

    public boolean isPhysicsStarted;
    public boolean isIngamePaused = false;

    Paint bgPaint = new Paint();
    public Paint textPaint;

    float userY;
    float userX;

    int mAction;

    int currentHighScore = 0;
    public static final String PREF_NAME = "savedPrefs";
    public static final String PREF_HIGH_SCORE = "highScore";
    public static final String PREF_SOUND = "sound";
    public static final String PREF_FX = "fx";

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public GameController mController = new GapperController();

    public GameThread(Context context, SurfaceHolder holder) {
        mContext = context;
        mSurfaceHolder = holder;

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(25);
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL);

        bgPaint.setColor(Color.WHITE);

        isPhysicsStarted = false;
        frames = 0;
        elapsedTime = 0;
        fps = 10;

        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = prefs.edit();
        int tempHs = prefs.getInt(PREF_HIGH_SCORE, -1);

        if (tempHs != -1)
            currentHighScore = tempHs;
    }

    public void initGraphics(int uScreenWidth, int uScreenHeight) {
        if (log) Log.d(ID, "initGraphics");

        if (log) Log.d(ID, "Initialized graphics");

        Visual.init(uScreenWidth, uScreenHeight, mContext);
        lastTick = System.currentTimeMillis();

        // Add all visuals
        mController.init(mContext);
    }


    public void setRunning(boolean b) {
        mRun = b;
    }


    public void run() {
        while (mRun) {
            canvas = null;

            try {
                canvas = mSurfaceHolder.lockCanvas();
                synchronized (mSurfaceHolder) {
                    if (!isIngamePaused) {
                        doPhysics();
                    }

                    doDraw();
                }
            } catch (Exception e) {
                System.out.println("Error " + e);

                e.printStackTrace();
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
            tickTime = System.currentTimeMillis() - lastTick;

            try {
                if (tickTime < msPerFrame) {
                    sleep(msPerFrame - tickTime);
                }

            } catch (Exception e) {
                String err = (e.getMessage() == null) ? "Couldn't sleep" : e.getMessage();

                Log.v(ID, err);
            }
            lastTick = System.currentTimeMillis();
        }

    }

    public void startGame() {
        isIngamePaused = false;
        userY = 0;
        userX = 0;
        elapsedTime = -1;
    }

    public void endGame() {
        if (Visual.dist > currentHighScore) {
            editor.putInt(PREF_HIGH_SCORE, (int) Visual.dist); // value to store
            editor.commit();
        }
    }

    public boolean onTouch(MotionEvent event) {
        mAction = event.getAction();

        synchronized (mSurfaceHolder) {
            userX = event.getRawX();
            userY = event.getRawY();

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                isTouching = true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                isTouching = false;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            }

            mController.onMove(mAction, userX, userY);
        }
        return true;
    }

    public void doPhysics() {
        if (!isPhysicsStarted) {
            Visual.deltaTime = 0;
            lastTime = System.currentTimeMillis();
            startTime = lastTime;
            isPhysicsStarted = true;
            return;
        }

        Visual.deltaTime = System.currentTimeMillis() - lastTime;
        lastTime = System.currentTimeMillis();

        msCounter += Visual.deltaTime;

        frames++;

        if (msCounter >= fpsGranularity) {
            msCounter = (int) (msCounter - fpsGranularity);
            fps = frames;
            frames = 0;
        }

        // Update item physics
        for (Visual visual :  mController.getVisuals()) {
            if (visual != null) visual.onTick();
        }

        int numVisuals = mController.getVisuals().length;

        //check for collisions with items
        for (int i = 0; i < numVisuals; i++) {
            int iRegionIdx = -1;

            iVisual = mController.getVisuals()[i];
            if (iVisual == null) continue;

            for (Region region : iVisual.getHitRegions()) {
                iRegionIdx++;
                if (region == null) continue;

                for (int j = i + 1; j < numVisuals; j++) {
                    int jRegionIdx = -1;

                    jVisual = mController.getVisuals()[j];
                    if (jVisual == null) continue;

                    for (Region jRegion : jVisual.getHitRegions()) {
                        jRegionIdx++;
                        if (jRegion == null) continue;

                        if (!region.quickReject(jRegion) && region.op(jRegion, Region.Op.INTERSECT)) {
                            mController.onCollision(iVisual, iRegionIdx, jVisual, jRegionIdx);
                        }
                    }
                }
            }
        }
    }

    Visual iVisual, jVisual;
    Region iRegion, jRegion;

    public void pauseMenu() {
        isIngamePaused = true;
    }


    public void resumeFromMenu() {
        lastTime = System.currentTimeMillis();
        isIngamePaused = false;
    }

    public void doDraw() {
        if (canvas == null) return;

        canvas.drawPaint(bgPaint);
        for (Visual visual :  mController.getVisuals()) {
            if (visual != null) visual.draw(canvas);
        }

        if (log) {

            if (tickTime > 0) {
                canvas.drawText("fps " + fps, 10, 100, textPaint);
            }
//		canvas.drawText("Dist, delta" + (int)Visual.dist + " " + Visual.deltaDist, 10, 30, paint);
//		//canvas.drawText("Dist " + Visual.dist, 10, 30, paintStroke);
//		canvas.drawText("Speed " + s.velocity, 10, 60, paint);
//		canvas.drawText("Time " + Visual.countdown, 10, 90, paint);
//		canvas.drawText("Time " + msCounter, 200, 90, paint);
//
//		canvas.drawText("Light state " + stoplight.state, 10, 150, paint);
//		canvas.drawText("Squirrel state " + s.STATE_NAMES[s.state], 10, 180, paint);
//		//canvas.drawText("Frames " + frames, 10, 210, paint);
//		canvas.drawText("Is touching " + isTouching, 10, 240, paint);
//		canvas.drawText("nut idx " + items.nutIdx, 10, 270, paint);
        }
    }


}
