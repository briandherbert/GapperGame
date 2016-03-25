package com.meetme.tuneablegame;

import android.content.Context;

/**
 * Created by bherbert on 3/25/16.
 */
public abstract class GameController {
    public abstract void init(Context context);
    public abstract Visual[] getVisuals();
    public abstract void onMove(int action, float x, float y);
    public abstract void onCollision(Visual visual1, int idx1, Visual visual2, int idx2);
}
